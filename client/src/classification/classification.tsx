import React, {Component} from 'react'
import { IFrame, IMessage} from '@stomp/stompjs';
import {Form, FormGroup, Input, Container, Row, Col, Button} from 'reactstrap'
import CCPanel from './classificationconfigpanel';

import SuccessModal from '../globalcomponents/glbsuccessmodal';
import ErrorModal from '../globalcomponents/glberrormodal';
import LoadingScreenOverlay from '../globalcomponents/glbloadingoverlay';
import WebcamCapture from '../globalcomponents/webcam';
import WebsocketService from '../globalcomponents/glbwebsocket';
import OnOffmqttsPanel from "../globalcomponents/mqtt/onoffsignals"
import DL4JBackend from "../globalcomponents/glbbackend"
import "./classification.css";

/**
 * Data of classifer that can be configured
 * name: Name (filename) of classifier
 * inputwidth: Webcam's screenshot width (must match with the input width of classifier) 
 * inputheight: Webcam's screenshot height (must match with the input height of classifier)
 * classnum: Number of output (= Number of classes of classifier) (CANNOT be configured)
 * labels: Label (name) of each classes
*/
interface ClassifierData {
    name: string; 
    inputwidth: number;
    inputheight: number;
    classnum: number;
    labels: string[]; 
}

/**
 * Classification props
*/
interface ClassificationProps {

}

/**
 * Classification states
 * websocketconnected: If client is connected to server's websocket
 * backendiscpu: If server (DL4J) is using CPU as backend (FALSE if not connected to server's websocket)
 * backendisgpu: If server (DL4J) is using GPU as backend (FALSE if not connected to server's websocket)
 * streaming: If client is streaming webcam's data to server
 * classifierloaded: If classifier (model) is loaded at server side
 * classifierpath: Path to classifier (model) file
 * classname: Classname (classification's result) of current frame
 * classifierdata: Classifier's data
 * lsoverlayactive: If loading screen overlay is active (visible)
 * ccpanelactive: If Classification Configuration Panel is active (visible)
 *  
*/
interface ClassificationStates {
    websocketconnected: boolean;
    backendiscpu: boolean;
    backendisgpu: boolean;
    streaming: boolean;
    classifierloaded: boolean;
    classifierpath: string;
    classname: string;
    classifierdata: ClassifierData;
    lsoverlayactive: boolean;
    ccpanelactive: boolean;
}

export default class Classification extends Component <ClassificationProps, ClassificationStates> {
    /**
     * Classification class properties
     * successmodalref: Success modal's ref
     * errormodalref: Error modal's ref
     * webcamref: Webcam's ref
     * stompwebsocket: WebsocketService class
     */
    successmodalref: React.RefObject<any>;
    errormodalref: React.RefObject<any>;
    webcamref: React.RefObject<any>;
    stompwebsocket: WebsocketService;

    constructor(props: ClassificationProps) {
        super(props);
        this.state = { 
            websocketconnected: false,
            backendiscpu: false,
            backendisgpu: false,
            streaming: false,
            classifierloaded: false,
            classifierpath: "D://CNNData/Modals/Testmodel.zip",
            classname: "-",
            classifierdata: { name: "", inputwidth: 200, inputheight: 200, classnum: 0, labels: [], },
            lsoverlayactive: false,
            ccpanelactive: false,
        }
        this.successmodalref = React.createRef();
        this.errormodalref = React.createRef();
        this.webcamref = React.createRef();

        // Initialize websocket and set callbacks (onConnect/onDisconnect/onClose/onError)
        this.stompwebsocket = new WebsocketService("ws://localhost:8081/stomp", 60000);
        this.stompwebsocket.setonConnectCallback(this.websocketonConnect);
        this.stompwebsocket.setonDisconnectCallback(this.websocketonDisconnect);
        this.stompwebsocket.setonWebSocketCloseCallback(this.websocketonClose);
        this.stompwebsocket.setonWebsocketErrorCallback(this.websocketonError);
        // Connect to server's websocket
        this.stompwebsocket.connect();
    }

