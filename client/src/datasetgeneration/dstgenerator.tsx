import moment from 'moment';
import { EventEmitter } from 'events'
import { waitFor } from 'wait-for-event'
import React, {Component} from 'react'
import { IFrame, IMessage} from '@stomp/stompjs';
import {Form, FormFeedback, FormGroup, FormText, Label, Input, Button, Container} from 'reactstrap'

import WebcamCapture from '../globalcomponents/webcam';
import ErrorModal from '../globalcomponents/glberrormodal';
import SuccessModal from '../globalcomponents/glbsuccessmodal';
import WebsocketService from '../globalcomponents/glbwebsocket';
import OTSmqttsPanel from "../globalcomponents/mqtt/onetimesignals"
import "./dstgenerator.css";

/**
 * DatasetGenerator props
*/
interface DatasetGeneratorProps {

}

/**
 * DatasetGenerator states
 * websocketconnected: If client is connected to server's websocket
 * screenshotwidth: Webcam's screenshot (output image) width
 * screenshotheight: Webcam's screenshot (output image) height
 * screenshotcount: Number of image(s) to take (Burst shot)
 * directorypath: Directory to save image(s)
*/
interface DatasetGeneratorStates {
    websocketconnected: boolean;
    screenshotwidth: number;
    screenshotheight: number;
    screenshotcount: number;
    directorypath: string;
}

export default class DatasetGenerator extends Component <DatasetGeneratorProps, DatasetGeneratorStates> {
    
    /**
     * DatasetGenerator class properties
     * cancontinuescreenshot: If screenshot operation can continue
     * webcamref: Webcam's ref
     * successmodalref: Success modal's ref
     * errormodalref: Error modal's ref
     * stompwebsocket: WebsocketService class
     * eventemitter: EventEmitter class (used to emit specific event name)
    */
    cancontinuescreenshot: boolean;
    webcamref: React.RefObject<any>;
    successmodalref: React.RefObject<any>;
    errormodalref: React.RefObject<any>;
    stompwebsocket: WebsocketService;
    eventemitter: EventEmitter;

    constructor(props: DatasetGeneratorProps) {
        super(props);
        this.state = {
            websocketconnected: false,
            screenshotwidth: 500,
            screenshotheight: 500,
            screenshotcount: 1,
            directorypath: "D://AppGeneratedDataset",
        }
        this.cancontinuescreenshot = true;
        this.webcamref = React.createRef();
        this.successmodalref = React.createRef();
        this.errormodalref = React.createRef();
        this.eventemitter = new EventEmitter();

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
     * [COMPONENT LIFECYCLE]
     * Called before DatasetGenerator component is unmounted (when Dataset Generation page is closed)
     * 1. Remove all websocket callbacks
     * 2. Unsubscribe from all topics
     * 3. Disconnect from server
    */
    componentWillUnmount = () : void => {
        this.stompwebsocket.removeonConnectCallback();
        this.stompwebsocket.removeonDisconnectCallback();
        this.stompwebsocket.removeonWebSocketCloseCallback();
        this.stompwebsocket.removeonWebsocketErrorCallback();
        if (this.stompwebsocket.isConnected()) {
            this.stompwebsocket.unsubscribe("/response/screenshot/success");
            this.stompwebsocket.unsubscribe("/response/screenshot/failed");
        }
        this.stompwebsocket.disconnect();
    }

    /**
     * [WEBSOCKET CALLBACK]
     * Called when client is connected to server
     * 1. Set websocketconnected (state) to TRUE
     * 2. Subscribe to topics
     * "response/screenshot/success" - When image is successfully saved 
     * "/response/screenshot/failed" - When image is failed to save
     * @param frame
    */
    websocketonConnect = (frame: IFrame) : void => {
        this.setwebsocketconnected(true);
        this.stompwebsocket.subscribe("/response/screenshot/success", this.screenshotsuccesscallback);
        this.stompwebsocket.subscribe("/response/screenshot/failed", this.screenshotfailedcallback);
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
     * [UPDATE COMPONENT'S STATE] - websocketconnected
     * @param websocketconnected 
    */
    setwebsocketconnected = (status: boolean) : void => {
        this.setState({ websocketconnected : status });
    }

    /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK]
     * Called when screenshot image is saved successfully
     * 1. Emit "ssprocessed" event -> Screenshot and send next image
     * @param response 
    */
    screenshotsuccesscallback = (response: IMessage) : void => {
        setImmediate(() => this.eventemitter.emit("ssprocessed"))
    }

    /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK]
     * Called when screenshot image failed to save
     * 1. Set "cancontinuescreenshot" to false (next image will be not sent)
     * 2. Emit "ssprocessed" event
     * 3. Get error message (reason) from server's response and open error modal
     * @param response 
    */
    screenshotfailedcallback = (response: IMessage) : void => {
        this.cancontinuescreenshot = false;
        let data = JSON.parse(response.body)
        setImmediate(() => this.eventemitter.emit("ssprocessed"))
        this.errormodalref.current.openmodal("ERROR DURING SAVING SCREENSHOT", data.message)
    }

    /**
     * Handle screenshotwidth (state) on change event
     * 1. Update value of screenshotwidth
     * @param event 
    */
    handlesswidthOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setState({ screenshotwidth: Number(event.target.value) });
    }

