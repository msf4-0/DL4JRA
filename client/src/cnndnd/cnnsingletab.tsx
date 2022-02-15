// IMPORT FROM MODULES
import React, {Component, MouseEvent} from 'react'
import {ReactFlowProvider, FlowElement, Node, Edge } from 'react-flow-renderer'
import { IFrame, IMessage } from '@stomp/stompjs';
import { EventEmitter } from 'events'
import { waitFor } from 'wait-for-event'

// CUSTOM IMPORT
import Toolbar from './cnntoolbar'
import ConfigurationPanel from './cnnconfigpanel'
import {FlipImage, RotateImage, ResizeImage, 
    DatasetAutoSplitStartNode, TrainingDatasetStartNode, ValidationDatasetStartNode, LoadDataset, GenerateDatasetIterator,
    CNNStartNode, CNNConfiguration, ConvolutionLayer, SubsamplingLayer, DenseLayer, OutputLayer, 
    SetInputType, ConstructCNN, TrainNN, ValidateNN, ExportNN, LocalResponseNormalizationLayer,
    LoadDatasetCSV,TrainingDatasetStartNodeCSV, ValidationDatasetStartNodeCSV, GenerateDatasetIteratorCSV, RNNStartNode, RNNConfiguration, RnnOutputLayer,
    AddInput, SetOutput, Convolution1DLayer, LSTM, ConstructNetworkRNN,
    segmentationStartnode, importPretrainedModel, configureFineTune, configureTranferLearning, 
    addCnnLossLayer, build_TransferLearning, segmentationDataStartNode, setIterator_segmentation,
    generateIterator, train_segmentation, validation_segmentation, setOutput_segmentation} from './cnnlayers'
import CNNNodeService from "./cnnnodedata"
import "./cnn.css"

// GLOBAL COMPONENT
import SuccessModal from '../globalcomponents/glbsuccessmodal'
import ErrorModal from "../globalcomponents/glberrormodal"
import ProgressModal from '../globalcomponents/glbprogressmodal'
import DragNDrop from '../globalcomponents/glbdnd'
import {Dictionary} from '../globalcomponents/interfaces'
import WebsocketService from "../globalcomponents/glbwebsocket"

/**
 * (Single-tab) CNN Props
 */
interface CNNProps {

}

/**
 * (Single-tab) CNN States
 * websocketconnected: If client is connected to server's websocket
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
 * backendiscpu: If server (DL4J) is using CPU as backend (FALSE if not connected to server's websocket)
 * backendisgpu: If server (DL4J) is using GPU as backend (FALSE if not connected to server's websocket)
*/
interface CNNStates {
    websocketconnected: boolean;
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
    backendiscpu: boolean;
    backendisgpu: boolean;
}

export default class CNNSingletab extends Component <CNNProps, CNNStates>{
    /**
     * CNN class properties
     * dndref: Drag and drop component's ref
     * confmodalref: Configuration panel (modal)'s ref
     * successmodalref: Success modal's ref
     * errormodalref: Error modal's ref
     * nodeTypes: Types of node
     * nodeinmodification: Id of node which data is in modification
     * seqcancontinue: If sequence can continue running
     * cnnwebsocket: WebsocketService class
     * eventemitter: EventEmitter class (used to emit specific event name)
     * numberofelementstosave: Total number of elements to save (autosave)
     * elemensaved: Number of elements that have been save (autosave)
    */
    dndref : React.RefObject<any>;
    confmodalref: React.RefObject<any>;
    successmodalref: React.RefObject<any>;
    errormodalref: React.RefObject<any>;
    nodeTypes: {[key : string] : any};
    nodeinmodification: string;
    seqcancontinue: Boolean;
    cnnwebsocket: WebsocketService;
    eventemitter: EventEmitter;
    numberofelementstosave: number;
    elementsaved: number;

    constructor(props: CNNProps) {
        super(props);
        this.state = {
            websocketconnected: false,
            progressmodalactive: false,
            progressbarcolor: 'success',
            progressbaranimated: true,
            progressmodalcanclose: false,
            progresscanabort: true,
            progressmodalheader: "",
            progressmodalsubheader: "",
            progressmodalmessages: [],
            currentprogress: 0,
            maxprogress: 1,
            backendiscpu: false,
            backendisgpu: false,
        }
        this.dndref = React.createRef();
        this.confmodalref = React.createRef();
        this.successmodalref = React.createRef();
        this.errormodalref = React.createRef();
        this.nodeTypes = 
        {
            FlipImage, RotateImage, ResizeImage,
            DatasetAutoSplitStartNode, TrainingDatasetStartNode, ValidationDatasetStartNode, LoadDataset, GenerateDatasetIterator,
            CNNStartNode, CNNConfiguration, ConvolutionLayer, SubsamplingLayer, DenseLayer, OutputLayer, SetInputType, 
            ConstructCNN, TrainNN, ValidateNN, ExportNN, LocalResponseNormalizationLayer,
            LoadDatasetCSV,TrainingDatasetStartNodeCSV, ValidationDatasetStartNodeCSV, GenerateDatasetIteratorCSV,
            RNNStartNode, RNNConfiguration, RnnOutputLayer, AddInput, SetOutput, Convolution1DLayer, LSTM, ConstructNetworkRNN,
            segmentationStartnode, importPretrainedModel, configureFineTune, configureTranferLearning, 
            addCnnLossLayer, build_TransferLearning, segmentationDataStartNode, setIterator_segmentation,
            generateIterator, train_segmentation, validation_segmentation, setOutput_segmentation
        }
        this.nodeinmodification = "";
        this.seqcancontinue = true;
        this.numberofelementstosave = 0;
        this.elementsaved = 0;
        this.eventemitter = new EventEmitter();
        // Initialize websocket and set callbacks (onConnect/onDisconnect/onClose/onError)
        this.cnnwebsocket = new WebsocketService("ws://localhost:8081/stomp");
        this.cnnwebsocket.setonConnectCallback(this.websocketonConnect);
        this.cnnwebsocket.setonDisconnectCallback(this.websocketonDisconnect);
        this.cnnwebsocket.setonWebSocketCloseCallback(this.websocketonClose);
        this.cnnwebsocket.setonWebsocketErrorCallback(this.websocketonError);
        // Connect to server's websocket
        this.cnnwebsocket.connect();
    }

