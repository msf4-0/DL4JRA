import React, {Component} from 'react'
import mqtt, { MqttClient } from "mqtt"
import { Modal, ModalHeader, ModalBody, ModalFooter, Form, FormGroup, Label, Input, Button, Container, Row, Col } from 'reactstrap'
import "./mqttpanels.css"

/**
 * MQTT object interface
 * client: MqttClient class object
 * mId: MqttDetailsBar Id (unique & auto-increment)
 * brokerurl: broker URL client is going to connect 
 * signaldestination: Destination (topic) where signal will be received
 * connected: If client is connected to broker
*/
interface mqttObject {
    client: null | MqttClient;
    mId: string;
    brokerurl: string;
    signaldestination: string;
    connected: boolean;
}

/**
 * OneTimeSignalmqttsPanel props
 * defaultmqttobject: Default value of new mqttObject
 * canconnect: If user is allowed to connect to mqtt broker
 * signalcallback: Callback function when signal is received
*/
interface OTSmqttsPanelProps {
    defaultmqttobject: mqttObject;
    canconnect: boolean;
    signalcallback: (() => void);
}

/**
 * OneTimeSignalmqttsPanel states
 * mqttobjects: Array to store mqttObject
 * modalactive: If OneTimeSignalmqttsPanel configuration modal is active (visible) (modal which user can modify brokerURL & destination)
 * indexOnchange: Id of mqttobject which is currently in modification
*/
interface OTSmqttsPanelStates{
    mqttobjects: mqttObject[];
    modalactive: boolean;
    indexOnchange: number;
}

export default class OTSmqttsPanel extends Component <OTSmqttsPanelProps, OTSmqttsPanelStates> {
    
    /**
     * OneTimeSignalmqttsPanel class properties
     * mqttobjectId: current Id of mqttobject (unique + auto increment)
    */
    mqttobjectId: number;
    
    // Default props 
    static defaultProps = { defaultmqttobject: 
        { 
            client: null, 
            brokerurl: "ws://broker.emqx.io:8083/mqtt",
            signaldestination: "signaldestination",
            connected: false 
        }
    }

    // Constructor
    constructor(props: OTSmqttsPanelProps) {
        super(props);
        this.state = {
            modalactive: false,
            indexOnchange: -1,
            mqttobjects: [ ], 
        }
        this.mqttobjectId = 0;
    }

    /**
     * [COMPONENT LIFECYCLE]
     * Called before component is unmounted
     * 1. Loop through all mqttobjects
     *      - Disconnect from broker if still connected
     *      - Delete mqtt object
    */
    componentWillUnmount = () : void => {
        for (let index = 0; index < this.state.mqttobjects.length; index ++) {
            let mId = this.state.mqttobjects[index].mId;
            this.deletemqttconfiguration(mId);
        }
    }

    /**
     * Generate unique Id for mqttobject
     * @returns Id
    */
    generateuniqueId = () : number => {
        this.mqttobjectId++;
        return this.mqttobjectId;
    }

    /**
     * Get the index of mqttobject (with matching mId) in mqttobjects array (state)
     * @param mId - mId of mqttobject
     * @returns Index of mqttobject in mqttobjects array, -1 if not found
    */
    getmqttIndex = (mId: string) : number => {
        return this.state.mqttobjects.findIndex( object => object.mId === mId );
    }

    /**
     * Add new mqttobject to mqttobjects array (state)
     * 1. Create new mqttobject (default as props.defaultmqttobject)
     * 2. Generate unique mId
     * 3. Update state
    */
    addnewmqttconfiguration = () : void => {
        let newmqttobject = this.props.defaultmqttobject;
        newmqttobject["mId"] = this.generateuniqueId().toString();
        this.setState({ 
            mqttobjects: [ ...this.state.mqttobjects, newmqttobject ]
        });
    }

    /**
     * 1. Get the index of mqttobject using mId 
     * 2. Set indexOnchange to the index 
     * 3. Open mqttobject configuration panel
     * @param mId - mId of mqttobject which is in modification
    */
    openmqttconfigurationpanel = (mId: string) : void => {
        let index = this.getmqttIndex(mId);
        this.setState({ indexOnchange: index, modalactive: true });
    }

    /**
     * 1. Set streamingmqttIfonChanged to -1 
     * 2. Close mqttobject configuration panel
    */
    closemqttconfigurationpanel = () : void => {
        this.setState({ indexOnchange: -1, modalactive: false });
    }

