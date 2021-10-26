import React, {Component} from 'react'
import Webcam  from 'react-webcam'
import {Form, FormGroup, Label, Input, Row, Col} from 'reactstrap'

/**
 * Webcam props
 * webcamwidth: Webcam's width
 * webcamheight: Webcam's height
 * screenshotwidth: Webcam screenshot's width
 * screenshotheight: Webcam screenshot's height
 * streamingcallback: Callback function for webcam streaming (send back webcam screenshot data)
*/
interface webcamprops {
    webcamwidth: number;
    webcamheight: number;
    screenshotwidth: number;
    screenshotheight: number;
    streamingcallback: null | ((imagedata: string) => void);
}

/**
 * Webcam states
 * webcamdeviceId: Id of webcam device which is currently using
 * cameradevices: List of video devices which are available
 * webcamstreaming: If webcam is currently streaming
*/
interface webcamstates {
    webcamdeviceId: string;
    cameradevices: MediaDeviceInfo[];
    webcamstreaming: boolean;
}

export default class WebcamCapture extends Component <webcamprops, webcamstates> {

    /**
     * Webcam class properties
     * webcamref: Webcam component's ref
     * streamInterval: Screenshot interval
    */
    webcamref: React.RefObject<any>;
    streamInterval:  null | NodeJS.Timeout;

    constructor(props: webcamprops) {
        super(props);
        this.state = { 
            webcamdeviceId: "", 
            cameradevices: [],
            webcamstreaming: false,
        }
        this.webcamref = React.createRef();
        this.streamInterval = null;
        // Retrieve all the available video source
        this.getallvideodevices();
        // Set ondevicechange callback function
        navigator.mediaDevices.ondevicechange = () => this.getallvideodevices();
    }

    /**
     * [UPDATE COMPONENT'S STATE] webcamdeviceId
     * @param webcamdeviceId 
    */
    setWebcamDeviceId = (webcamdeviceId: string ): void => {
        this.setState({ webcamdeviceId });
    }

    /**
     * [UPDATE COMPONENT'S STATE] cameradevices
     * @param cameradevices 
    */
    setCameraDevices = (cameradevices: MediaDeviceInfo[]) : void => {
        this.setState({ cameradevices })
    }

    /**
     * [UPDATE COMPONENT'S STATE] webcamstreaming
     * @param webcamstreaming 
    */
    setWebcamStreaming = (webcamstreaming: boolean) : void => {
        this.setState({ webcamstreaming });
    }

    /** Get all available video source connected to user device */
    getallvideodevices = () : void => {
        navigator.mediaDevices.enumerateDevices().then(this.updatecameradevices);
    }

    /**
     * Update camera devices (filter only video input)
     * Audio input and etc. will be ignored
     * @param mediadevices - All media devices detected (including audio, video etc.)
    */
    updatecameradevices = (mediadevices: MediaDeviceInfo[]) : void => {
        let cameradevices = mediadevices.filter( ({kind}) => kind === "videoinput");
        this.setCameraDevices(cameradevices);
    }

    /**
     * Called when webcam Id on change
     * Change active webcam Id to target value
     * @param event 
    */
    handleWebcamIdOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setWebcamDeviceId(event.target.value);
    }

    /**
     * [COMPONENT LIFECYCLE]
     * Reset webcam Id to "" if screenshot width or height is changed
     * @param nextprops 
     * @param nextState 
     * @returns TRUE
    */
    shouldComponentUpdate = (nextprops: webcamprops, nextState: webcamstates) : boolean => {
        if (this.props.screenshotwidth !== nextprops.screenshotwidth || this.props.screenshotheight !== nextprops.screenshotheight) {
            if (this.state.webcamstreaming)
                this.stopstreaming();
            nextState.webcamdeviceId = ""
        }
        return true;
    }

    /**
     * Check if webcam is available for streaming
     * @returns - webcamdeviceId != ""
    */
    available = () : boolean => {
        return this.state.webcamdeviceId !== "";
    }

    /** Check if webcam is current streaming */
    isStreaming = () : boolean => {
        return this.state.webcamstreaming;
    }

    /**
     * 1. Perform screenshot action
     * 2. Return image data (base64 encoded string)
     * @returns - Image data in base64 encoded string format
    */
    screenshot = () : string=> {
        let imagesource = this.webcamref.current.getScreenshot( { width: this.props.screenshotwidth, height: this.props.screenshotheight })     
        return imagesource;
    }

    /**
     * Function is invoked every x milliseconds (setInterval callback function)
     * 1. Screenshot and get image data
     * 2. Invoke streamingcallback props function
    */
    streaming = () : void => {
        if (! this.available()) return;
        let imagesource = this.screenshot();
        if (this.props.streamingcallback !== null)
            this.props.streamingcallback(imagesource);
    }

    /**
     * Start webcam streaming
     * 1. Call this.streaming() every specfied intervals (in milliseconds)
     * 2. Set streaming (state) to true
     * @param milliseconds - The intervals (in milliseconds) on how often to execute the code
    */
    startstreaming = (milliseconds: number) : void => {
        this.streamInterval = setInterval(this.streaming, milliseconds)
        this.setWebcamStreaming(true);
    }

    /**
     * Stop streaming
     * 1. Clears this.streaming() timer set by setInterval() method
     * 2. Set streaming (state) to false
    */
    stopstreaming = () : void => {
        if (this.streamInterval !== null)
            clearInterval(this.streamInterval)
        this.setWebcamStreaming(false);
    }

    render = () => {
        return (
            <div style={{ width: this.props.webcamwidth + 20, padding: 10, margin: "0 auto" }}>
                <Form>
                    <Row form>
                        <Col>
                            <FormGroup>
                                <Label for="webcamdeviceId">CAMERA (WEBCAM)</Label>
                                <Input type='select' name='webcamdeviceId' id='webcamdeviceId' value={this.state.webcamdeviceId} onChange={this.handleWebcamIdOnchange}>
                                    <option value="">Select</option>
                                    { this.state.cameradevices.map ((device, key) => 
                                        <option key={key} value={device.deviceId}>{device.label}</option>
                                    )}
                                </Input>
                            </FormGroup>
                        </Col>
                    </Row>
                </Form>
                { this.state.webcamdeviceId !== "" &&
                    < Webcam
                        audio={false}
                        screenshotFormat="image/jpeg"
                        screenshotQuality={1}
                        width={this.props.webcamwidth}
                        height={this.props.webcamheight}
                        videoConstraints={{ aspectRatio: 1, deviceId: this.state.webcamdeviceId, frameRate: 30 }}
                        ref={this.webcamref}
                    />
                }
            </div>
        )
    }
}