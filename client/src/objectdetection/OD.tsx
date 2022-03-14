import { IFrame, IMessage } from '@stomp/stompjs';
import React, {Component} from 'react';
import { Form, FormGroup, Input, Button, Container, Row, Col} from 'reactstrap';
import { CloseEvent } from 'sockjs-client';
import WebsocketService from '../globalcomponents/glbwebsocket';
import "./OD.css";

import ErrorModal from '../globalcomponents/glberrormodal';
import SuccessModal from '../globalcomponents/glbsuccessmodal';
import LoadingScreenOverlay from '../globalcomponents/glbloadingoverlay';
import WebcamCapture from "../globalcomponents/webcam";
import OnOffmqttsPanel from "../globalcomponents/mqtt/onoffsignals";
import DataOutputPanel from "../globalcomponents/mqtt/dataoutputpanel";
import DL4JBackend from '../globalcomponents/glbbackend';

/**
 * Object Detection (OD) props
*/
interface ODProps {
    
}

/**
 * Object Detection (OD) states
 * websocketconnected: If client is connected to server's websocket
 * backendiscpu: If server (DL4J) is using CPU as backend (FALSE if not connected to server's websocket)
 * backendisgpu: If server (DL4J) is using GPU as backend (FALSE if not connected to server's websocket)
 * streaming: If client is streaming webcam's data to server
 * recording: If client is recording (canvas)
 * logging: If server is logging detected object into text file (** NOTE THAT logging textfile will be saved to C://DL4JRA/Logging folder)
 * modelname: Name of model which is currently being used for detection
 * screenshotdimension: Screenshot dimension (width & height) for webcam
 * recordedchunks: Array to store frames -> Convert to video
 * lsoverlayactive: If loading screen overlay is active
*/
interface ODStates {
    websocketconnected: boolean;
    backendiscpu: boolean;
    backendisgpu: boolean;
    streaming: boolean;
    recording: boolean;
    logging: boolean;
    modelname: string;
    screenshotdimension: number;
    recordedchunks: Blob[];
    lsoverlayactive: boolean;
    classifierpath: string;
    traindatasetpath: string;
    testdatasetpath: string;
    load: boolean;
    import: boolean;
}

export default class ObjectDetection extends Component <ODProps, ODStates> {
    /**
     * OD class properties
     * stompwebsocket: WebsocketService class
     * webcamref: Webcam's ref
     * successmodalref: Success modal's ref
     * errormodalref: Error modal's ref
     * canvasref: Canvas' ref
     * webcamoutputref: MQTT output's ref
     * context2d: Context2D of canvas
     * recorder: MediaRecorder object (for recording)
    */
    stompwebsocket: WebsocketService;
    webcamref : React.RefObject<any>;
    successmodalref: React.RefObject<any>;
    errormodalref : React.RefObject<any>;
    canvasref : React.RefObject<any>;
    webcamoutputref: React.RefObject<any>;
    context2d: any;
    recorder: null | MediaRecorder;

    constructor(props: ODProps) {
        super(props);
        this.state = {
            websocketconnected: false,
            backendiscpu: false,
            backendisgpu: false,
            streaming: false,
            recording: false,
            logging: false,
            modelname: "",
            recordedchunks: [],
            screenshotdimension: 416,
            lsoverlayactive: false,
            classifierpath: "E:\\SHRDC\\models\\TINYYOLO.zip",
            traindatasetpath: "",
            testdatasetpath: "",
            load: false,
            import: false,
        }
        this.webcamref = React.createRef();
        this.successmodalref = React.createRef();
        this.errormodalref = React.createRef();
        this.canvasref = React.createRef();
        this.webcamoutputref = React.createRef();
        this.recorder = null;

        // Initialize websocket and set callbacks (onConnect/onDisconnect/onClose/onError)
        this.stompwebsocket = new WebsocketService("ws://localhost:8081/stomp");
        this.stompwebsocket.setonConnectCallback(this.websocketonConnect);
        this.stompwebsocket.setonDisconnectCallback(this.websocketonDisconnect);
        this.stompwebsocket.setonWebSocketCloseCallback(this.websocketonClose);
        this.stompwebsocket.setonWebsocketErrorCallback(this.websocketonError);
        // Connect to server's websocket
        this.stompwebsocket.connect();
    }

