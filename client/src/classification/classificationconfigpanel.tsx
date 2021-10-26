import React, {Component} from 'react'
import { Modal, ModalHeader, ModalBody, ModalFooter, Form, FormGroup, FormFeedback, Label, Input, Container, Row, Col, Button } from 'reactstrap'

/**
 * Data of classifer that can be configured
 * name: Name (filename) of classifier
 * inputwidth: Webcam's screenshot width (must match with the input width of classifier) 
 * inputheight: Webcam's screenshot height (must match with the input height of classifier)
 * classnum: Number of output (= Number of classes of classifier) (CANNOT be configured)
 * labels: Label (name) of each classes
*/
interface ClassifierData {
    name: string;
    inputwidth: number; 
    inputheight: number;
    classnum: number; 
    labels: string[]; 
}

/**
 * Classification Configuration Panel (CCP) props
 * active: If modal is active (visible)
 * databeforemodification: Classifier's data before configuration panel is opened
 * closeccpanel: Callback function to close (hide) modal
 * updatemodeldata: Callback function to update classifier's data
*/
interface CCPProps {
    active: boolean;
    databeforemodification: ClassifierData;
    closeccpanel: () => void;
    updatemodeldata: (data: ClassifierData) => void;
}

/**
 * Classification Configuration Panel (CCP) states
 * data: Classifier's data in modification  
*/
interface CCPStates {
    data: ClassifierData
}

export default class CCPanel extends Component <CCPProps, CCPStates> {

    constructor(props: CCPProps) {
        super(props);
        this.state = { data: { name: "", inputwidth: 200, inputheight: 200, classnum: 0, labels: [] }, };
    }

    /**
     * Called when configuration panel (modal) is opened
     * Set data (state) to data before modification (prop) 
    */
    onOpened = () : void => {
        this.setState({ data: this.props.databeforemodification });
    }

    /**
     * Handle data (name, inputwidth, inputheight) on change event
     * @param event 
    */
    handleinputOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setState({ data: { ...this.state.data, [event.target.name]: event.target.value }})
    }

    /**
     * Handle data (labels) on change event
     * @param event 
    */
    handlelabelsOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        let indexOnchange = Number(event.target.name);
        this.setState({ data: { ...this.state.data, labels: 
            [
                ...this.state.data.labels.slice(0, indexOnchange),
                event.target.value,
                ...this.state.data.labels.slice(indexOnchange + 1)
            ]
        }})
    }

    /**
     * Called when <<UPDATE>> button is clicked
     * Update classifier's data and close panel
    */
    handleupdate = () : void => {
        this.props.updatemodeldata(this.state.data);
        this.props.closeccpanel();
    }

    // RENDER
    render = () => {
        return (
            <Modal isOpen={this.props.active} centered={true} onOpened={this.onOpened}>
                <ModalHeader>Classifier configuration</ModalHeader>
                <ModalBody>
                    <Form>
                        <Container>
                            <Row>
                                <Col>
                                    <FormGroup>
                                        <Label for='name'>CLASSIFIER FILENAME</Label>
                                        <Input disabled name='name' value={this.state.data.name} onChange={this.handleinputOnchange}/>
                                    </FormGroup>
                                </Col>
                            </Row>
                            <Row>
                                <Col md={6}>
                                    <FormGroup>
                                        <Label for='inputwidth'>INPUT WIDTH</Label>
                                        <Input type='number' name='inputwidth' value={this.state.data.inputwidth} invalid={this.state.data.inputwidth < 100} onChange={this.handleinputOnchange}></Input>
                                        <FormFeedback valid={false}>Webcam screenshot width has a minimum of 100px</FormFeedback>
                                    </FormGroup>
                                </Col>
                                <Col md={6}>
                                    <FormGroup>
                                        <Label for='inputheight'>INPUT HEIGHT</Label>
                                        <Input type='number' name='inputheight' value={this.state.data.inputheight} invalid={this.state.data.inputheight < 100} onChange={this.handleinputOnchange}></Input>
                                        <FormFeedback valid={false}>Webcam screenshot height has a minimum of 100px</FormFeedback>
                                    </FormGroup>
                                </Col>
                            </Row>
                            { this.state.data.labels.map( (labelname: string, index: number) => 
                                <Row key={index}>
                                    <Col>
                                        <Label for={index.toString()}>LABEL-{index}</Label>
                                        <Input type='text' name={index.toString()} value={labelname} onChange={this.handlelabelsOnchange}/>
                                    </Col>
                                </Row>
                            )
                            }
                        </Container>
                    </Form>
                </ModalBody>
                <ModalFooter>
                    <Button block color='primary' onClick={this.handleupdate}>UPDATE</Button>
                    <Button block onClick={this.props.closeccpanel}>CLOSE</Button>
                </ModalFooter>
            </Modal>
        )
    }






}