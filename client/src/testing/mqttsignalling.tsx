import React, {Component} from 'react'
import mqtt, { MqttClient } from 'mqtt';
import { Button, Form, Input, FormGroup, Label } from 'reactstrap'

/**
 * MqttSignalandOutputTesting props 
*/
interface MqttSignalandOutputTestingProps {

}

/**
 * MqttSignalandOutputTesting states
 * connected: If client is connected to broker
*/
interface MqttSignalandOutputTestingStates {
    connected: boolean;
}

export default class MqttSignalandOutputTesting extends Component <MqttSignalandOutputTestingProps, MqttSignalandOutputTestingStates> {

    /**
     * MqttSignalandOutputTesting class properties
     * client - MqttClient object
     * canvasref: Canvas' ref
    */
    client: MqttClient;
    canvasref: React.RefObject<any>;

    // Constructor
    constructor(props: MqttSignalandOutputTestingProps) {
        super(props);
        this.state = { connected: false };
        this.client = mqtt.connect("ws://broker.emqx.io:8083/mqtt");
        this.client.once("connect", this.onconnectcallback);
        this.client.on("message", this.onmessagecallback);
        this.client.once("close", this.onclosecallback);
        this.client.once("error", this.onerrorcallback);
        this.canvasref = React.createRef();
    }

    /**
     * [COMPONENT LIFECYCLE]
     * Called before component is unmounted
     * 1. If client is connected to broker, disconnect it
    */
    componentWillUnmount = () : void => {
        if (this.client.connected)
            this.client.end();
    }

    /**
     * [MQTT CALLBACK]
     * Called when client is connected to broker
     * 1. Subscribe to "targetdestination" topic
     * 2. Set connected (state) to true
     */
    onconnectcallback = () : void => {
        this.client.subscribe("targetdestination");
        this.setState({ connected: true });
    }

    /**
     * [MQTT CALLBACK]
     * Called when client receives message from broker
     * 1. Check if destination of message is "targetdestination"
     * 2. Check if message received is base64 encoded string
     * 3. Render image on canvas
     * @param topic - Destination
     * @param payload - Content of message
    */
    onmessagecallback = (topic: string, payload: Buffer) : void => {
        if (topic !== "targetdestination") return;
        if (! this.canvasref.current) return;
        let processedimage = payload.toString();
        if (! this.checkisbase64encodedstring(processedimage)) return;
        let context2d = this.canvasref.current.getContext("2d");
        let image = new Image();
        image.onload = () => {
            context2d.drawImage(image, 0, 0);
        }
        image.src = processedimage;
    }

    /**
     * [MQTT CALLBACK]
     * Called when client connection is on error
     * 1. console.log error message (reason)
     * 2. Set connected (state) to false
     */
    onerrorcallback = (error: Error) : void => {
        console.log(error.message);
        if (this.state.connected)
            this.setState({ connected: false });
    }

    /**
     * [MQTT CALLBACK]
     * Called when client connection is closed
     * 1. Set connected (state) to false
    */
    onclosecallback = () : void => {
        if (this.state.connected)
            this.setState({ connected: false });
    }

    /**
     * Send a signal (blank message) to target destination
     * @param destination - Destination to send signal
    */
    sendsignal = (destination: string) : void => {
        if (this.client.connected)
            this.client.publish(destination, "");
    }

    /**
     * Check if data is base64 encoded string
     * @param data - String to check
     * @returns - If data is base64 encoded
    */
    checkisbase64encodedstring = (data: string) : boolean => {
        try {
            return btoa(atob(data)) === data;
        } catch (exception) {
            return false;
        }
    }


    render = () => {
        return (
            <div style={{ width: '100%', height: 'calc(100vh - 100px)', overflowY: 'scroll'}}>
                <div style={{ width: '80%', margin: '0 auto', textAlign:"center" }}>
                    <div style={{ whiteSpace: 'pre-line' }}>This page is to test the functionality of mqtt signalling and output</div>
                    <div style={{ whiteSpace: 'pre-line' }}>Client (mqtt) is auto connected to url <b>"ws://broker.emqx.io:8083/mqtt"</b></div>
                    <div style={{ whiteSpace: 'pre-line' }}>You can choose to send a <b>signal</b> (blank message) to any destination/topic.</div>
                </div>
                <div style={{width: '50%', margin: "0 auto"}}>
                    <InputButtonGroup sendsignal={this.sendsignal} buttoncansend={this.state.connected}/>
                </div>
                <div style={{ width: '80%', margin: '0 auto', textAlign:"center" }}>
                    <div style={{ whiteSpace: 'pre-line' }}>Client is also subscribe to destination/topic named <b>targetdestination</b></div>
                    <div style={{ whiteSpace: 'pre-line' }}>Image <b>(base64 encoded) </b> sent to the destination will be rendered on the canvas below</div>
                </div>
                <div style={{width: '50%', height: 500, margin: "0 auto", border: "1px black solid", display: "flex", alignItems: "center", justifyContent: "center", overflowX: "scroll", overflowY: 'hidden'}}>
                    <div style={{height: 500, width: 500 }}>
                        <canvas height={500} width={500} ref={this.canvasref}></canvas>
                    </div>
                </div>
            </div>
        )
    }
}

// ======================================================================

/**
 * InputButtonGroup props
 * sendsignal: Callback function when SIGNAL button is clicked
 * buttoncansend: If button can be clicked (If client is connected to broker)
*/
interface InputButtonGroupProps {
    sendsignal: ((destination: string) => void)
    buttoncansend: boolean;
}

/**
 * InputButtonGroup states
 * destination: Destination to send the signal
*/
interface InputButtonGroupStates {
    destination: string;
}

class InputButtonGroup extends Component <InputButtonGroupProps, InputButtonGroupStates> {
    constructor(props: InputButtonGroupProps) {
        super(props);
        this.state = { destination: "signaldestination" }
    }

    /**
     * Called when value of destination (input field) on change
     * 1. Update value of destination (state)
    */
    handledestinationOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setState({ destination: event.target.value });
    }

    render = () => {
        return (
            <div style={{ padding: 10, border: "1px black solid",  borderRadius: 5, marginTop: 10, }}>
                <Form>
                    <FormGroup>
                        <Label for="destination">Destination</Label>
                        <Input type='text' name='destination' value={this.state.destination} onChange={this.handledestinationOnchange}></Input>
                    </FormGroup>
                </Form>
                <Button color="primary" disabled={! this.props.buttoncansend} block onClick={() => this.props.sendsignal(this.state.destination)}>SIGNAL</Button>
            </div>
        )
    }
}