    /**
     * [COMPONENT LIFECYCLE]
     * Called before CNN component is unmounted
     * 1. Wait for "SavingCompleted" evemt is emitted (Wait until entire flow is autosaved)
     * 2. Remove all websocket callbacks
     * 3. Unsubscribe from all topics
     * 4. Disconnect from server
     */
    componentWillUnmount = async () : Promise<any> => {
        await waitFor("SavingCompleted", this.eventemitter);
        this.cnnwebsocket.removeonConnectCallback();
        this.cnnwebsocket.removeonDisconnectCallback();
        this.cnnwebsocket.removeonWebSocketCloseCallback();
        this.cnnwebsocket.removeonWebsocketErrorCallback();
        if (this.cnnwebsocket.isConnected()) {
            this.cnnwebsocket.unsubscribe("/response/backend");
            this.cnnwebsocket.unsubscribe("/response/cnn/currentprocessdone");
            this.cnnwebsocket.unsubscribe("/response/cnn/message");
            this.cnnwebsocket.unsubscribe("/response/cnn/progressupdate");
            this.cnnwebsocket.unsubscribe("/response/cnn/error");
            this.cnnwebsocket.unsubscribe("/response/cnnflowloading/node");
            this.cnnwebsocket.unsubscribe("/response/cnnflowloading/edge");
            this.cnnwebsocket.unsubscribe("/response/cnnflowsaving/elementsaved");
            this.cnnwebsocket.unsubscribe("/response/cnnflowsaving/complete");
        }
        this.cnnwebsocket.disconnect();
    }

    /**
     * [UPDATE COMPONENT'S STATE] - websocketconnected
     * @param websocketconnected 
    */
    setwebsocketconnected = (websocketconnected: boolean) : void => {
        this.setState({ websocketconnected });
    }

    /**
     * [WEBSOCKET CALLBACK]
     * Called when client is disconnected from websocket
     * 1. If websocketconnected (state) is TRUE, set to FALSE
     * @param frame 
     */
    websocketonDisconnect = (frame: IFrame) : void => {
        if (this.state.websocketconnected)
            this.setwebsocketconnected(false);
    }

    /**
     * [WEBSOCKET CALLBACK]
     * Called when websocket is closed
     * 1. If websocketconnected (state) is TRUE, set to FALSE
     * @param event 
     */
    websocketonClose = (event: CloseEvent) : void => {
        if (this.state.websocketconnected)
            this.setwebsocketconnected(false);
    }

    /**
     * [WEBSOCKET CALLBACK]
     * Called when websocket encounters error
     * 1. If websocketconnected (state) is TRUE, set to FALSE
     * @param event 
     */
    websocketonError = (event: Event) : void => {
        if (this.state.websocketconnected)
            this.setwebsocketconnected(false);
    }

    /**
     * [WEBSOCKET CALLBACK]
     * Called when client is connected to server
     * 1. Set websocketconnected (state) to TRUE
     * 2. Subscribe to topics
     * 3. Get the backend used by server (DL4J)
     * 4. Restore previously saved nodes & edges
     * @param frame
    */
    websocketonConnect = (frame: IFrame) : void => {
        this.setwebsocketconnected(true);
        this.cnnwebsocket.subscribe("/response/backend", this.getbackendCallback);
        this.cnnwebsocket.subscribe("/response/cnn/currentprocessdone", this.processdoneCallback);
        this.cnnwebsocket.subscribe("/response/cnn/message", this.processmessageCallback);
        this.cnnwebsocket.subscribe("/response/cnn/progressupdate", this.processupdateCallback);
        this.cnnwebsocket.subscribe("/response/cnn/error", this.processerrorCallback);
        this.cnnwebsocket.subscribe("/response/cnnflowloading/node", this.processnodeloadingCallback);
        this.cnnwebsocket.subscribe("/response/cnnflowloading/edge", this.processedgeloadingCallback);
        this.cnnwebsocket.subscribe("/response/cnnflowsaving/elementsaved", this.elementsavedCallback);
        this.cnnwebsocket.subscribe("/response/cnnflowsaving/complete", this.flowsavingcomplete);
        
        this.cnnwebsocket.sendmessage("/server/getbackend", "");
        this.cnnwebsocket.sendmessage("/server/cnnflowsaving/reset", "");
        this.cnnwebsocket.sendmessage("/server/cnnflowloading", "");
    }