    /**
     * Delete mqttobject from mqttobjects array
     * 1. Disconnect client if connected
     * 2. Get the index of mqttobject in mqttobjects array -> update state 
     * @param mId - mId of mqttobject to be removed
    */
    deletemqttconfiguration = (mId: string) : void => {
        this.disconnectfrombroker(mId);
        let index = this.getmqttIndex(mId);
        this.setState({
            mqttobjects:
            [
                ...this.state.mqttobjects.slice(0, index),
                ...this.state.mqttobjects.slice(index+1)
            ]
        })
    }

    /**
     * Update mqttobject data
     * @param brokerurl - Updated value of brokerurl
     * @param signaldestination - Updated value of signaldestination
     * @param index - Index of mqttobject which data is updated
    */
    updatemqttdata = (brokerurl: string, signaldestination: string, index: number) : void => {
        this.setState({
            mqttobjects:
            [
                ...this.state.mqttobjects.slice(0, index),
                {
                    ...this.state.mqttobjects[index],
                    brokerurl, signaldestination,
                },
                ...this.state.mqttobjects.slice(index+1)
            ]
        })
    }

    /**
     * Update mqttobject connected (boolean) status
     * @param index - Index of mqttobject
     * @param connected - If client is connected to broker
    */
    updatemqttstatus = (index: number, connected: boolean) : void => {
        this.setState({ 
            mqttobjects: [
                ...this.state.mqttobjects.slice(0, index),
                { ...this.state.mqttobjects[index], connected },
                ...this.state.mqttobjects.slice(index + 1)
            ]
        })
    }

    /**
     * Connect mqttobject's client to broker
     * 1. Get the index of mqttobject
     * 2. Check if mqttobject's client is already connected
     * 3. Check if brokerurl, start/stop destination is OK
     * 4. Set mqtt's connect, close, error, message callback function
     * 5. Connect client to mqtt broker
     * @param mId - mId of mqttobject which the client belongs to
    */
    connecttobroker = (mId: string) : void => {
        let index = this.getmqttIndex(mId);
        if (index === -1) return;
        let mqttdata = this.state.mqttobjects[index];
        if (mqttdata.connected) return;
        if (mqttdata.brokerurl === "" || mqttdata.signaldestination === "") return;
        mqttdata.client = mqtt.connect(mqttdata.brokerurl, { reconnectPeriod : 0, });
        mqttdata.client.once("connect", () => this.mqttonconnect(mId));
        mqttdata.client.once("close", () => this.mqttonclose(mId));
        mqttdata.client.once("error", () => this.mqttonerror(mId));
        mqttdata.client.on("message", (topic: string, payload: Buffer) => this.mqttonmessage(topic, payload, mId))
    }

    /**
     * Disconnect mqttobject's client from broker
     * 1. Get the index of mqttobject using mId
     * 2. Disconnect client from brokerurl
     * 3. Update mqttobject connected status 
     * @param mId - mId of mqttobject
    */
    disconnectfrombroker = (mId: string) : void => {
        let index = this.getmqttIndex(mId);
        if (index === -1) return;
        let mqttobject = this.state.mqttobjects[index];
        if (mqttobject.client === null) return;
        if (! mqttobject.client.connected) return;
        mqttobject.client.end();
        this.updatemqttstatus(index, false);
    }

    /**
     * [MQTT CALLBACK]
     * Called when client receives a message
     * 1. If destination is signaldestination -> Invoke props.signalcallback() function
     * @param topic - Destination (topic) of the message
     * @param payload - Content of the message
     * @param mId - mId of mqttobject which the recipient (client) belongs to
    */
    mqttonmessage = (topic: string, payload: Buffer, mId: string) : void => {
        let index = this.getmqttIndex(mId);
        if (index === -1) return;
        let mqttobj = this.state.mqttobjects[index];
        let signaldestination = mqttobj.signaldestination;
        if (topic === signaldestination)
            this.props.signalcallback();
    }

