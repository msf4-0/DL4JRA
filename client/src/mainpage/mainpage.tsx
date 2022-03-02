import React, {Component} from 'react'
import { Card, CardTitle, CardText, Container, Row, Col, Button} from 'reactstrap'
import { History, LocationState } from 'history'
import {RouteComponentProps} from 'react-router-dom'
import "./mainpage.css"

interface MainpageProps extends RouteComponentProps {
    history: History<LocationState>;
}

interface MainpageStates {

}

export default class Mainpage extends Component <MainpageProps, MainpageStates>{

    // [NAVIGATION] CNN (MULTITAB)
    navigatecnnmultitabpage = () => {
        this.props.history.push("/cnnmultitab")
    }

    // [NAVIGATION] OBJECT DETECTION (PRETRAINED)
    navigateodpretrainedpage = () => {
        this.props.history.push("/odpretrained")
    }

    // [NAVIGATION] DATASET GENERATION PAGE
    navigatedatasetgenerationpage = () => {
        this.props.history.push("/datasetgenerator")
    }

    // [NAVIGATION] CLASSIFICATION PAGE
    navigateclassificationpage = () => {
        this.props.history.push('/classification')
    }

    // [NAVIGATION] - MQTT SIGNALLING PAGE
    navigatemqttsignallingpage = () => {
        this.props.history.push('/sendmqttsignal')
    }

    render = () => {
        return (
            <Container className='mainpage-content'>
                <Row>
                    <Col sm={6}>
                        <Card body>
                            <CardTitle tag="h5">GENERATE DATASET</CardTitle>
                            <CardText>Generate dataset (images) through webcam capturing</CardText>
                            <Button color='primary' onClick={this.navigatedatasetgenerationpage}>Continue</Button>
                        </Card>
                    </Col>
                    <Col sm={6}>
                        <Card body>
                            <CardTitle tag="h5">NEURAL NETWORK </CardTitle>
                            <CardText>-Train and validate NN for image and CSV input classification</CardText>
                            <CardText>-Image Segmentation with importing pretrained model (UNET)</CardText>
                            <CardText>-ReTrain pretrained model (TINYYOLO) for obeject detection</CardText>
                            <Button color='primary' onClick={this.navigatecnnmultitabpage}>Continue</Button>
                        </Card>
                    </Col>
                </Row>

                <Row>
                <Col sm={6}>
                        <Card body>
                            <CardTitle tag="h5">OBJECT DETECTION</CardTitle>
                            <CardText>Detect object through webcam using pretrained model</CardText>
                            <Button color='primary' onClick={this.navigateodpretrainedpage}>Continue</Button>
                        </Card>
                    </Col>
                    <Col sm={6}>
                        <Card body>
                            <CardTitle tag="h5">OBJECT CLASSIFICATION</CardTitle>
                            <CardText>Classify images (through webcam) using CNN</CardText>
                            <Button color='primary' onClick={this.navigateclassificationpage}>Continue</Button>
                        </Card>
                    </Col>
                </Row>

                <Row>
                    <Col sm={6}>
                        <Card body>
                            <CardTitle tag="h5">MQTT SIGNALLING</CardTitle>
                            <CardText>CONNEC TO TEST BROKER AND SEND OUT TEST SIGNAL</CardText>
                            <Button color='primary' onClick={this.navigatemqttsignallingpage}>Continue</Button>
                        </Card>
                    </Col>
                </Row>
                
            </Container>
        )
    }
}