    /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK]
     * Called when a node is finished processed by server
     * Example: For "LOAD DATASET" node, function will be called after dataset has been loaded successfully
     * 1. Get the message from server's response
     * 2. Append message to progress modal
     * 3. Emit "processcompleted" event -> Proceed to next node
     * @param response 
    */    
   processdoneCallback = (response: IMessage) : void => {
        if (! this.seqcancontinue) return;
        let data = JSON.parse(response.body);
        let message = data.message;
        this.appendprogressmessage(message);
        setImmediate(() => this.eventemitter.emit("processcompleted"));
    }

    /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK]
     * Called when client receives a pure string message from server
     * 1. Get message from server's response
     * 2. Append message to progress modal
     * @param response 
    */
    processmessageCallback = (response: IMessage) : void => {
        if (! this.seqcancontinue) return;
        let data = JSON.parse(response.body);
        this.appendprogressmessage(data.message);
    }

    /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK]
     * Called when server updates the progress of current node
     * Example: For training CNN node, currentprogress = 5, maxprogress = 10 is equal to current epoch = 5, max epoch = 10
     * 1. Retrieve current and max progress value from server's response
     * 2. Update current max progress value in progress modal
     * @param response 
    */
    processupdateCallback = (response: IMessage) : void => {
        if (! this.seqcancontinue) return;
        let data = JSON.parse(response.body);
        let currentprogress = Number(data.currentprogress);
        let maxprogress = Number(data.maxprogress);
        this.setcurrentprogress(currentprogress);
        this.setmaxprogress(maxprogress);
    }
    
    /**
     * [WEBSOCKET SUBCRIPTION CALLBACK]
     * Called when there is an exception in server's side
     * 1. Set "seqcancontinue" to false
     * 2. Hide progress modal
     * 3. Get the exception message (reason) from server's response
     * 4. Set the background of error node to red
     * 5. Fire "processcompleted" event (** Next node will not continue if "seqcancontinue" is FALSE)
     * @param response 
     */
    processerrorCallback = (response: IMessage) : void => {
        this.seqcancontinue = false;
        this.setprogressmodalactive(false);
        let data = JSON.parse(response.body);
        this.errormodalref.current.openmodal("OOPS", data.message);
        if (data.nodeId !== null && data.nodeId !== "")
            this.dndref.current.setErrorNode(data.nodeId);
        setImmediate(() => this.eventemitter.emit("processcompleted"));
    }

    /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK]
     * Restore previously saved node
     * @param response 
    */
    processnodeloadingCallback = (response: IMessage) : void => {
        let node = JSON.parse(response.body);
        this.dndref.current.addLoadedElement(node);
    }

    /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK]
     * Restore previously saved edge
     * @param response 
    */
    processedgeloadingCallback = (response: IMessage) : void => {
        let edge = JSON.parse(response.body);
        this.dndref.current.addLoadedElement(edge);
    }

    /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK]
     * Called when a node/edge is successfully saved
     * 1. Increment "elementsaved" 
     * 2. Save flow
    */
    elementsavedCallback = () : void => {
        this.elementsaved++;
        this.saveflow();
    }

    /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK]
     * Called when nodes & edges have been saved to JSON file (in server side)
     * 1. Emit "SavingCompleted" -> component unmount
    */
    flowsavingcomplete = () : void => {
        setImmediate(() => this.eventemitter.emit("SavingCompleted"));
    }

    /**
     * [WEBSOCKET SUBSCRIPTION CALLBACK]
     * Get the backend used by server (DL4J) in server's response
     * 1. If GPU is used, set backendisgpu to TRUE and backendiscpu to FALSE
     * 2. If CPU is used, set backendiscpu to TRUE and backendisgpu to FALSE
     * @param response 
    */
    getbackendCallback = (response: IMessage) : void => {
        let backendused = response.body;
        if (backendused === "gpu")
            this.setState({ backendiscpu: false, backendisgpu: true });
        else
            this.setState({ backendiscpu: true, backendisgpu: false });
    }

    /**
     * Called when user double-click on a node
     * 1. Set nodeinmodification to the Id of node that was double-clicked
     * 2. Open configuration modal 
     * @param event 
     * @param node - The node that was double-clicked
    */
    onNodeDoubleClick = (event : MouseEvent, node: Node<any>) : void => {
        event.preventDefault();
        this.nodeinmodification = node.id;
        this.confmodalref.current.showmodal(node.data);
    }

    /**
     * Update the data of node (Id = nodeinmodification)
     * @param data - Latest value of data 
    */
    onNodeUpdateCallback = (data: Dictionary) : void => {
        this.dndref.current.updatenodedata(this.nodeinmodification, data);
    }

    /**
     * Called when user clicked on CONSTRUCT button 
     * Function: Run entire sequence of active tab
     * 1. Reset "seqcancontinue" to true
     * 2. Reset error node (remove red background if any exists)
     * 3. Reset, open and set header of progress modal
     * 4. Signal server a new sequence has begun
     * 5. Sequence : Training dataset -> Validation dataset -> Network
     * 6. After finish running sequence, set "progressmodalcanclose" to true
     * 7. Set "progressmodalcanabort" to false
    */
    Construct = async () : Promise<any> => {
        this.seqcancontinue = true;
        this.dndref.current.resetErrorNode();
        this.resetprogressmodal();
        this.setprogressmodalactive(true);
        this.setprogressmodalheader("RUNNING CNN SEQUENCE");
        await this.sendmessage("/server/cnn/startnewsequence", "");
        await this.ConstructDatasetAutoSplitFlowSequence();
        await this.ConstructTrainingDatasetFlowSequence();
        await this.ConstructValidationDatasetFlowSequence();
        await this.ConstructCNNFlowSequence();
        await this.ConstructRNNFlowSequence();
        //Segmentation
        await this.ConstructSegmentationFlowSequence();
        await this.ConstructDatasetnEvaluationSegmentationFlowSequence();
        this.setprogressmodalcanclose(true);
        this.setprogresscanabort(false);
    }

    /**
     * Send node data to server for process
     * @param destination - Destination to send the message
     * @param nodeid - Id of node which in processing
     * @param nodedata - Data of node
    */
    processnode = (destination: string, nodeid: string, nodedata: Dictionary) : Promise<any> => {
        nodedata["nodeid"] = nodeid;
        return this.sendmessage(destination, JSON.stringify(nodedata));
    }

    /**
     * Send node data to server for process
     * @param destination - Destination to send the message
     * @param nodeid - Id of node which in processing
     * @param nodedata - Data of node
     * @param ordering - Ordering of layer 
    */
    processnodewithordering = (destination: string, nodeid: string, nodedata: Dictionary, ordering: number) : Promise<any> => {
        nodedata["nodeid"] = nodeid;
        nodedata["ordering"] = ordering;
        return this.sendmessage(destination, JSON.stringify(nodedata));
    }

    /**
     * 1. Send message (node data) to server
     * 2. Wait (asynchronously) until "processcompleted" event is emitted
     * @param destination - Destination to send the message
     * @param message - Message to send
    */
    sendmessage = (destination: string, message: string) : Promise<any> => {
        this.cnnwebsocket.sendmessage(destination, message);
        return waitFor("processcompleted", this.eventemitter);
    }

    /**
    * Get and run the sequence of Auto Split Dataset
    * 1. Find first node of type "DATASET AUTO-SPLIT (D-AS)"
    * 2. Get the whole sequence if "DS-AP" node exists
    * 3. Loop through the sequence and process each node
    * 4. Wait for current node to complete before proceed to next node 
    */
    ConstructDatasetAutoSplitFlowSequence = async () : Promise<void> => {
    let startnodeId : string | null = this.dndref.current.searchFirstOccuranceOfNodeType("DatasetAutoSplitStartNode");
    if (startnodeId === null) return;
    let autosplitsequences : FlowElement[] = this.dndref.current.getEntireSequence(startnodeId);
    for(let index = 0; index < autosplitsequences.length; index++) {
        if (! this.seqcancontinue) return;
        let element = autosplitsequences[index];
        if (element.type === "LoadDataset") {
            await this.processnode("/server/cnn/loaddatasetautosplit", element.id, element.data);
        } else if (element.type === "FlipImage") {
            await this.processnode("/server/cnn/fliptrainingdataset", element.id, element.data);
        } else if (element.type === "RotateImage") {
            await this.processnode("/server/cnn/rotatetrainingdataset", element.id, element.data);
        } else if (element.type === "ResizeImage") {
            await this.processnode("/server/cnn/resizetrainingdataset", element.id, element.data);
        } else if (element.type === "GenerateDatasetIterator") {
            await this.processnode("/server/cnn/generatedatasetautosplititerator", element.id, element.data);
        }
    }
    }

    /**
     * Get and run the sequence of Training Dataset
     * 1. Find first node of type "TRAINING DATASET STARTNODE (TDS)"
     * 2. Get the whole sequence if "TDS" node exists
     * 3. Loop through the sequence and process each node
     * 4. Wait for current node to complete before proceed to next node 
    */
    ConstructTrainingDatasetFlowSequence = async () : Promise<void> => {
        let startnodeId : string | null = this.dndref.current.searchFirstOccuranceOfNodeType("TrainingDatasetStartNode");
        if (startnodeId === null) return;
        let trainingsequences : FlowElement[] = this.dndref.current.getEntireSequence(startnodeId);
        for(let index = 0; index < trainingsequences.length; index++) {
            if (! this.seqcancontinue) return;
            let element = trainingsequences[index];
            if (element.type === "LoadDataset") {
                await this.processnode("/server/cnn/loadtrainingdataset", element.id, element.data);
            } else if (element.type === "FlipImage") {
                await this.processnode("/server/cnn/fliptrainingdataset", element.id, element.data);
            } else if (element.type === "RotateImage") {
                await this.processnode("/server/cnn/rotatetrainingdataset", element.id, element.data);
            } else if (element.type === "ResizeImage") {
                await this.processnode("/server/cnn/resizetrainingdataset", element.id, element.data);
            } else if (element.type === "GenerateDatasetIterator") {
                await this.processnode("/server/cnn/generatetrainingdatasetiterator", element.id, element.data);
            }
        }
    }

    /**
     * Get and run the sequence of Training Dataset
     * 1. Find first node of type "VALIDATION DATASET STARTNODE (VDS)"
     * 2. Get the whole sequence if "VDS" node exists
     * 3. Loop through the sequence and process each node
     * 4. Wait for current node to complete before proceed to next node 
    */
    ConstructValidationDatasetFlowSequence = async () : Promise<any> => {
        let startnodeId : string | null = this.dndref.current.searchFirstOccuranceOfNodeType("ValidationDatasetStartNode");
        if (startnodeId === null) return;
        let validationsequences : FlowElement[] = this.dndref.current.getEntireSequence(startnodeId);
        for(let index = 0; index < validationsequences.length; index++) {
            if (! this.seqcancontinue) return;
            let element = validationsequences[index];
            if (element.type === "LoadDataset") {
                await this.processnode("/server/cnn/loadvalidationdataset", element.id, element.data);
            } else if (element.type === "FlipImage") {
                await this.processnode("/server/cnn/flipvalidationdataset", element.id, element.data);
            } else if (element.type === "RotateImage") {
                await this.processnode("/server/cnn/rotatevalidationdataset", element.id, element.data);
            } else if (element.type === "ResizeImage") {
                await this.processnode("/server/cnn/resizevalidationdataset", element.id, element.data);
            } else if (element.type === "GenerateDatasetIterator") {
                await this.processnode("/server/cnn/generatevalidationdatasetiterator", element.id, element.data);
            }
        }
    }