    /**
     * [MQTT CALLBACK]
     * Called when client is connected to broker
     * 1. Get the index of mqttobject based of mId
     * 2. Subscribe to signaldestination
     * 3. Set connection status of mqttobject to true
     * @param mId - mId of mqttobject which the client belongs to
    */
    mqttonconnect = (mId: string) : void => {
        let index = this.getmqttIndex(mId);
        if (index === -1) return;
        let mqttobject = this.state.mqttobjects[index];
        if (mqttobject.client !== null) {
            mqttobject.client.subscribe(mqttobject.signaldestination);
        }
        this.updatemqttstatus(index, true);
    }

    /**
    * [MQTT CALLBACK]
    * Called when connection is on error
    * 1. Get the index of mqttobject using mId
    * 2. Set connection status of mqttobject to false
    * @param mId - mId of mqttobject which the client belongs to
    */
    mqttonerror = (mId: string) : void => {
        let index = this.getmqttIndex(mId);
        if (index === -1) return;
        this.updatemqttstatus(index, false);
    }

    /**
     * [MQTT CALLBACK]
     * Called when connection is closed
    * 1. Get the index of mqttobject using mId
    * 2. Set connection status of mqttobject to false
     * @param mId - mId of mqttobject which the client belongs to
    */
    mqttonclose = (mId: string) : void => {
        let index = this.getmqttIndex(mId);
        if (index === -1) return;
        this.updatemqttstatus(index, false);
    }

    render = () => {
        return (
            <div>
                <MQTTConfigurationPanel 
                    active={this.state.modalactive}
                    index={this.state.indexOnchange}
                    mqtts={this.state.mqttobjects}
                    updatemqttdata={this.updatemqttdata}
                    closemodal={this.closemqttconfigurationpanel}
                />
                {this.state.mqttobjects.map((mqttobject: mqttObject, index: number) =>
                    <Row key={index}>
                        <MqttDetailsBar
                            canconnect={this.props.canconnect}
                            mId={mqttobject.mId}
                            brokerurl={mqttobject.brokerurl}
                            signaldestination={mqttobject.signaldestination}
                            connected={mqttobject.connected}
                            ondoubleclick={this.openmqttconfigurationpanel}
                            deleteconfiguration={this.deletemqttconfiguration}
                            connecttobroker={this.connecttobroker}
                            disconnectfrombroker={this.disconnectfrombroker}
                        />
                    </Row>
                )}
                <Row><Button block color='secondary' onClick={this.addnewmqttconfiguration}>ADD CONFIGURATION</Button></Row>
            </div>
        )
    }
}

// ===============================================================================================================

/**
 * MqttDetailsBar props
 * canconnect: If user is allowed to connect to mqtt broker
 * mId: MqttDetailsBar Id (unique & auto-increment)
 * brokerurl: broker URL client is going to connect 
 * signaldestination: Destination (topic) where signal will be received
 * connected: If client is connected to broker
 * ondoubleclick: Callback function if user double-clicks on detailsbar
 * deleteconfiguration: Callback function if user clicks on DELETE button on detailsbar
 * connecttobroker: Callback function if user clicks on CONNECT button on detailsbar
 * disconnectfrombroker: Callback function if user clicks on DISCONNECT button on detailsbar
*/
interface DetailsBarProps {
    canconnect: boolean;
    mId: string;
    brokerurl: string;
    signaldestination: string;
    connected: boolean;
    ondoubleclick: ((mId: string) => void);
    deleteconfiguration: ((mId: string) => void);
    connecttobroker: ((mId: string) => void);
    disconnectfrombroker: ((mId: string) => void);
}

class MqttDetailsBar extends Component <DetailsBarProps, {}> {

    render = () => {
        return (
        <div className='onoffmqttdetail-container' 
            style={{backgroundColor: this.props.connected? "#D9FFA7" : "#FFFFBB"}}
            onDoubleClick={() => this.props.ondoubleclick(this.props.mId)}
        >
            <div className='info'>BROKER: {this.props.brokerurl}</div>
            <div className='info'>SIGNAL: {this.props.signaldestination}</div>
            <Container>
                <Row>
                    <Col>
                        { ! this.props.connected && <Button block color='success' disabled={! this.props.canconnect} onClick={() => this.props.connecttobroker(this.props.mId)}>CONNECT</Button>}
                        { this.props.connected && <Button block color="warning" onClick={() => this.props.disconnectfrombroker(this.props.mId)}>DISCONNECT</Button>}
                    </Col>
                    <Col>
                        <Button block color='danger' onClick={() => this.props.deleteconfiguration(this.props.mId)}>DELETE</Button>
                    </Col>
                </Row>
            </Container>
        </div>
        )
    }
}
// ===============================================================================================================