    /**
     * Redraw canvas to plain color (Gray)
     * Indicates that there is no live stream from server
    */
    resetcanvasbackground = () : void => {
        this.context2d.fillStyle = '#626262';
        this.context2d.fillRect(0, 0, 500, 500);
    }

    /**
     * [COMPONENT LIFECYCLE]
     * Called when OD component is mounted
     * 1. Get canvas' context2d property
    */
    componentDidMount = () : void => { 
        if (this.canvasref.current) {
            this.context2d = this.canvasref.current.getContext("2d");
            this.resetcanvasbackground();
        }
    }

    /**
     * [COMPONENT LIFECYCLE]
     * Called before OD component is unmounted (when OD page is closed)
     * 1. Shut down all processes
     * 2. Remove all websocket callbacks
     * 3. Unsubscribe from all topics
     * 4. Disconnect
    */
    componentWillUnmount = () : void => {
        this.shutdownprocesses();
        this.stompwebsocket.removeonConnectCallback();
        this.stompwebsocket.removeonDisconnectCallback();
        this.stompwebsocket.removeonWebSocketCloseCallback();
        this.stompwebsocket.removeonWebsocketErrorCallback();
        if (this.stompwebsocket.isConnected()) {
            this.stompwebsocket.unsubscribe("/response/backend");
            this.stompwebsocket.unsubscribe("/response/objectdetection/modelchanged");
            this.stompwebsocket.unsubscribe("/response/objectdetection/processedimage");
            this.stompwebsocket.unsubscribe("/response/objectdetection/error");
        }
        this.stompwebsocket.disconnect();
    }

    /**
     * [WEBSOCKET CALLBACK]
     * Called when websocket is connected to server
     * 1. Set websocketconnected (state) to TRUE
     * 2. Subscribe to topics
     * "/response/backend" - Server responds with the backend used
     * "/response/objectdetection/modelchanged" - When model is successfully updated
     * "/response/objectdetection/processedimage" - When server process (detect and draw bounding boxes) image and return
     * "/response/objectdetection/error" - When server encounters an exception during detection
     * 3. Get the backend used by server (DL4J)
     * @param frame 
    */
    websocketonConnect = (frame: IFrame) : void => {
        this.setwebsocketconnected(true);
        this.stompwebsocket.subscribe("/response/backend", this.getbackendCallback);
        this.stompwebsocket.subscribe("/response/objectdetection/modelchanged", this.modelchangedcallback);
        this.stompwebsocket.subscribe("/response/objectdetection/loadmodel", this.modelchangedcallback);
        this.stompwebsocket.subscribe("/response/objectdetection/processedimage", this.processedimagecallback);
        this.stompwebsocket.subscribe("/response/objectdetection/error", this.servererrorcallback);
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
     * Called when server sends back processed (detect & draw) image
     * 1. Get image data from server's response (base64 encoded)
     * 2. Render image on canvas
     * 3. Send image data to MQTT output
     * @param data 
    */
    processedimagecallback = (data: IMessage) : void => {
        var processedimage = JSON.parse(data.body);
        let image = new Image();
        image.onload = () => { 
            if (this.context2d !== null || this.context2d !== undefined)
                this.context2d.drawImage(image, 0, 0);
        }
        if (this.webcamoutputref.current)
            this.webcamoutputref.current.sendmessage(processedimage.base64encodedstring);
        image.src = processedimage.base64encodedstring;
    }

    /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK]
     * Called when there is an exception during detection at server side
     * 1. Get error message (reason) from server's response
     * 2. Open error modal
     * 3. Shut down all processes
     * @param response 
    */
    servererrorcallback = (response: IMessage) : void => {
        let data = JSON.parse(response.body);
        this.errormodalref.current.openmodal("OOPS", data.message);
        this.shutdownprocesses();
    }