    /**
     * [COMPONENT LIFECYCLE]
     * Called when classification component is unmounted (when Classification page is closed)
     * 1. Remove all websocket callbacks 
     * 2. Unsubscribe from all topics 
     * 3. Disconnect
    */
    componentWillUnmount = () : void => {
        this.stompwebsocket.removeonConnectCallback();
        this.stompwebsocket.removeonDisconnectCallback();
        this.stompwebsocket.removeonWebSocketCloseCallback();
        this.stompwebsocket.removeonWebsocketErrorCallback();
        if (this.stompwebsocket.isConnected()) {
            this.stompwebsocket.unsubscribe("/response/backend");
            this.stompwebsocket.unsubscribe("/response/classification/modelchanged");
            this.stompwebsocket.unsubscribe("/response/classification/result");
            this.stompwebsocket.unsubscribe("/response/classification/error");
        }
        this.stompwebsocket.disconnect();
    }

    /**
     * [WEBSOCKET CALLBACK]
     *  Called when client is connected to server
     * 1. Set websocketconnected (state) to TRUE
     * 2. Subscribe to topics
     * 3. Get the backend used by server (DL4J)
     * @param frame 
    */
    websocketonConnect = (frame: IFrame) : void => {
        this.setwebsocketconnected(true);
        this.stompwebsocket.subscribe("/response/backend", this.getbackendCallback);
        this.stompwebsocket.subscribe("/response/classification/modelchanged", this.modelchangedcallback);
        this.stompwebsocket.subscribe("/response/classification/result", this.processclassificationresult);
        this.stompwebsocket.subscribe("/response/classification/error", this.processclassificationerror);
        this.stompwebsocket.sendmessage("/server/getbackend", "");
    }

    /**
     * [WEBSOCKET CALLBACK]
     * Called when client is disconnected from websocket
     * 1. If websocketconnected (state) is TRUE, set to FALSE
     * @param frame 
    */
    websocketonDisconnect = (frame: IFrame) : void => {
        if (this.state.websocketconnected)
            this.setwebsocketconnected(false);
    }

    /**
     * [WEBSOCKET CALLBACK]
     * Called when websocket is closed
     * 1. If websocketconnected (state) is TRUE, set to FALSE
     * @param event 
    */
    websocketonClose = (event: CloseEvent) : void => {
        if (this.state.websocketconnected)
            this.setwebsocketconnected(false);
    }

    /**
     * [WEBSOCKET CALLBACK]
     * Called when websocket encounters error
     * 1. If websocketconnected (state) is TRUE, set to FALSE
     * @param event 
    */
    websocketonError = (event: Event) : void => {
        if (this.state.websocketconnected)
            this.setwebsocketconnected(false);
    }