/**
     * Get and run the sequence of Training Dataset
     * 1. Find first node of type "TRAINING STARTNODE CSV (TDS)"
     * 2. Get the whole sequence if "TRAINING STARTNODE CSV " node exists
     * 3. Loop through the sequence and process each node
     * 4. Wait for current node to complete before proceed to next node 
    */
 ConstructTrainingDatasetFlowSequence_CSV = async () : Promise<any> => {
    let startnodeId : string | null = this.dndref.current.searchFirstOccuranceOfNodeType("TrainingDatasetStartNodeCSV");
    if (startnodeId === null) return;
    let trainingsequences : FlowElement[] = this.dndref.current.getEntireSequence(startnodeId);
    for(let index = 0; index < trainingsequences.length; index++) {
        if (! this.seqcancontinue) return;
        let element = trainingsequences[index];
        if (element.type === "LoadDatasetCSV") {
            await this.processnode("/server/cnn/loadtrainingdataset_csv", element.id, element.data);
        }else if (element.type === "GenerateDatasetIterator") {
            await this.processnode("/server/cnn/generatetrainingdatasetiterator_csv", element.id, element.data);
        }
    }
}

    /**
 * Get and run the sequence of Training Dataset
 * 1. Find first node of type "VALIDATION STARTNODE CSV(VDS)"
 * 2. Get the whole sequence if "VALIDATION STARTNODE CSV " node exists
 * 3. Loop through the sequence and process each node
 * 4. Wait for current node to complete before proceed to next node 
*/
     ConstructValidationDatasetFlowSequence_CSV = async () : Promise<any> => {
        let startnodeId : string | null = this.dndref.current.searchFirstOccuranceOfNodeType("ValidationDatasetStartNodeCSV");
        if (startnodeId === null) return;
        let validationsequences : FlowElement[] = this.dndref.current.getEntireSequence(startnodeId);
        for(let index = 0; index < validationsequences.length; index++) {
            if (! this.seqcancontinue) return;
            let element = validationsequences[index];
            if (element.type === "LoadDatasetCSV") {
                await this.processnode("/server/cnn/loadvalidationdataset_csv", element.id, element.data);
            }else if (element.type === "GenerateDatasetIterator") {
                await this.processnode("/server/cnn/generatevalidationdatasetiterator_csv", element.id, element.data);
            }
        }
    }
    /**
     * Get and run the sequence of Training Dataset
     * 1. Find first node of type "CNN STARTNODE"
     * 2. Get the whole sequence if "CNN STARTNODE" exists
     * 3. Loop through the sequence and process each node
     * 4. Wait for current node to complete before proceed to next node 
    */
    ConstructCNNFlowSequence = async () : Promise<any> => {
        let cnnstartnodeId : string | null = this.dndref.current.searchFirstOccuranceOfNodeType("CNNStartNode");
        let cnnsequences : FlowElement[];
        if (cnnstartnodeId !== null) {
            cnnsequences = this.dndref.current.getEntireSequence(cnnstartnodeId);
            let ordering = 0;
            for (let index = 0; index < cnnsequences.length; index ++) {
                if (! this.seqcancontinue) return;
                let element = cnnsequences[index];
                if (element.type === "CNNConfiguration") {
                    await this.processnode("/server/cnn/initializeconfiguration", element.id, element.data);
                } else if (element.type === "ConvolutionLayer") {
                    await this.processnodewithordering("/server/cnn/appendconvolutionlayer", element.id, element.data, ordering);
                    ordering ++;
                } else if (element.type === "SubsamplingLayer") {
                    await this.processnodewithordering("/server/cnn/appendsubsamplinglayer", element.id, element.data, ordering);
                    ordering++;
                } else if (element.type === "DenseLayer") {
                    await this.processnodewithordering("/server/cnn/appenddenselayer", element.id, element.data, ordering);
                    ordering++;
                } else if (element.type === "OutputLayer") {
                    await this.processnodewithordering("/server/cnn/appendoutputlayer", element.id, element.data, ordering);
                    ordering++;
                }else if (element.type === "LocalResponseNormalizationLayer") {
                    await this.processnodewithordering("/server/cnn/appendlocalresponsenormalizationlayer", element.id, element.data, ordering);
                    ordering++;
                } else if (element.type === "SetInputType") {
                    await this.processnode("/server/cnn/setinputtype", element.id, element.data);
                } else if (element.type === "ConstructCNN") {
                    await this.processnode("/server/cnn/constructnetwork", element.id, element.data);
                } else if (element.type === "TrainNN") {
                    await this.processnode("/server/cnn/trainnetwork", element.id, element.data);
                } else if (element.type === "ValidateNN") {
                    await this.processnode("/server/cnn/validatenetwork", element.id, element.data);
                } else if (element.type === "ExportNN") {
                    await this.processnode("/server/cnn/exportnetwork", element.id, element.data);
                }
            }
        }
    }


    /**
     * Get and run the sequence of Training Dataset
     * 1. Find first node of type "CNN STARTNODE"
     * 2. Get the whole sequence if "CNN STARTNODE" exists
     * 3. Loop through the sequence and process each node
     * 4. Wait for current node to complete before proceed to next node 
    */
     ConstructRNNFlowSequence = async () : Promise<any> => {
        let cnnstartnodeId : string | null = this.dndref.current.searchFirstOccuranceOfNodeType("RNNStartNode");
        let cnnsequences : FlowElement[];
        if (cnnstartnodeId !== null) {
            cnnsequences = this.dndref.current.getEntireSequence(cnnstartnodeId);
            for (let index = 0; index < cnnsequences.length; index ++) {
                if (! this.seqcancontinue) return;
                let element = cnnsequences[index];
                if (element.type === "RNNConfiguration") {
                    await this.processnode("/server/cnn/initializeconfiguration_rnn", element.id, element.data);
                } else if (element.type === "AddInput") {
                    await this.processnode("/server/cnn/addinput", element.id, element.data);
                } else if (element.type === "SetOutput") {
                    await this.processnode("/server/cnn/setoutput", element.id, element.data);
                } else if (element.type === "Convolution1DLayer") {
                    await this.processnode("/server/cnn/appendconvolution1dlayer", element.id, element.data);
                } else if (element.type === "LSTM") {
                    await this.processnode("/server/cnn/appendlstm", element.id, element.data);
                } else if (element.type === "RnnOutputLayer") {
                    await this.processnode("/server/cnn/appendrnnoutputlayer", element.id, element.data);
                } else if (element.type === "ConstructNetworkRNN") {
                    await this.processnode("/server/cnn/constructnetwork_rnn", element.id, element.data);
                }else if (element.type === "TrainNN") {
                    await this.processnode("/server/cnn/trainnetwork", element.id, element.data);
                } else if (element.type === "ValidateNN") {
                    await this.processnode("/server/cnn/validatenetwork", element.id, element.data);
                } else if (element.type === "ExportNN") {
                    await this.processnode("/server/cnn/exportnetwork", element.id, element.data);
                }else if (element.type === "EvaluateModelRNN" ){
                    await this.processnode("/server/cnn/evaluatemodelrnn", element.id, element.data);
                }
            }
        }
    }
    
    ConstructSegmentationFlowSequence = async () : Promise<any> => {
        let cnnstartnodeId : string | null = this.dndref.current.searchFirstOccuranceOfNodeType("segmentationStartnode");
        let cnnsequences : FlowElement[];
        if (cnnstartnodeId !== null) {
            cnnsequences = this.dndref.current.getEntireSequence(cnnstartnodeId);
            for (let index = 0; index < cnnsequences.length; index ++) {
                if (! this.seqcancontinue) return;
                let element = cnnsequences[index];
                if (element.type === "importPretrainedModel") {
                    await this.processnode("/server/cnn/importpretrainedmodel", element.id, element.data);
                } else if (element.type === "configureFineTune") {
                    await this.processnode("/server/cnn/configurefinetune", element.id, element.data);
                } else if (element.type === "configureTranferLearning") {
                    await this.processnode("/server/cnn/configuretransferlearning", element.id, element.data);
                } else if (element.type === "addCnnLossLayer") {
                    await this.processnode("/server/cnn/appendcnnlosslayer", element.id, element.data);
                } else if (element.type === "setOutput_segmentation") {
                    await this.processnode("/server/cnn/setoutput_segmentation", element.id, element.data);
                } else if (element.type === "build_TransferLearning") {
                    await this.processnode("/server/cnn/buildtransferlearning", element.id, element.data);
                }
            }
        }
    }

    /**
     * After importing and construting pretrained model, load the dataset and run.
    */
         ConstructDatasetnEvaluationSegmentationFlowSequence = async () : Promise<void> => {
            let startnodeId : string | null = this.dndref.current.searchFirstOccuranceOfNodeType("segmentationDataStartNode");
            if (startnodeId === null) return;
            let sequence : FlowElement[] = this.dndref.current.getEntireSequence(startnodeId);
            for(let index = 0; index < sequence.length; index++) {
                if (! this.seqcancontinue) return;
                let element = sequence[index];
                if (element.type === "setIterator_segmentation") {
                    await this.processnode("/server/cnn/setupiterator", element.id, element.data);
                } else if (element.type === "generateIterator") {
                    await this.processnode("/server/cnn/generateiteratorsegmentation", element.id, element.data);
                } else if (element.type === "train_segmentation") {
                    await this.processnode("/server/cnn/trainsegmentation", element.id, element.data);
                } else if (element.type === "validation_segmentation") {
                    await this.processnode("/server/cnn/validatesegmentation", element.id, element.data);
                } 
            }
        }


    /**
     * Test functionality of flow (without constructing node)
     * 1. Start new sequence
     * 2. Load training dataset -> Generate training dataset
     * 3. Load validation dataset -> Generate validation dataset
     * 4. Initialize CNN
     * 5. Append layers (convolution -> subsampling -> dense -> output)
     * 6. Set input type
     * 7. Construct network
     * 8. Train network for 50 epochs
    */
    HardcodedCNNConfiguration = async () : Promise<any> => {
        this.seqcancontinue = true;
        this.resetprogressmodal();
        this.setprogressmodalactive(true);
        this.setprogressmodalheader("RUNNING CNN SEQUENCE (TEST)");
        await this.sendmessage("/server/cnn/startnewsequence", "");
        if (this.seqcancontinue)
            await this.processnode("/server/cnn/loadtrainingdataset", "001", {path : "D:/CNNData/Train", imagewidth: 50, imageheight: 50, channels: 1, batchsize: 1, numLabels: 2});
        if (this.seqcancontinue)
            await this.processnode("/server/cnn/generatetrainingdatasetiterator", "002", {name: "TDSI"});
        
        if (this.seqcancontinue)
            await this.processnode("/server/cnn/initializeconfiguration", "003", { seed: 1234, learningrate: 0.005, optimizationalgorithm: "STOCHASTIC_GRADIENT_DESCENT"});
        if (this.seqcancontinue)
            await this.processnodewithordering("/server/cnn/appendconvolutionlayer", "004", {kernalx: 2, kernaly: 2, stridex: 1, stridey: 1, paddingx: 0, paddingy: 0, nIn: 1, nOut: 10, activationfunction: "RELU"}, 0);
        if (this.seqcancontinue)
            await this.processnodewithordering("/server/cnn/appendsubsamplinglayer", "005", {kernalx: 2, kernaly: 2, stridex: 1, stridey: 1, paddingx: 0, paddingy: 0, poolingtype: "MAX"}, 1);
        if (this.seqcancontinue)
            await this.processnodewithordering("/server/cnn/appenddenselayer", "006", {nOut: 50, activationfunction: "RELU"}, 2);
        if (this.seqcancontinue)
            await this.processnodewithordering("/server/cnn/appendoutputlayer", "007", {nOut: 2, activationfunction: "SOFTMAX", lossfunction: "NEGATIVELOGLIKELIHOOD"}, 3);
        if (this.seqcancontinue)
            await this.processnodewithordering("/server/cnnappendlocalresponsenormalizationlayer", "008", {}, 5)
        if (this.seqcancontinue)
            await this.processnode("/server/cnn/setinputtype", "009", {imagewidth: 50, imageheight: 50, channels: 1});
        if (this.seqcancontinue)
            await this.processnode("/server/cnn/constructnetwork", "010", {});
        if (this.seqcancontinue)
            await this.processnode("/server/cnn/trainnetwork", "011", {epochs: 50, scoreListener: 1});
        this.setprogressmodalcanclose(true);
        this.setprogresscanabort(false);
        this.setprogressbaranimated(false);
    }

    /**
     * Reset progress modal
     * 1. Reset progress bar (color: success, animated: true)
     * 2. Reset progressmodalcanclose to false & canabort to true
     * 3. Header and subheader to empty string
     * 4. Clear all messages
     * 5. Current progress to 0 and max progress to 1
    */
    resetprogressmodal = () : void => {
        this.setprogressbarcolor("success");
        this.setprogressbaranimated(true);
        this.setprogressmodalcanclose(false);
        this.setprogresscanabort(true);
        this.setprogressmodalheader("");
        this.setprogressmodalsubheader("");
        this.clearprogressmessages();
        this.setcurrentprogress(0);
        this.setmaxprogress(1);
    }

    /**
     * [UPDATE COMPONENT'S STATE] - progressmodalactive
     * @param progressmodalactive 
    */
    setprogressmodalactive = (progressmodalactive: boolean) : void => {
        this.setState({ progressmodalactive });
    } 

    /**
     * [UPDATE COMPONENT'S STATE] - progressbarcolor
     * @param progressbarcolor 
    */
    setprogressbarcolor = (progressbarcolor: "success" | "danger") : void => {
        this.setState({ progressbarcolor });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - progressbaranimated
     * @param progressbaranimated 
    */
    setprogressbaranimated = (progressbaranimated: boolean) : void => {
        this.setState({ progressbaranimated });
    }

    /** 
     * [UPDATE COMPONENT'S STATE] - progressmodalcanclose
     * @param progressmodalcanclose 
    */
    setprogressmodalcanclose = (progressmodalcanclose: boolean) : void => {
        this.setState({ progressmodalcanclose });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - progresscanabort
     * @param progresscanabort 
    */
    setprogresscanabort = (progresscanabort: boolean) : void => {
        this.setState({ progresscanabort });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - progressmodalheader
     * @param progressmodalheader 
    */
    setprogressmodalheader = (progressmodalheader: string) : void => {
        this.setState({ progressmodalheader });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - progressmodalsubheader
     * @param progressmodalsubheader 
    */
    setprogressmodalsubheader = (progressmodalsubheader: string) : void => {
        this.setState({ progressmodalsubheader });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - progressmodalmessages
     * Append message to progressmodalmessages
     * @param message 
    */
    appendprogressmessage = (message: string) : void => {
        this.setState({ progressmodalmessages: [ ...this.state.progressmodalmessages, message ] });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - progressmodalmessages
     * Removes all messages in progressmodalmessages
    */
    clearprogressmessages = () : void => {
        this.setState({ progressmodalmessages: [] });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - currentprogress
     * @param currentprogress 
    */
    setcurrentprogress = (currentprogress: number) : void => {
        this.setState({ currentprogress });
    }

    /**
     * [UPDATE COMPONENT'S STATE] - maxprogress
     * @param maxprogress 
    */
    setmaxprogress = (maxprogress: number) : void => {
        this.setState({maxprogress });
    }

    /**
     * Abort progress (while sequence is running)
     * 1. Send message to server (destination: /server/cnn/abort)
     * 2. Set progressbarcolor to "danger"
     * 3. Set progressbarsubheader
     * 4. Set seqcancontinue to false (next node will NOT be run)
     * 5. Emit "processcompleted" event 
    */
    abortprogress = () : void => {
        this.cnnwebsocket.sendmessage("/server/cnn/abort", "");
        this.setprogressbarcolor("danger");
        this.setprogressmodalsubheader("CNN SEQUENCE ABORTED!");
        this.seqcancontinue = false;
        setImmediate(() => this.eventemitter.emit("processcompleted"));
    }

    /**
     * First method to call before any node/edge has been saved
     * 1. Set the TOTAL number of elements to save
     * 2. Set value of "elementsaved" to 0
     * @param count - The TOTAL number of elements to save
     */
    setnumberofelementstosend = (count: number) : void => {
        this.elementsaved = 0;
        this.numberofelementstosave = count;
        this.saveflow();
    }

    /**
     * [AUTO SAVE] Send node data to server for saving purpose
     * @param node - Node data (name, position, data etc.)
    */
    sendnodedataforsaving = (node: Node) : void => {
        if (this.cnnwebsocket.isConnected())
            this.cnnwebsocket.sendmessage("/server/cnnflowsaving/node", JSON.stringify(node));
    }

    /**
     * [AUTO SAVE] Send edge data to server for saving purpose
     * @param edge - Edge data (source, target, style etc.)
    */
    sendedgedataforsaving = (edge: Edge) => {
        if (this.cnnwebsocket.isConnected())
            this.cnnwebsocket.sendmessage("/server/cnnflowsaving/edge", JSON.stringify(edge));
    }

    /**
     * [AUTO SAVE] Send signal to server after all elements have been saved (elementssaved = numberofelementstosave)
     * => Server will write all nodes & edges data to JSON file
    */
    saveflow = () => {
        if (this.elementsaved === this.numberofelementstosave)
            this.cnnwebsocket.sendmessage("/server/cnnflowsaving/saveflow", "");
    }

    render = () => {
        return (
            <div>
                <SuccessModal ref={this.successmodalref} />
                <ErrorModal ref={this.errormodalref} />
                <ProgressModal
                    progressmodalactive={this.state.progressmodalactive}
                    progressbarcolor={this.state.progressbarcolor}
                    progressbaranimated={this.state.progressbaranimated}
                    progressmodalcanclose={this.state.progressmodalcanclose}
                    progresscanabort={this.state.progresscanabort}
                    progressmodalheader={this.state.progressmodalheader}
                    progressmodalsubheader={this.state.progressmodalsubheader}
                    progressmodalmessages={this.state.progressmodalmessages}
                    currentprogress={this.state.currentprogress}
                    maxprogress={this.state.maxprogress}
                    closemodal={() => this.setprogressmodalactive(false)}
                    abortprogress={this.abortprogress}
                />
                <ConfigurationPanel ref={this.confmodalref} onNodeUpdate={this.onNodeUpdateCallback}/>
                <div className='cnnflow'>
                    <Toolbar
                        hardcodedcnntesting={this.HardcodedCNNConfiguration}
                        constructsequence={this.Construct}
                        websocketconnected={this.state.websocketconnected}
                        backendiscpu={this.state.backendiscpu}
                        backendisgpu={this.state.backendisgpu}
                    />
                    <ReactFlowProvider>
                        <DragNDrop
                            setnumberofelementstosend={this.setnumberofelementstosend}
                            sendnodedataforsaving={this.sendnodedataforsaving}
                            sendedgedataforsaving={this.sendedgedataforsaving}
                            nodeTypes={this.nodeTypes}
                            onNodeDoubleClick={this.onNodeDoubleClick}
                            preparenodedata={CNNNodeService.preparedata}
                            ref={this.dndref}
                        />
                    </ReactFlowProvider>
                </div>
            </div>
        )
    }
}