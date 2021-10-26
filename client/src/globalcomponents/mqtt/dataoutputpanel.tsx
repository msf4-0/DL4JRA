import React, {Component} from 'react'
import mqtt, { MqttClient } from "mqtt"
import { Modal, ModalHeader, ModalBody, ModalFooter, Form, FormGroup, Label, Input, Button, Container, Row, Col } from 'reactstrap'
import "./mqttpanels.css"

/**
 * MQTT Configuration object
 * client: MqttClient class object
 * mId: MqttDetailsBar Id (unique & auto-increment)
 * brokerurl: broker URL client is going to connect 
 * targetdestination: Destination (topic) to send data
 * connected: If client is connected to broker
*/
interface mqttObject {
    mId: string;
    client: null | MqttClient;
    brokerurl: string;
    targetdestination: string;
    connected: boolean;
}

/**
 * DataOutputPanel props
 * defaultobject: Default value of new mqttobject
*/
interface DataOutputPanelProps {
    defaultobject: mqttObject;
}


/**
 * DataOutputPanel states
 * mqttobjects: Array to store mqttobject
 * confmodalactive: If DataOutputPanel configuration modal is active (visible) (modal which user can modify brokerURL & destination)
 * indexOnchange: Id of mqttobject which is currently in modification
*/
interface DataOutputPanelStates {
    mqttobjects: mqttObject[];
    confmodalactive: boolean;
    indexOnchange : number;
}

export default class DataOutputPanel extends Component <DataOutputPanelProps, DataOutputPanelStates> {
    /**
     * DataOutputPanel class properties
     * uniqueId: current Id of mqttobject (unique + auto increment)
     */
    uniqueId: number;

    static defaultProps = {
        defaultobject: { client: null, brokerurl: "ws://broker.emqx.io:8083/mqtt", targetdestination: "targetdestination", connected: false }
    }

    // Constructor
    constructor(props: DataOutputPanelProps) {
        super(props);
        this.uniqueId = 0;
        this.state = { mqttobjects: [], confmodalactive: false, indexOnchange: -1, }
    }

    /**
     * Generate unique Id for mqttobject
     * @returns Id
    */
    generateuniqueId = () : number => {
        return ++this.uniqueId;
    }

    /**
     * Get the index of mqttobject (with matching mId) in mqttobjects array (state)
     * @param mId - mId of mqttobject
     * @returns Index of mqttobject in mqttobjects array, -1 if not found
    */
    getIndex = (mId: string) : number => {
        return this.state.mqttobjects.findIndex( object => object.mId === mId );
    }

    /**
     * Update mqttobject connected (boolean) status
     * @param index - Index of mqttobject
     * @param connected - If client is connected to broker
    */
    updateconnectionstatus = (index: number, connected: boolean) : void => {
        this.setState({
            mqttobjects:
            [
                ...this.state.mqttobjects.slice(0, index),
                {
                    ...this.state.mqttobjects[index], connected,
                },
                ...this.state.mqttobjects.slice(index + 1)
            ]
        })
    }

    /**
     * Add new mqttobject to mqttobjects array (state)
     * 1. Create new mqttobject (default as props.defaultobject)
     * 2. Generate unique mId
     * 3. Update state
    */
    addnewconfiguration = () : void => {
        let newobject = this.props.defaultobject;
        newobject['mId'] = this.generateuniqueId().toString();
        this.setState({ mqttobjects: [ ...this.state.mqttobjects, newobject ]})
    }

    /**
     * Delete mqttobject from mqttobjects array
     * 1. Disconnect client if connected
     * 2. Get the index of mqttobject in mqttobjects array -> update state 
     * @param mId - mId of mqttobject to be removed
    */
    deleteconfiguration = (mId: string) : void => {
        this.disconnectfrombroker(mId);
        let index = this.getIndex(mId);
        this.setState({ 
            mqttobjects:
            [
                ...this.state.mqttobjects.slice(0, index),
                ...this.state.mqttobjects.slice(index + 1)
            ]
        })
    }

    /**
     * Update mqttobject data
     * @param index - Index of mqttobject which data is updated
     * @param brokerurl - Updated value of brokerurl
     * @param targetdestination - Updated value of targetdestination
    */
    updatemqttdata = (index: number, brokerurl: string, targetdestination: string) : void => {
        this.setState({
            mqttobjects:
            [
                ...this.state.mqttobjects.slice(0, index),
                {
                    ...this.state.mqttobjects[index],
                    brokerurl, targetdestination,
                },
                ...this.state.mqttobjects.slice(index + 1)
            ]
        })
    }