/**
 * mqttdata (Mqttobject data that can be configured)
 * brokerurl: broker URL client is going to connect 
 * signaldestination: Destination (topic) where signal will be received
 * connected: If client is connected to broker
 *      - User are not allowed to modify data is client is connected
*/
interface mqttdata {
    brokerurl: string;
    signaldestination: string;
    connected: boolean;
}

/**
 * MQTTConfigurationPanel props
 * active: If configuration panel is active (visible)
 * index: Index of mqttobject to modify/update
 * mqtts: Array of mqttobject
 * updatemqttdata: Callback function when user clicks on UPDATE button
 * closemodal: Callback function when user clicks on CLOSE button
*/
interface configurationprops {
    active: boolean;
    index: number;
    mqtts: mqttdata[];
    updatemqttdata: ((url: string, signaldestination: string, index: number) => void);
    closemodal: (() => void);
}

/**
 *  MQTTConfigurationPanel states
 * brokerurl: broker URL client is going to connect 
 * signaldestination: Destination (topic) where signal will be received 
 * (NOTE THAT modification takes place in interstate which means mqttobject data will not be updated UNTIL user clicks on UPDATE button)
*/
interface configurationstates {
    brokerurl: string;
    signaldestination: string;
}

class MQTTConfigurationPanel extends Component <configurationprops, configurationstates>{

    constructor(props: configurationprops) {
        super(props);
        this.state = { brokerurl: "", signaldestination: "" }
    }

    /**
     * [MODAL CALLBACK]
     * Called when modal is opened
     * 1. Set initial data value
    */
    onopenedCallback = () : void => {
        this.setState (
            {
                brokerurl: this.props.mqtts[this.props.index].brokerurl,
                signaldestination: this.props.mqtts[this.props.index].signaldestination,
            }
        );
    }

    /**
     * [MODAL CALLBACK]
     * Called when modal is closed
     * 1. Reset data value
    */
    onclosedCallback = () : void => {
        this.setState({ brokerurl: "", signaldestination: "" });
    }
    
    /**
     * Called when value of brokerurl (input field) on change
     * 1. Update brokerurl (state)
     * @param event 
    */
    handlebrokerurlOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setState({ brokerurl: event.target.value });
    }

    /**
     * Called when value of signaldestination (input field) on change
     * 1. Update signaldestination (state)
     * @param event 
    */
    handlesignaldestinationOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setState({ signaldestination: event.target.value });
    }

    /**
     * Called when user clicks on UPDATE button -> Update (Overwrite) mqttobject data
     * 1. Invoke props.updatemqttdata() method
     * 2. Invoke props.closemodal() method
    */
    updatemqttdata = () : void => {
        this.props.updatemqttdata(this.state.brokerurl, this.state.signaldestination, this.props.index);
        this.props.closemodal();
    }

    render = () => {
        return (
            <Modal centered isOpen={this.props.active} onOpened={this.onopenedCallback} onClosed={this.onclosedCallback}>
                <ModalHeader>MQTT CONFIGURATIONS</ModalHeader>
                { this.props.index !== -1 &&
                    <>
                        <ModalBody>
                            <Form>
                                <FormGroup>
                                    <Label for="brokerurl">BROKER URL</Label>
                                    <Input type='text' name='brokerurl' value={this.state.brokerurl} onChange={this.handlebrokerurlOnchange} disabled={this.props.mqtts[this.props.index].connected}></Input>
                                </FormGroup>
                                <FormGroup>
                                    <Label for="signaldestination">START SIGNAL SUBSCRIPTION</Label>
                                    <Input type='text' name='signaldestination' value={this.state.signaldestination} onChange={this.handlesignaldestinationOnchange} disabled={this.props.mqtts[this.props.index].connected}></Input>
                                </FormGroup>
                            </Form>
                        </ModalBody>
                        <ModalFooter>
                            <Container>
                                <Row>
                                    <Button block color='primary' onClick={this.updatemqttdata} disabled={this.props.mqtts[this.props.index].connected}>UPDATE</Button>
                                </Row>
                                <Row>
                                    <Button block color='secondary' onClick={this.props.closemodal}>CLOSE</Button>
                                </Row>
                            </Container>
                        </ModalFooter>
                    </>
                }
            </Modal>
        )
    }
}