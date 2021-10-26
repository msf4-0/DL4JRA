import React, { Component } from "react"
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Progress, ListGroup, ListGroupItem, Container, Row } from 'reactstrap'

/**
 * Progress modal props
 * progressmodalactive: If progress modal is active (visible)
 * progressmodalbarcolor: Color of bar for progress modal
 * progressmodalanimated: If the progress bar of progress modal is animated
 * progressmodalcanclose: If user can close the progress modal (FALSE if sequence is running)
 * progresscanabort: If user can abort action
 * progressmodalheader: Header of progress modal
 * progressmodalsubheader: Subheader of progress modal
 * progressmodalmessages: Messages appeared in progress modal
 * currentprogress: Current progress
 * maxprogress: Max progress 
 * closemodal: Callback function to close progress modal
 * abortprogress: Callback function when user clicks on ABORT button
*/
interface IProgressmodalprops {
    progressmodalactive: boolean;
    progressbarcolor: 'success' | 'danger';
    progressbaranimated: boolean;
    progressmodalcanclose: boolean;
    progresscanabort: boolean;
    progressmodalheader: string;
    progressmodalsubheader: string;
    progressmodalmessages: string[];
    currentprogress: number;
    maxprogress: number;
    closemodal: (() => void);
    abortprogress: (() => void);
}

/**
 * Progress modal states
*/
interface IProgressmodalstates {

}

export default class ProgressModal extends Component <IProgressmodalprops, IProgressmodalstates> {

    /**
     * Calculate the progress percentage
     * Percentage = (current / max) x 100%
     * @returns Progress percentage
     */
    calculatepercentage = () : number => {
        return Math.floor((this.props.currentprogress / this.props.maxprogress) * 100);
    }

    render = () => {
        return (
            <Modal centered={true} isOpen={this.props.progressmodalactive}>
                <ModalHeader>{this.props.progressmodalheader}</ModalHeader>
                <ModalBody>
                    <div>CURRENT TASK: {this.props.progressmodalsubheader}</div>
                    <Progress animated={this.props.progressbaranimated} value={this.calculatepercentage()} color={this.props.progressbarcolor} />
                    <div style={{ marginTop: 5, height: 400, overflowY: "scroll" }}>
                        <ListGroup>
                        {
                            this.props.progressmodalmessages.map( (message, index) => 
                                <ListGroupItem key={index}>{message}</ListGroupItem>    
                            )
                        }
                        </ListGroup>
                    </div>
                </ModalBody>
                <ModalFooter>
                    <Container>
                        <Row><Button block color='success' onClick={this.props.closemodal} disabled={! this.props.progressmodalcanclose}>DONE</Button></Row>
                        <Row><Button block color='danger' onClick={this.props.abortprogress} disabled={! this.props.progresscanabort}>ABORT</Button></Row>
                    </Container>
                </ModalFooter>
            </Modal>
        )
    }



}