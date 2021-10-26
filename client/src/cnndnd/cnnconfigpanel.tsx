import React, {Component} from 'react'
import { Modal, ModalHeader, ModalBody, ModalFooter, Form, FormGroup, Label, Input, Button } from 'reactstrap'
import {ActivationFunctionTypes, Dictionary, OptimizationAlgorithmTypes, PoolingType 
, LossFunctionTypes, Flipmode } from "../globalcomponents/interfaces"

/**
 * CNN Configuration Panel props
 * onNodeUpdate - Callback function to update node data 
*/
interface ConfigurationPanelProps {
    onNodeUpdate: (data: Dictionary) => void;
}

/**
 * CNN Configuration Panel states
 * active: If configuration modal is active (visible)
 * data: Node's data to be configured/modified
*/
interface ConfigurationPanelStates {
    active: boolean;
    data: Dictionary;
}


export default class ConfigurationPanel extends Component <ConfigurationPanelProps, ConfigurationPanelStates>{

    // Constructor
    constructor(props : ConfigurationPanelProps) {
        super(props);
        this.state = {
            active: false,
            data: {}
        }
    }

    /**
     * Open CNN configuration panel (modal) and set initial data value
     * @param data - Node data before configuration modal is opened
    */
    showmodal = (data: Dictionary) : void => {
        this.setState({ active : true , data });
    }

    /**
     * Handle data on change event
     * @param event 
    */
    handledataOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setState({
            data : { ...this.state.data, [event.target.name] : event.target.value }
        });
    }

    /**
     * 1. Update node's data using props callback function
     * 2. Close (hide) configuration modal
    */
    updatenodedata = () : void => {
        this.props.onNodeUpdate(this.state.data);
        this.hidemodal();
    }

    /**
     * 1. Hide modal
     * 2. Reset data (state) to empty dictionary
     */
    hidemodal = () : void => {
        this.setState( { active : false, data : {} } )
    }

    render = () => {
        return (
            <>
                <Modal isOpen={this.state.active} centered={true}>
                    <ModalHeader>Node Configurations</ModalHeader>
                    <ModalBody>
                        <Form>
                            { Object.entries(this.state.data).map( ([config, value]) => 
                                {
                                    switch(config)
                                    {
                                        case "name":
                                        case "path":
                                        case "filename":
                                            return (
                                                <FormGroup key={config}>
                                                    <Label for={config}>{config.toUpperCase()}</Label>
                                                    <Input type='text' name={config} id={config} value={value} onChange={this.handledataOnchange} />
                                                </FormGroup>
                                            )
                                        case "nIn":
                                            return (
                                                <FormGroup key={config}>
                                                    <Label for="nIn">nIn</Label>
                                                    <Input type='number' name='nIn' id='nIn' value={value} onChange={this.handledataOnchange} disabled={true}/>
                                                </FormGroup>
                                            )
                                        case "learningrate":
                                        case "imagewidth":
                                        case "imageheight":
                                        case "channels":
                                        case "numLabels":
                                        case "batchsize":
                                        case "seed":
                                        case "kernalx":
                                        case "kernaly":
                                        case "stridex":
                                        case "stridey":
                                        case "angle":
                                        case "nOut":
                                        case "epochs":
                                        case "scoreListener":
                                            return (
                                                <FormGroup key={config}>
                                                    <Label for={config}>{config.toUpperCase()}</Label>
                                                    <Input type='number' name={config} id={config} value={value} onChange={this.handledataOnchange}/>
                                                </FormGroup>
                                            )
                                        case "activationfunction":
                                            return (
                                                <FormGroup key={config}>
                                                    <Label for={config}>ACTIVATION FUNCTION</Label>
                                                    <Input type='select' name={config} id={config} value={value} onChange={this.handledataOnchange}>
                                                        { Object.keys(ActivationFunctionTypes).map( type => 
                                                                <option key={type} value={ActivationFunctionTypes[type]}>{type}</option>
                                                        )}
                                                    </Input>
                                            </FormGroup>
                                            )
                                        case "optimizationalgorithm":
                                            return (
                                                <FormGroup key={config}>
                                                    <Label for={config}>OPTIMIZATION ALGORITHM</Label>
                                                    <Input type='select' name={config} id={config} value={value} onChange={this.handledataOnchange}>
                                                        { Object.keys(OptimizationAlgorithmTypes).map( type => 
                                                                <option key={type} value={OptimizationAlgorithmTypes[type]}>{type}</option>
                                                        )}
                                                    </Input>
                                                </FormGroup>
                                            )
                                        case "poolingtype":
                                            return (
                                                <FormGroup key={config}>
                                                    <Label for={config}>ACTIVATION FUNCTION</Label>
                                                    <Input type='select' name={config} id={config} value={value} onChange={this.handledataOnchange}>
                                                        { Object.keys(PoolingType).map( type => 
                                                                <option key={type} value={PoolingType[type]}>{type}</option>
                                                        )}
                                                    </Input>
                                                </FormGroup>
                                            )
                                        case "lossfunction":
                                            return (
                                                <FormGroup key={config}>
                                                    <Label for={config}>LOSS FUNCTION</Label>
                                                    <Input type='select' name={config} id={config} value={value} onChange={this.handledataOnchange} >
                                                        { Object.keys(LossFunctionTypes).map( lossfunction => 
                                                                <option key={lossfunction} value={LossFunctionTypes[lossfunction]}>{lossfunction}</option>
                                                        )}
                                                    </Input>
                                                </FormGroup>
                                            )
                                        // PROBLEM WITH FLIP MODE 
                                        case "flipmode":
                                            return (
                                                <FormGroup key={config}>
                                                    <Label for={config}>FLIP MODE</Label>
                                                    <Input type='select' name={config} id={config} value={value} onChange={this.handledataOnchange}>
                                                        { Object.keys(Flipmode).map( flipmode => 
                                                                <option key={flipmode} value={Flipmode[flipmode]}>{flipmode}</option>
                                                        )}
                                                        {/* { Object.keys(Flipmode).filter( key => ! isNaN(Number(key)) ).map( (mode) => {
                                                            return (
                                                                <option key={mode} value={Flipmode[mode]}>{Flipmode[mode]}</option>)
                                                            }
                                                        )} */}
                                                    </Input>
                                                </FormGroup>
                                            )
                                        default:
                                            return <></>
                                    }
                                }
                            ) }
                        </Form>
                    </ModalBody>

                    <ModalFooter>
                        <Button color='info' onClick={this.updatenodedata}>Update</Button>
                        <Button color="secondary" onClick={this.hidemodal}>Close</Button>
                    </ModalFooter>
                </Modal>
            </>
        )
    }
}