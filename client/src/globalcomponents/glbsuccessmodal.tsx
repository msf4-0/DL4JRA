import React, {Component} from "react"
import DoneIcon from '@mui/icons-material/Done';
import {Modal, ModalHeader, ModalBody, Button} from "reactstrap"
import "./glbsuccessmodal.css"

/**
 * Success modal props 
*/
interface SMProps {

}

/**
 * Success modal states
 * modalactive: If success modal is active (visible)
 * successmessage: message in success modal 
*/
interface SMStates {
    modalactive: boolean;
    successmessage: string;
}

export default class SuccessModal extends Component <SMProps, SMStates> {
    
    constructor(props: SMProps) {
        super(props);
        this.state = { 
            modalactive: false,
            successmessage: "",
        }
    }   

    /**
     * [UPDATE COMPONENT'S STATE] modalactive
     * @param modalactive 
    */
    setmodalactive = (modalactive: boolean) : void => {
        this.setState({ modalactive });
    }

    /**
     * [UPDATE COMPONENT'S STATE] successmessage
     * @param successmessage 
    */
    setsuccessmessage = (successmessage: string) : void => {
        this.setState({ successmessage });
    }

    /**
     * 1. Open success modal
     * 2. Set successmessage
     * @param successmessage 
    */
    openmodal = (successmessage: string) : void => {
        this.setmodalactive(true);
        this.setsuccessmessage(successmessage);
    }

    /**
     * 1. Hide success modal
     * 2. Clear successmessage
    */
    closemodal = () : void => {
        this.setmodalactive(false);
        this.setsuccessmessage("");
    }

    render = () => {
        return (
            <Modal className='success-modal' isOpen={this.state.modalactive} centered>
                <ModalHeader className="modal-header">
                    <div className='icon-box'>
                        <DoneIcon sx={{fontSize: 64}}/>
                    </div>
                </ModalHeader>
                <ModalBody className='text-center'>
                    <h4>Great</h4>
                    <p>{this.state.successmessage}</p>
                    <Button className='btn' onClick={this.closemodal}>
                        <span>Continue</span>
                    </Button>
                </ModalBody>
            </Modal>
        )
    }
}