    /**
     * Handle screenshotheight (state) on change event
     * 1. Update value of screenshotheight
     * @param event 
    */
    handlessheightOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setState({ screenshotheight: Number(event.target.value) });
    }
    
    /**
     * Handle screenshotcount (state) on change event
     * 1. Update value of screenshotcount
     * @param event 
    */
    handlesscountOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setState({ screenshotcount: Number(event.target.value) });
    }

    /**
     * Handle directorypath (state) on change event
     * 1. Update value of directorypath
     * @param event 
    */
    handledirectoryOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setState({ directorypath: event.target.value });
    }

    /**
     * Called when SCREENSHOT button is clicked or triggered
     * 1. Make sure screenshotwidth, screenshotheight & screenshotcount is within range
     * 2. Make sure webcam is available
     * 3. Set cancontinuescreenshot to true
     * 4. Screenshot (screenshotcount) number of times and send image data (base64 encoded to server) -> Wait for "ssprocessed" event before proceed to next image
     * 5. Open success modal
    */
    screenshotOnclick = async () : Promise<any> => {
        if (this.state.screenshotwidth < 200 || this.state.screenshotheight < 200 || this.state.screenshotcount <= 0) {
            this.errormodalref.current.openmodal("OOPS", "Service unavailable. Please make sure that screenshot width/height/count is within accepted range");
            return;
        } else if (! this.webcamref.current.available()) {
            this.errormodalref.current.openmodal("OOPS", "Service unavailable. Video source not found.");
            return;
        }
        this.cancontinuescreenshot = true;
        let ctimestamp = moment().format('MMMM-Do-YYYY h-mm-ss-a');
        for (let index = 0; index < this.state.screenshotcount; index++) {
            if (! this.cancontinuescreenshot) return;
            let encodedimage = this.webcamref.current.screenshot();
            let trimmedimage = encodedimage.split(",")[1];

            let data = { 
                base64encodedstring: trimmedimage, 
                length: trimmedimage.length,
                directory: this.state.directorypath, 
                filename: ctimestamp + "(" + index + ")"
            }
            this.stompwebsocket.sendmessage("/server/screenshot/save", JSON.stringify(data));
            await waitFor("ssprocessed", this.eventemitter)
        }
        if (this.cancontinuescreenshot)
            this.successmodalref.current.openmodal(this.state.screenshotcount + " image(s) has/been saved to " + this.state.directorypath)
    }

    /**
     * [MQTT CALLBACK]
     * Called when receive SCREENSHOT signal from MQTT broker
     * 1. If client is connected to server, trigger SCREENSHOT button
    */
    startscreenshotsignalreceivedcallback = () : void=> {
        console.log("Received start screenshot signal from MQTT broker");
        if (! this.state.websocketconnected)
            console.log("Screenshot is unavailable. Reason: Client is not connected to server!");
        else
            this.screenshotOnclick();
    }


    render = () => {
        return (
            <>
                <SuccessModal ref={this.successmodalref}/>
                <ErrorModal ref={this.errormodalref}/>
                <div className='dataset-generator-configuration-panel'>
                    <Form>
                        <FormGroup>
                            <Label for="screenshotwidth">WIDTH</Label>
                            <Input type='number' name='screenshotwidth' id='screenshotwidth' valid={this.state.screenshotwidth >= 200} invalid={this.state.screenshotwidth < 200} value={this.state.screenshotwidth} onChange={this.handlesswidthOnchange}/>
                            <FormFeedback valid={false} tooltip>Screenshot width has a minimum of 200px</FormFeedback>
                        </FormGroup>

                        <FormGroup>
                            <Label for="screenshotheight">HEIGHT</Label>
                            <Input type='number' name='screenshotheight' id='screenshotheight' valid={this.state.screenshotheight >= 200} invalid={this.state.screenshotheight < 200} value={this.state.screenshotheight} onChange={this.handlessheightOnchange}/>
                            <FormFeedback valid={false} tooltip>Screenshot height has a minimum of 200px</FormFeedback>
                        </FormGroup>

                        <FormGroup>
                            <Label for="screenshotcount">COUNT</Label>
                            <Input type='number' name='screenshotcount' id='screenshotcount' valid={this.state.screenshotcount > 0} invalid={this.state.screenshotcount <= 0} value={this.state.screenshotcount} onChange={this.handlesscountOnchange}/>
                            <FormFeedback valid={false} tooltip>Screenshot count must be at least 1</FormFeedback>
                        </FormGroup>

                        <FormGroup>
                            <Label for="directorypath">LOCATION</Label>
                            <Input type='text' name='directorypath' id="directorypath" value={this.state.directorypath} onChange={this.handledirectoryOnchange}></Input>
                            <FormText>DIRECTORIES WILL BE CREATED IF LOCATION IS NOT FOUND</FormText>
                        </FormGroup>
                    </Form>
                    <Container>
                        <OTSmqttsPanel 
                            canconnect={true}
                            signalcallback={this.startscreenshotsignalreceivedcallback}
                        />
                    </Container>
                </div>

                <div className="dataset-generator-main-content-container">
                    <div>
                        <WebcamCapture
                            webcamwidth={500}
                            webcamheight={500}
                            screenshotwidth={this.state.screenshotwidth}
                            screenshotheight={this.state.screenshotheight}
                            streamingcallback={null}
                            ref={this.webcamref}
                        />
                        <div>
                            <Button block color='primary' onClick={this.screenshotOnclick} disabled={! this.state.websocketconnected}>Screenshot</Button>
                        </div>
                    </div>
                </div>
            </>
        )
    }
}