    /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK]
     * Called when server successfully updates the object detection model
     * 1. Get model data (name & input dimension) from server's response
     * 2. Open success modal
     * 3. Update modelname (state) and screenshotdimension (state)
     * @param response 
    */
    modelchangedcallback = (response: IMessage) : void => {
        this.setlsoverlayactive(false);
        let data = JSON.parse(response.body);
        let modelname = data.name;
        let screenshotdimension = Number(data.screenshotdimension);
        this.successmodalref.current.openmodal("Detection modal has been updated successfully. Name: " + modelname + ", screenshot dimension: " + screenshotdimension);
        this.setState({ modelname, screenshotdimension });
    }

        /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK]
     * Called when server successfully updates the object detection model
     * 1. Get model data (name & input dimension) from server's response
     * 2. Open success modal
     * 3. Update modelname (state) and screenshotdimension (state)
     * @param response 
    */
         loadmodelcallback = (response: IMessage) : void => {
            this.setlsoverlayactive(false);
            let data = JSON.parse(response.body);
            let modelname = "tinyyolo";
            let screenshotdimension = Number(data.screenshotdimension);
            this.successmodalref.current.openmodal("Detection modal has been updated successfully. Name: " + modelname + ", screenshot dimension: " + screenshotdimension);
            this.setState({ modelname, screenshotdimension });
        }


    /**
     * [UPDATE COMPONENT'S STATE] - websocketconnected
     * @param status 
    */
    setwebsocketconnected = (websocketconnected: boolean) : void => {
        this.setState( { websocketconnected });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - streaming
     * @param streaming 
    */
    setstreaming = (streaming: boolean) => {
        this.setState({ streaming });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - recording
     * @param recording 
    */
    setrecording = (recording: boolean) : void => {
        this.setState({ recording });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - lsoverlayactive
     * @param lsoverlayactive 
    */
    setlsoverlayactive = (lsoverlayactive: boolean) : void => {
        this.setState({ lsoverlayactive });
    }

    /**
     * Shut down all processes
     * 1. STOP recording if currently is recording
     * 2. STOP streaming if currenly is streaming
     * 3. Set modelname to "", screenshotdimension to 0 and logging to fase
     * 4. Close loading screen overlay
    */
    shutdownprocesses = () : void => {
        if (this.state.recording) {
            this.stoprecording();
        }
        if (this.state.streaming) {
            this.stopstreaming();
        }
        this.setState({ modelname: "", screenshotdimension: 0, logging: false});
        this.setlsoverlayactive(false);
    }

    /**
     * Check if streaming is available
     * @returns True if modelname != "" and client is connected to server, else false
    */
    streamingavailable = () : boolean => {
        return this.state.modelname !== "" && this.state.websocketconnected;
    }

    /**
     * Handle modelname (state) on change event
     * 1. If new value = "", send signal to server to reset od model and reset states (modelname & screenshotdimension)
     * 2. If new value = modelname, send the modelname to server
     * @param event 
    */
    handlemodelOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setlsoverlayactive(true);
        if (event.target.value === "") {
            this.stompwebsocket.sendmessage("/server/objectdetection/modelreset", "");
            this.setState({ modelname: "", screenshotdimension: 0 , import: false});
            this.setlsoverlayactive(false);
        } else {
            this.setState({import: true})
            this.stompwebsocket.sendmessage("/server/objectdetection/modelonchanged", JSON.stringify({ modelname: event.target.value }));
        }
    }
    
        /**
     * Called when LOAD MODEL button is clicked
     * Function: load the exsited model in your computer
    */
         loadModel = () : void => {
            this.setlsoverlayactive(true);
            this.setState({load: true})
            this.stompwebsocket.sendmessage("/server/objectdetection/loadmodel", JSON.stringify({ path: this.state.classifierpath,
                 trainPath: this.state.traindatasetpath, testPath: this.state.testdatasetpath  }));
        }

    /**
     * Called when UNLOAD MODEL button is clicked
     * Function: Unload the model
    */
        unloadModel = () : void => {
            this.setState({load: false})
            this.stompwebsocket.sendmessage("/server/objectdetection/modelreset", "");
        }
            
        /**
         * Called when classifier path value on change
         * @param event 
        */
         handleClassifierPathOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
            this.setState({ classifierpath: event.target.value });

        }
                    
        /**
         * Called when classifier path value on change
         * @param event 
        */
         handleTrainPathOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
            this.setState({ traindatasetpath: event.target.value });
        }
                    
        /**
         * Called when classifier path value on change
         * @param event 
        */
         handleTestPathOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
            this.setState({ testdatasetpath: event.target.value });
        }

    /**
     * Callback fucntion for webcam streaming
     * 1. Send webcam data (base64 encoded string) to server for detection
     * @param encodedimage - Image data (base64 encoded string)
    */
    webcamstreamingcallback = (encodedimage: string) : void => {
        let trimmedimage = encodedimage.split(",")[1];
        let imagedata = { base64encodedstring: trimmedimage, length: trimmedimage.length, outputwidth: 500, outputheight: 500 };
        this.stompwebsocket.sendmessage("/server/objectdetection/detect", JSON.stringify(imagedata));
    }

    /**
     * Called when START STREAMING button is clicked or triggered
     * 1. Make sure that webcam is available for streaming
     * 2. Send signal to server indicates streaming is started, together with the value of logging
     * 3. Signal webcam to screenshot every (100ms = 10FPS)
     * 4. Set streaming (state) to true
    */
    startstreaming = () : void => {
        if (this.webcamref.current === null) return;
        if (this.webcamref.current.available()) {
            this.stompwebsocket.sendmessage("/server/objectdetection/streamingstart", JSON.stringify({ logging: this.state.logging }));
            this.webcamref.current.startstreaming(100);
            this.setstreaming(true);
        }
    }

    /**
     * Called when STOP STREAMING button is clicked or triggered
     * 1. Make sure that webcam is available for streaming
     * 2. Stop webcam's screenshot interval
     * 3. Reset canvas background (to gray) and set streaming (state) to false
     * 4. Send signal to server indicates streaming is ended
     * 5. If currently is recording, stop recording
    */
    stopstreaming = () : void => {
        if (this.webcamref.current === null) return;
        this.webcamref.current.stopstreaming();
        this.resetcanvasbackground();
        this.setstreaming(false);
        this.stompwebsocket.sendmessage("/server/objectdetection/streamingend", "");
        if (this.state.recording) {
            this.stoprecording();
        }
    }

    /**
     * Called when START RECORDING button is clicked or triggered
     * 1. Make sure canvas is rendered (mounted)
     * 2. Empty recordedchunks (new recording)
     * 3. Start mediarecorder on canvas
     * 4. Set recording (state) to true
    */
    startrecording = () : void => {
        if (this.canvasref.current !== null) {
            this.setState({ recordedchunks: [] });
            this.recorder = new MediaRecorder(this.canvasref.current.captureStream(10), { mimeType: "video/webm" });
            this.recorder.start();
            this.recorder.ondataavailable = this.savechunks;
            this.setState({ recording: true });
        }
    }

    /**
     * Called when STOP RECORDING button is clicked or triggered
     * 1. Stop recorder and set recording (state) to false
    */
    stoprecording = () : void => {
        if (this.recorder !== null) {
            this.recorder.stop();
            this.setState({ recording: false });
        }
    }

    /**
     * Called when START LOGGING button is clicked or triggered
     * 1. Set logging (state) to true
    */
    startlogging = () : void => {
        this.setState({ logging: true });
    }

    /**
     * Called when STOP LOGGING button is clicked or triggered
     * 1. Set logging (state) to false
    */
    stoplogging = () : void => {
        this.setState({ logging: false });
    }

    /**
     * Save frame into chunks that can be turned in video
     * @param event 
    */
    savechunks = (event: BlobEvent) : void => {
        this.setState({ recordedchunks: [ ...this.state.recordedchunks, event.data ] });
    }

    /**
     * Called when DOWNLOAD button is clicked
     * 1. Turn recordedchunks into video and open the video on a new tab
    */
    handledownload = () : void => {
        if (this.state.recordedchunks.length > 0) {
            let blob = new Blob(this.state.recordedchunks, { type: "video/webm" });
            let url = URL.createObjectURL(blob);
            window.open(url);
        }
    }

    /**
     * [MQTT CALLBACK]
     * Called when receive START STREAMING signal from MQTT broker
     * 1. If streaming is available and current is NOT streaming -> Trigger START STREAMING button
    */
    startstreamingsignalreceivedcallback = () : void => {
        console.log("[MQTT SIGNAL] START STREAMING");
        if (! this.streamingavailable() || this.state.streaming)
            console.log("Streaming is unavailable. Reason: Client is not connected to server OR object detection model is not loaded OR it is already in streaming!");
        else
            this.startstreaming();
    }

    /**
     * [MQTT CALLBACK]
     * Called when receive STOP STREAMING signal from MQTT broker
     * 1. If streaming is available and current is streaming -> Trigger STOP STREAMING button
    */
    stopstreamingsignalreceivedcallback = () : void => {
        console.log("[MQTT SIGNAL] STOP STREAMING");
        if (! this.streamingavailable() || ! this.state.streaming)
            console.log("Stop streaming is unavailable. Reason: Client is not connected to server OR object detection model is not loaded OR webcam is current no streaming!");
        else
            this.stopstreaming();
    }

    /**
     * [MQTT CALLBACK]
     * Called when receive START RECORDING signal from MQTT broker
     * 1. If streaming is available and current is NOT recording -> Trigger START RECORDING button
    */
    startrecordingsignalreceivedcallback = () : void => {
        console.log("[MQTT SIGNAL] START RECORDING");
        if (! this.state.streaming || this.state.recording)
            console.log("Recording is unavailable. Reason: Client is not currently streaming or it is already in recording!");
        else
            this.startrecording();
    }

    /**
     * [MQTT CALLBACK]
     * Called when receive STOP RECORDING signal from MQTT broker
     * 1. If streaming is available and current is recording -> Trigger STOP RECORDING button
    */
    stoprecordingsignalreceivedcallback = () : void => {
        console.log("[MQTT SIGNAL] STOP RECORDING");
        if (! this.state.streaming || this.state.recording)
            console.log("Stop recording is unavailable. Reason: Client is not currently streaming or it is not in recording!");
        else
            this.stoprecording();
    }

    /**
     * [MQTT CALLBACK]
     * Called when receive START LOGGING signal from MQTT broker
     * 1. If current not streaming AND websocket is connected to server AND current is NOT logging -> Trigger START LOGGING button
    */
    startloggingsignalreceivedcallback = () : void => {
        console.log("[MQTT SIGNAL] START LOGGING");
        if (this.state.streaming || ! this.state.websocketconnected || this.state.logging)
            console.log("Logging is unavailable. Reason: Client is not connected to server OR client is in streaming OR it is already in logging mode!");
        else
            this.startlogging();
    }

    /**
     * [MQTT CALLBACK]
     * Called when receive STOP LOGGING signal from MQTT broker
     * 1. If current not streaming AND websocket is connected to server AND current is logging -> Trigger STOP LOGGING button
    */
    stoploggingsignalreceivedcallback = () : void => {
        console.log("[MQTT SIGNAL] STOP LOGGING");
        if (this.state.streaming || ! this.state.websocketconnected || ! this.state.logging)
            console.log("Logging is unavailable. Reason: Client is not connected to server OR client is in streaming OR it is not in logging mode!");
        else
            this.stoplogging();
    }

    render = () => {
        return (
            <>
                <SuccessModal ref={this.successmodalref} />
                <ErrorModal ref={this.errormodalref} />
                <LoadingScreenOverlay active={this.state.lsoverlayactive} />
                <div className='objectdetection-configuration-panel'>
                    <Form>
                        <div className='pair'>
                            <div className='description'>DL4J BACKEND</div>
                            <DL4JBackend 
                                websocketconnected={this.state.websocketconnected}
                                backendiscpu={this.state.backendiscpu}
                                backendisgpu={this.state.backendisgpu}
                            />
                        </div>


                        {/* MODAL SELECTION PAIR  */}
                        <div className='pair'>
                            <div className='description'>OBJECT DETECTION MODEL</div>
                            <FormGroup>
                                <Input type='select' name='modelname' id='modelname' onChange={this.handlemodelOnchange} disabled={this.state.streaming || ! this.state.websocketconnected || this.state.load}>
                                    <option value="">Select</option>
                                    <option value="tinyyolo">TinyYolo</option>
                                    <option value="yolo2">YOLOv2</option>
                                </Input>
                                
                            </FormGroup>

                            <div className='description'> LOAD MODEL </div>
                            <FormGroup>
                            <   div className='description'> MODEL PATH </div>
                                <Input type='text' name='classifierpath'  value={this.state.classifierpath} onChange={this.handleClassifierPathOnchange}></Input>
                            <   div className='description'> TRAIN DATASET PATH </div>
                                <Input type='text' name='TrainDatasetPath'  value={this.state.traindatasetpath} onChange={this.handleTrainPathOnchange}></Input>
                            <   div className='description'> TEST DATASET PATH </div>
                                <Input type='text' name='TestDatasetPath'  value={this.state.testdatasetpath} onChange={this.handleTestPathOnchange}></Input>
                            </FormGroup>
                            <Container>
                                <Row>
                                    <Col><Button block color='primary' disabled={! this.state.websocketconnected || this.state.import} onClick={this.loadModel} >LOAD MODEL</Button></Col>
                                    <Col><Button block color='primary' disabled={! this.state.websocketconnected || this.state.import} onClick={this.unloadModel} >UNLOAD MODEL</Button></Col>
                                </Row>
                            </Container>
                            
                        </div>

                        {/* STREAMING PAIR  */}
                        <div className='pair'>
                            <div className='description'>STREAMING</div>
                            <Container>
                                <Row>
                                    <Col><Button block color='primary' onClick={this.startstreaming} disabled={! this.streamingavailable() || this.state.streaming}>START</Button></Col>
                                    <Col><Button block color='danger' onClick={this.stopstreaming} disabled={! this.streamingavailable() || ! this.state.streaming}>STOP</Button></Col>
                                </Row>
                                <OnOffmqttsPanel 
                                    canconnect={true}
                                    startsignalcallback={this.startstreamingsignalreceivedcallback}
                                    stopsignalcallback={this.stopstreamingsignalreceivedcallback}
                                />
                            </Container>
                        </div>

                        {/* RECORDING PAIR  */}
                        <div className='pair'>
                            <div className='description'>RECORDING</div>
                            <Container>
                                <Row>
                                    <Col><Button block color='primary' onClick={this.startrecording} disabled={! this.state.streaming || this.state.recording}>START</Button></Col>
                                    <Col><Button block color='danger' onClick={this.stoprecording} disabled={! this.state.streaming || ! this.state.recording }>STOP</Button></Col>
                                </Row>
                                <OnOffmqttsPanel 
                                    canconnect={true}
                                    startsignalcallback={this.startrecordingsignalreceivedcallback}
                                    stopsignalcallback={this.stoprecordingsignalreceivedcallback}
                                />
                            </Container>
                        </div>

                        {/* LOGGING PAIR  */}
                        <div className='pair'>
                            <div className='description'>LOGGING</div>
                            <Container>
                                <Row>
                                    <Col><Button block color='primary' onClick={this.startlogging} disabled={this.state.streaming || ! this.state.websocketconnected || this.state.logging}>START</Button></Col>
                                    <Col><Button block color='danger' onClick={this.stoplogging} disabled={this.state.streaming || ! this.state.websocketconnected || ! this.state.logging}>STOP</Button></Col>
                                </Row>
                                <OnOffmqttsPanel 
                                    canconnect={true}
                                    startsignalcallback={this.startloggingsignalreceivedcallback}
                                    stopsignalcallback={this.stoploggingsignalreceivedcallback}
                                />
                            </Container>
                        </div>
                    
                        {/* DOWNLOAD RECORDING  */}
                        <div className='pair'>
                            <div className='description'>DOWNLOAD RECORDING</div>
                            <Container>
                                <Row>
                                    <Col><Button block color='primary' onClick={this.handledownload} disabled={this.state.recordedchunks.length === 0}>DOWNLOAD</Button></Col>
                                </Row>
                            </Container>
                        </div>

                        <div className='pair'>
                            <div className='description'>OUTPUT</div>
                            <Container>
                                <DataOutputPanel ref={this.webcamoutputref}/>
                            </Container>
                        </div>
                    </Form>
                </div>


                <div className='objectdetection-main-content-container'>
                    <div className='webcam-canvas-container'>
                        <div className='webcam-canvas-element'>
                            <WebcamCapture 
                                webcamwidth={500}
                                webcamheight={500}
                                screenshotwidth={this.state.screenshotdimension}
                                screenshotheight={this.state.screenshotdimension}
                                streamingcallback={this.webcamstreamingcallback}
                                ref={this.webcamref}
                            />
                        </div>
                        <div className='webcam-canvas-element'>
                            <canvas height={500} width={500} ref={this.canvasref}></canvas>
                        </div>
                    </div>
                </div>
            </>
        )
    }
}