    /**
     * Connect mqttobject's client to broker
     * 1. Get the index of mqttobject
     * 2. Check if mqttobject's client is already connected
     * 3. Check if brokerurl, target destination is OK (not blank)
     * 4. Set mqtt's connect, close & error callback function
     * 5. Connect client to mqtt broker
     * @param mId - mId of mqttobject which the client belongs to
    */
    connecttobroker = (mId: string) : void => {
        let index = this.getIndex(mId);
        if (index === -1) return;
        let mqttobject = this.state.mqttobjects[index];
        if (mqttobject.connected) return;
        if (mqttobject.brokerurl === "" || mqttobject.targetdestination === "") return;
        mqttobject.client = mqtt.connect(mqttobject.brokerurl, { reconnectPeriod: 0 });
        mqttobject.client.once("connect", () => this.onconnectcallback(mId));
        mqttobject.client.once("close", () => this.onclosecallback(mId));
        mqttobject.client.once("error", () => this.onerrorcallback(mId));
    }

    /**
     * [MQTT CALLBACK]
     * Called when client is connected to broker
     * 1. Get the index of mqttobject based of mId
     * 2. Subscribe to signaldestination
     * 3. Set connection status of mqttobject to true
     * @param mId - mId of mqttobject which the client belongs to
    */
    onconnectcallback = (mId: string) : void => {
        let index = this.getIndex(mId);
        if (index === -1) return;
        let mqttobject = this.state.mqttobjects[index];
        if (mqttobject.client === null) return;
        this.updateconnectionstatus(index, true);
    }

    /**
     * Disconnect mqttobject's client from broker
     * 1. Get the index of mqttobject using mId
     * 2. Disconnect client from brokerurl
     * 3. Update mqttobject connected status 
     * @param mId - mId of mqttobject
    */
    disconnectfrombroker = (mId: string) : void => {
        let index = this.getIndex(mId);
        if (index === -1) return;
        let mqttobject = this.state.mqttobjects[index];
        if (mqttobject.client === null) return;
        if (! mqttobject.client.connected) return;
        mqttobject.client.end();
        this.updateconnectionstatus(index, false);
    }

    /**
     * [MQTT CALLBACK]
     * Called when connection is closed
    * 1. Get the index of mqttobject using mId
    * 2. Set connection status of mqttobject to false
     * @param mId - mId of mqttobject which the client belongs to
    */
    onclosecallback = (mId: string) : void => {
        let index = this.getIndex(mId);
        if (index === -1) return;
        this.updateconnectionstatus(index, false);
    }

    /**
    * [MQTT CALLBACK]
    * Called when connection is on error
    * 1. Get the index of mqttobject using mId
    * 2. Set connection status of mqttobject to false
    * @param mId - mId of mqttobject which the client belongs to
    */
    onerrorcallback = (mId: string) : void => {
        let index = this.getIndex(mId);
        if (index === -1) return;
        this.updateconnectionstatus(index, false);
    }

    /**
     * 1. Get the index of mqttobject using mId 
     * 2. Set indexOnchange to the index 
     * 3. Open mqttobject configuration panel
     * @param mId - mId of mqttobject which is in modification
    */
    openconfigurationmodal = (mId: string) : void => {
        let index = this.getIndex(mId);
        if (index === -1) return;
        this.setState({ indexOnchange: index, confmodalactive: true });
    }

    /**
     * 1. Set streamingmqttIfonChanged to -1 
     * 2. Close mqttobject configuration panel
    */
    closeconfigurationmodal = () : void => {
        this.setState({ indexOnchange: -1, confmodalactive: false });
    }

    /**
     * Send message if client is connected
     * 1. Loop through all client in mqttobjects
     *      - If client is connected to broker, send message to target destination
     * @param message - Message to send
    */
    sendmessage = (message: string) : void => {
        for (let index = 0; index < this.state.mqttobjects.length; index ++) {
            let mqttobject = this.state.mqttobjects[index];
            if (mqttobject.client === null) continue;
            if (! mqttobject.client.connected) continue;
            mqttobject.client.publish(mqttobject.targetdestination, message);
        }
    }

