import React, {Component} from 'react'
import { Container, Row, Col } from 'reactstrap'
import "./glbbackend.css"

/**
 * Backend props
 * websocketconnected: If client is connected to server's websocket
 * backendiscpu: If server (DL4J) is using CPU as backend (FALSE if not connected to server's websocket)
 * backendisgpu: If server (DL4J) is using GPU as backend (FALSE if not connected to server's websocket)
*/
interface BackendProps {
    websocketconnected: boolean;
    backendisgpu: boolean;
    backendiscpu: boolean;
}

/**
 * Backend states
*/
interface BackendStates {

}

export default class DL4JBackend extends Component <BackendProps, BackendStates> {
    /**
     * DL4JBackend is a component used to show user which backend the server is currently using
     * 1. If client not connected to server, both CPU and GPU is not used (false)
     * 2. Background will turn green in color is server is using the particular resources for DL4J 
    */
    render = () => {
        return (
            <div className="dl4j-backend-container">
                <Container>
                    <Row>
                        <Col><div className="cpu-gpu-container" style={{ backgroundColor: this.props.websocketconnected && this.props.backendiscpu? "#DDFFA4" : "#A2A2A2" }}>CPU</div></Col>
                        <Col><div className="cpu-gpu-container" style={{ backgroundColor: this.props.websocketconnected && this.props.backendisgpu? "#DDFFA4" : "#A2A2A2" }}>GPU</div></Col>
                    </Row>
                </Container>
            </div>
        )
    }
}