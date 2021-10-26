import React, {Component} from "react"
import {Modal, ModalHeader, ModalBody, Button} from "reactstrap"
import CloseIcon from '@mui/icons-material/Close';
import "./glberrormodal.css"

/**
 * Error modal props
*/
interface EMProps {

}

/**
 * Error modal states
 * modelactive: If error modal is active (visible)
 * errorheader: Header of error modal
 * errormessage: Message of error modal
*/
interface EMStates {
    modelactive: boolean; 
    errorheader: string; 
    errormessage: string;
}

export default class ErrorModal extends Component <EMProps, EMStates> {
    constructor(props: EMProps) {
        super(props);
        this.state = { modelactive: false, errorheader: "", errormessage: "" }
    }

    /**
     * [UPDATE COMPONENT'S STATE] modelactive
     * @param modelactive 
    */
    setmodalactive = (modelactive: boolean) : void => {
        this.setState({ modelactive });
    }

    /**
     * [UPDATE COMPONENT'S STATE] errorheader
     * @param errorheader 
    */
    seterrorheader = (errorheader: string) : void => {
        this.setState({ errorheader });
    }

    /**
     * [UPDATE COMPONENT'S STATE] errormessage
     * @param errormessage 
    */
    seterrormessage = (errormessage: string) : void => {
        this.setState({ errormessage });
    }

    /**
     * 1. Open error modal
     * 2. Set header and message of error modal
     * @param errorheader 
     * @param errormessage 
    */
    openmodal = (errorheader: string, errormessage: string) : void => {
        this.setmodalactive(true)
        this.seterrorheader(errorheader)
        this.seterrormessage(errormessage)
    }

    /**
     * 1. Hide error modal
     * 2. Reset header and message of error modal
    */
    hidemodal = () : void => {
        this.setmodalactive(false);
        this.seterrorheader("")
        this.seterrormessage("")
    }

    render = () => {
        return (
            <Modal className='error-modal' isOpen={this.state.modelactive} centered={true}>
                <ModalHeader className='modal-header'>
                    <div className='icon-box'>
                        <CloseIcon sx={{fontSize: 64}} />
                    </div>
                </ModalHeader>
                <ModalBody className='text-center'>
                    <h4>{this.state.errorheader}</h4>
                    <p>{this.state.errormessage}</p>
                    <Button className='btn' onClick={this.hidemodal}>
                        <span>Continue</span>
                    </Button>
                </ModalBody>
            </Modal>
        )
    }
}