    /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK]
     * Get the backend used by server (DL4J) in server's response
     * 1. If GPU is used, set backendisgpu to TRUE and backendiscpu to FALSE
     * 2. If CPU is used, set backendiscpu to TRUE and backendisgpu to FALSE
     * @param response 
    */
    getbackendCallback = (response: IMessage) : void => {
        let backendused = response.body;
        if (backendused === "gpu")
            this.setState({ backendiscpu: false, backendisgpu: true });
        else
            this.setState({ backendiscpu: true, backendisgpu: false });
    }

    /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK]
     * Called when classifier is loaded successfully at server side
     * 1. Close (hide) loading screen overlay and set classifierloaded (state) to TRUE
     * 2. Get the number of classes (output layer's size of classifier) from server's response
     * 3. Initialize default class name (labels)
     * 4. Open success modal
     * @param response 
    */
    modelchangedcallback = (response: IMessage) : void => {
        this.setlsoverlayactive(false);
        this.setclassifierloaded(true);
        let data = JSON.parse(response.body);

        let newlabels: string[] = []
        for(let index = 0; index < data.classnum; index++)
            newlabels.push("CLASS-" + (index + 1));
        this.setState({
            classifierdata: 
            { 
                ...this.state.classifierdata, 
                name: data.filename,
                classnum: data.classnum,
                labels: newlabels,
            }
        })
        this.successmodalref.current.openmodal("CLASSIFIER HAS BEEN LOADED SUCCESSFULLY (CLASS NUM: " + data.classnum + ")");
    }

    /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK]
     * Called when server returns the result of classification
     * 1. Get the result (in index) of classification
     * 2. Set the classname (state)
     * @param response 
    */
    processclassificationresult = (response: IMessage) : void => {
        let data = JSON.parse(response.body);
        let result = data.result;
        this.setclassname(this.state.classifierdata.labels[result]);
    }

    /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK] 
     * Called when there is an exception in server's side
     * 1. Close loading screen overlay (in case it is opened)
     * 2. Stop webcam's streaming (if client is streaming)
     * 3. Get the exception message (reason) from server's response
     * 4. Open error modal
     * @param response 
    */
    processclassificationerror = (response: IMessage) : void => {
        this.setlsoverlayactive(false);
        if (this.state.streaming) {
            this.stopstreaming();
        }
        let data = JSON.parse(response.body);
        this.errormodalref.current.openmodal("OOPS", data.message);
    }

    /**
     * [UPDATE COMPONENT'S STATE] - websocketconnected
     * @param status 
    */
    setwebsocketconnected = (status: boolean) : void => {
        this.setState({ websocketconnected: status });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - streaming
     * @param streaming 
    */
    setstreaming = (streaming: boolean) : void => {
        this.setState({ streaming });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - classifierloaded
     * @param classifierloaded 
     */
    setclassifierloaded = (classifierloaded: boolean) : void => {
        this.setState({ classifierloaded });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - classifierdata
     * @param classifierdata 
    */
    setclassifierdata = (classifierdata: ClassifierData) : void => {
        this.setState({ classifierdata });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - classname
     * @param classname 
     */
    setclassname = (classname: string) : void => {
        this.setState({ classname });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - lsoverlayactive
     * @param lsoverlayactive 
     */
    // [UPDATE STATE] - lsoverlayactive
    setlsoverlayactive = (lsoverlayactive: boolean) : void => {
        this.setState({ lsoverlayactive });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - ccpanelactive
     * @param ccpanelactive 
     */
    setccpanelactive = (ccpanelactive: boolean) : void => {
        this.setState({ ccpanelactive });
    }


    /**
     * Called when classifier path value on change
     * @param event 
    */
    handleclassifierpathOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setState({ classifierpath: event.target.value });
    }

    /**
     * Check if webcam is available for streaming
     * Condition 1 - Classifier is loaded
     * Condition 2 - Client is connected to server's websocket
     * @returns TRUE if webcam is available for streaming, else FALSE
    */
    canstream = () : boolean => {
        return this.state.websocketconnected && this.state.classifierloaded;
    }

    /**
     * Called when UPDATE button is clicked
     * Function: Load new classifier from server's side
     * 1. Open (show) loading screen overlay
     * 2. Send classifier data (path of file) to server
    */
    updateclassifier = () : void => {
        this.setlsoverlayactive(true);
        this.stompwebsocket.sendmessage("/server/classification/modelchanged", JSON.stringify({ path: this.state.classifierpath }));
    }

    /**
     * Callback function for webcam streaming
     * Send webcam data (image/frame in base64 encoded string) to server for classification
     * @param encodedimage - Image/frame in base64 encoded string
    */
    webcamstreamingcallback = (encodedimage: string) : void => {
        let trimmedimage = encodedimage.split(",")[1];
        this.stompwebsocket.sendmessage("/server/classification/classify", JSON.stringify({ base64encodedimage: trimmedimage }));
    }

    /**
     * Called when START (STREAMING) button is clicked or triggered
     * 1. Called startstreaming function (of webcam component) - screenshot every 1000ms
     * 2. Set streaming (state) to TRUE
    */
    startstreaming = () : void => {
        if (this.webcamref.current === null) return;
        if (this.webcamref.current.available()) {
            this.webcamref.current.startstreaming(1000);
            this.setstreaming(true);
        }
    }

    /**
     * Called when STOP (STREAMING) button is clicked or triggered
     * 1. Called stopstreaming function (of webcam component)
     * 2. Set streaming (state) to FALSE
    */
    stopstreaming = () : void => {
        if (this.webcamref.current === null) return;
        this.webcamref.current.stopstreaming();
        this.setstreaming(false);
    }

    /**
     *  Called when CONFIGURE button is clicked
     * 1. Set ccpanelactive to TRUE => open configuration panel (CCP)
    */
    configureOnclick = () : void => {
        this.setccpanelactive(true);
    }

    /**
     * Callback function for configuration panel
     * Function: Update classifier's data 
     * @param data 
    */
    updatemodeldata = (data: ClassifierData) : void => {
        this.setclassifierdata(data);
    }

    /**
     * [MQTT CALLBACK]
     * Called when received START STREAMING signal from MQTT broker
     * 1. If webcam is available for streaming AND client current not streaming -> Trigger START STREAMING button
    */
    startstreamingsignalreceivedcallback = () : void => {
        console.log("[MQTT SIGNAL] START STREAMING");
        if (! this.canstream() || this.state.streaming)
            console.log("Streaming is unavailable. Reason: Client is not connected to server OR classifier is not loaded OR it is already in streaming!");
        else
            this.startstreaming();
    }

    /**
     * [MQTT CALLBACK]
     * Called when received STOP STREAMING signal from MQTT broker
     * 1. If webcam is available for streaming AND client is current streaming -> Trigger STOP STREAMING button
    */
    stopstreamingsignalreceivedcallback = () : void => {
        console.log("[MQTT SIGNAL] STOP STREAMING");
        if (! this.canstream() || ! this.state.streaming)
            console.log("Stop streaming is unavailable. Reason: Client is not connected to server OR classifier is not loaded OR webcam is current not streaming!");
        else
            this.stopstreaming();
    }


    render = () => {
        return (
            <>
                <SuccessModal ref={this.successmodalref} />
                <ErrorModal ref={this.errormodalref} />
                <LoadingScreenOverlay active={this.state.lsoverlayactive}/>
                <CCPanel 
                    active={this.state.ccpanelactive} 
                    updatemodeldata={this.updatemodeldata} 
                    closeccpanel={() => this.setccpanelactive(false)}
                    databeforemodification={this.state.classifierdata}
                />

                <div className='classification-configuration-panel'>
                    <Form>
                        <div className='pair'>
                            <div className='description'>DL4J BACKEND</div>
                            <DL4JBackend 
                                websocketconnected={this.state.websocketconnected}
                                backendiscpu={this.state.backendiscpu}
                                backendisgpu={this.state.backendisgpu}
                            />
                        </div>

                        <div className='pair'>
                            <div className='description'>PATH TO CLASSIFIER</div>
                            <FormGroup>
                                <Input type='text' name='classifierpath'  value={this.state.classifierpath} onChange={this.handleclassifierpathOnchange}></Input>
                            </FormGroup>
                            <Container>
                                <Row>
                                    <Col><Button block color='primary' disabled={! this.state.websocketconnected} onClick={this.updateclassifier}>UPDATE</Button></Col>
                                    <Col><Button block color='info' disabled={! this.state.classifierloaded} onClick={this.configureOnclick}>CONFIGURE</Button></Col>
                                </Row>
                            </Container>
                        </div>

                        <div className='pair'>
                            <div className='description'>STREAMING</div>
                            <Container>
                                <Row>
                                    <Col><Button block color='primary' disabled={! this.canstream() || this.state.streaming} onClick={this.startstreaming}>START</Button></Col>
                                    <Col><Button block color='danger' disabled={! this.canstream() || ! this.state.streaming} onClick={this.stopstreaming}>STOP</Button></Col>
                                </Row>
                                <OnOffmqttsPanel
                                    canconnect={true}
                                    startsignalcallback={this.startstreamingsignalreceivedcallback}
                                    stopsignalcallback={this.stopstreamingsignalreceivedcallback}
                                />
                            </Container>
                        </div>
                    </Form>
                </div>

                <div className='classification-main-content-container'>
                    <div>
                        <WebcamCapture
                            webcamwidth={500}
                            webcamheight={500}
                            screenshotwidth={this.state.classifierdata.inputwidth}
                            screenshotheight={this.state.classifierdata.inputheight}
                            streamingcallback={this.webcamstreamingcallback}
                            ref={this.webcamref}
                        />
                        <div className="result-div">
                            {this.state.classname}
                        </div>
                    </div>
                </div>
            </>

        )
    }
}