    render = () => {
        return (
            <div>
                <ConfigurationPanel
                    active={this.state.confmodalactive}
                    index={this.state.indexOnchange}
                    mqtts={this.state.mqttobjects}
                    updatemqttdata={this.updatemqttdata}
                    closemodal={this.closeconfigurationmodal}
                />
                { this.state.mqttobjects.map ((mqttobject: mqttObject, index: number) => 
                    <Row key={index}>
                        <MqttDetailsBar 
                            canconnect={true}
                            mId={mqttobject.mId}
                            brokerurl={mqttobject.brokerurl}
                            targetdestination={mqttobject.targetdestination}
                            connected={mqttobject.connected}
                            ondoubleclick={this.openconfigurationmodal}
                            deleteconfiguration={this.deleteconfiguration}
                            connecttobroker={this.connecttobroker}
                            disconnectfrombroker={this.disconnectfrombroker}
                        />
                    </Row>
                )}
                <Row><Button block color='secondary' onClick={this.addnewconfiguration}>ADD CONFIGURATION</Button></Row>
            </div>
        )
    }
}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * MqttDetailsBar props
 * canconnect: If user is allowed to connect to mqtt broker
 * mId: MqttDetailsBar Id (unique & auto-increment)
 * brokerurl: broker URL client is going to connect 
 * targetdestination: Destination (topic) to send data
 * connected: If client is connected to broker
 * ondoubleclick: Callback function if user double-clicks on detailsbar
 * deleteconfiguration: Callback function if user clicks on DELETE button on detailsbar
 * connecttobroker: Callback function if user clicks on CONNECT button on detailsbar
 * disconnectfrombroker: Callback function if user clicks on DISCONNECT button on detailsbar
*/
interface confcomponentprops {
    canconnect: boolean;
    mId: string;
    brokerurl: string;
    targetdestination: string;
    connected: boolean;
    ondoubleclick: ((mId: string) => void);
    deleteconfiguration: ((mId: string) => void);
    connecttobroker: ((mId: string) => void);
    disconnectfrombroker: ((mId: string) => void);
}

class MqttDetailsBar extends Component <confcomponentprops, {}> {
    render = () => {
        return (
        <div className='onoffmqttdetail-container' 
            style={{backgroundColor: this.props.connected? "#D9FFA7" : "#FFFFBB"}}
            onDoubleClick={() => this.props.ondoubleclick(this.props.mId)}
        >
            <div className='info'>BROKER: {this.props.brokerurl}</div>
            <div className='info'>DESTINATION: {this.props.targetdestination}</div>
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


/////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * mqttdata (Mqttobject data that can be configured)
 * brokerurl: broker URL client is going to connect 
 * targetdestination: Destination (topic) to send data
 * connected: If client is connected to broker
 *      - User are not allowed to modify data is client is connected
*/
interface mqttdata { 
    brokerurl: string, 
    targetdestination: string, 
    connected: boolean 
}

/**
 * ConfigurationPanel props
 * active: If configuration panel is active (visible)
 * index: Index of mqttobject to modify/update
 * mqtts: Array of mqttobject
 * updatemqttdata: Callback function when user clicks on UPDATE button
 * closemodal: Callback function when user clicks on CLOSE button
*/
interface confprops { 
    active: boolean, 
    index: number, 
    mqtts: mqttdata[], 
    updatemqttdata: ((index: number, brokerurl: string, targetdestination: string) => void),
    closemodal: (() => void)
}

/**
 *  ConfigurationPanel states
 * brokerurl: broker URL client is going to connect 
 * targetdestination: Destination (topic) to send data
 * (NOTE THAT modification takes place in interstate which means mqttobject data will not be updated UNTIL user clicks on UPDATE button)
*/
interface confstates { 
    brokerurl: string, 
    targetdestination: string 
}

class ConfigurationPanel extends Component <confprops, confstates> {
    // Constructor
    constructor(props: confprops) {
        super(props);
        this.state = { brokerurl: "", targetdestination: "" }
    }

    /**
     * [MODAL CALLBACK]
     * Called when modal is opened
     * 1. Set initial data value
    */
    onopened = () : void => {
        this.setState(
            { 
                brokerurl: this.props.mqtts[this.props.index].brokerurl,
                targetdestination: this.props.mqtts[this.props.index].targetdestination
            }
        )
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
     * Called when value of targetdestination (input field) on change
     * 1. Update targetdestination (state)
     * @param event 
    */
    handletargetdestinationOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setState({ targetdestination: event.target.value });
    }

    /**
     * Called when user clicks on UPDATE button -> Update (Overwrite) mqttobject data
     * 1. Invoke props.updatemqttdata() method
     * 2. Invoke props.closemodal() method
    */
    updatedata = () : void => {
        this.props.updatemqttdata(this.props.index, this.state.brokerurl, this.state.targetdestination);
        this.props.closemodal();
    }

    render = () => {
        return (
            <Modal centered isOpen={this.props.active} onOpened={this.onopened}>
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
                                <Label for="targetdestination">TARGET DESTINATION</Label>
                                <Input type='text' name='targetdestination' value={this.state.targetdestination} onChange={this.handletargetdestinationOnchange} disabled={this.props.mqtts[this.props.index].connected}></Input>
                            </FormGroup>
                        </Form>
                    </ModalBody>
                    <ModalFooter>
                        <Container>
                            <Row>
                                <Button block color='primary' onClick={this.updatedata} disabled={this.props.mqtts[this.props.index].connected}>UPDATE</Button>
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