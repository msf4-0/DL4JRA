import { AlignHorizontalCenter } from '@mui/icons-material';
import { textAlign } from '@mui/system';
import {Component, DragEvent} from 'react'
import {Button} from 'reactstrap'
import DL4JBackend from "../globalcomponents/glbbackend"

/**
 * CNNToolbar props
 * hardcodedcnntesting: Callback function when "CNN(TEST)" button is clicked 
 * constructsequence: Callback function when "CONSTRUCT" button is clicked
 * websocketconnected: If client is connected to server's websocket
 * backendiscpu: If server (DL4J) is using CPU as backend (FALSE if not connected to server's websocket)
 * backendisgpu: If server (DL4J) is using GPU as backend (FALSE if not connected to server's websocket)
 */
interface CNNToolbarprops {
    hardcodedcnntesting : (() => void);
    constructsequence: (() => void);
    websocketconnected: boolean;
    backendiscpu: boolean;
    backendisgpu: boolean;
}

export default class Toolbar extends Component <CNNToolbarprops> {
    onDragStart = (event : DragEvent<HTMLDivElement> , nodeType: string) => {
        event.dataTransfer.setData('application/reactflow', nodeType);
        event.dataTransfer.effectAllowed = 'move';
    }

    render = () => {
        return (
            <aside className='cnn-toolbar'>
                <div style={{ margin: 5, padding: 5, }}>
                    <div className='description'style={{ fontSize:20, }}>DL4J BACKEND</div>
                    <DL4JBackend 
                        websocketconnected={this.props.websocketconnected}
                        backendiscpu={this.props.backendiscpu}
                        backendisgpu={this.props.backendisgpu}
                    />
                </div>

                <div style={{ margin: 5, padding: 5, }}>
                    <div className='description'style={{ fontSize:20, }}>ACTION</div>
                    {/* <Button className='toolbar-element' block onClick={this.props.hardcodedcnntesting} disabled={! this.props.websocketconnected}>CNN(TEST)</Button> */}
                    <Button className='toolbar-element' block onClick={this.props.constructsequence} disabled={! this.props.websocketconnected}>CONSTRUCT</Button>
                </div>


                {/* SECTION FOR TRAINING DATASET */}
                <div style={{ margin: 5, padding: 5, }}>   
                    <div className='description' style={{ fontSize:20, }}>------------------</div>
                    <div className='description'style={{ fontSize:20, }}>DATASETS</div>
                    <div className='description' >Image inputs</div>
                    
                    {/* DATASET AUTO SPLIT STARTNODE  */}
                    <ToolboxNode nodetype="DatasetAutoSplitStartNode" nodename="StartNode Auto-Split" />

                    {/* TRAINING DATASET STARTNODE  */}
                    <ToolboxNode nodetype="TrainingDatasetStartNode" nodename="Training StartNode Image" />

                    {/* VALIDATION DATASET STARTNODE  */}
                    <ToolboxNode nodetype="ValidationDatasetStartNode" nodename="Validating StartNode Image" />

                    {/* LOAD TRAINING DATASET  */}
                    <ToolboxNode nodetype="LoadDataset" nodename="Load DS" />

                    {/* LOAD TRAINING DATASET  */}
                    <ToolboxNode nodetype="GenerateDatasetIterator" nodename="Image Data Iterator" />

                    
                    <div style={{ margin: 5, padding: 5, }}>   
                        <div className='description'>Image preprocess</div>
                        {/* FLIP IMAGE */}
                        <ToolboxNode nodetype="FlipImage" nodename="Flip" />
                        
                        {/* ROTATE IMAGE */}
                        <ToolboxNode nodetype="RotateImage" nodename="Rotate" />

                        {/* RESIZE IMAGE */}
                        <ToolboxNode nodetype="ResizeImage" nodename="Resize" />
                    </div>

                    <div className='description' >CSV inputs</div>

                    {/* TRAINING DATASET STARTNODE  */}
                    <ToolboxNode nodetype="TrainingDatasetStartNodeCSV" nodename="Training StartNode CSV" />

                    {/* VALIDATION DATASET STARTNODE  */}
                    <ToolboxNode nodetype="ValidationDatasetStartNodeCSV" nodename="Validating StartNode CSV" />

                    {/* LOAD TRAINING DATASET CSV  */}
                    <ToolboxNode nodetype="LoadDatasetCSV" nodename="Load DS" />

                    {/* LOAD TRAINING DATASET  */}
                    <ToolboxNode nodetype="GenerateDatasetIteratorCSV" nodename="CSV Data Iterator" />
                </div>

                 {/* SECTION FOR CONVOLUTIONAL NEURAL NETWORK  */}
                 <div style={{ margin: 5, padding: 5, }}>
                    <div className='description' style={{ fontSize:20, }}>------------------</div>
                    <div className='description' style={{ fontSize:20, }}>NN</div>

                    {/* TRAIN CNN */}
                     <ToolboxNode nodetype='TrainNN' nodename='Train' />

                    {/* VALIDATE */}
                    <ToolboxNode nodetype='ValidateNN' nodename="Validate" />

                    {/* EXPORT */}
                    <ToolboxNode nodetype="ExportNN" nodename="Export" />

                </div>
                {/* SECTION FOR CONVOLUTIONAL NEURAL NETWORK (Multilayer configuration) */}
                <div style={{ margin: 5, padding: 5, }}>
                    <div className='description'>MultiLayer</div>
                    <div className='description'>Configuration Network</div>
                    
                    {/* CNN START NODE */}
                    <ToolboxNode nodetype="CNNStartNode" nodename="MulitiLayer (S)" />

                    {/* CNN CONFIGURATION */}
                    <ToolboxNode nodetype="CNNConfiguration" nodename="MultiLayer Config" />

                    {/* CONVOLUTION LAYER */}
                    <ToolboxNode nodetype="ConvolutionLayer" nodename="Convolutional" />

                    {/* SUBSAMPLING LAYER */}
                    <ToolboxNode nodetype="SubsamplingLayer" nodename="Subsampling" />

                     {/* DENSE LAYER */}
                     <ToolboxNode nodetype="DenseLayer" nodename="Dense" />

                     {/* OUTPUT LAYER */}
                     <ToolboxNode nodetype="OutputLayer" nodename="Output" />

                    {/* ============================================================================================ */}
                    {/* LOCAL RESPONSE NORMALIZATION LAYER */}
                    <ToolboxNode nodetype="LocalResponseNormalizationLayer" nodename="Local Response Normalization" />

                     {/* SET INPUT TYPE */}
                     <ToolboxNode nodetype="SetInputType" nodename="I.Type" />

                    {/* CONSTRUCT CNN NODE */}
                    <ToolboxNode nodetype="ConstructCNN" nodename="MultiLayer Construct" />
                </div>

                {/* SECTION FOR NEURAL NETWORK (Computation graph configuration)  */}
                <div style={{ margin: 5, padding: 5, }}>
                    <div className='description'>Computation Graph</div>
                    <div className='description'>Configuration Network</div>
                    {/* RNN STARTNODE */}
                    <ToolboxNode nodetype="RNNStartNode" nodename=" ComputatinGraph (S)" />

                    {/* RNN CONFIGURATION */}
                    <ToolboxNode nodetype="RNNConfiguration" nodename="ComputationGraph Config" />

                    {/* ADD INPUT */}
                    <ToolboxNode nodetype="AddInput" nodename="Add Input" />

                    {/* SET OUPUT */}
                    <ToolboxNode nodetype="SetOutput" nodename="Set Output" />

                    {/* CONVOLUTION 1D LAYER */}
                    <ToolboxNode nodetype="Convolution1DLayer" nodename="Convolutional" />

                    {/* LSTM */}
                    <ToolboxNode nodetype="LSTM" nodename="LSTM" />

                    {/* RNN OUPUT LAYER */}
                    <ToolboxNode nodetype="RnnOutputLayer" nodename="Rnn Output Layer" />

                    {/* CONSTRUCT NETWORK RNN */}
                    <ToolboxNode nodetype="ConstructNetworkRNN" nodename="ComputationGraph Construct" />
                    
                    {/* EVALUATE MODLE RNN */}
                    <ToolboxNode nodetype="EvaluateModelRNN" nodename="Evaluate Model" />
                </div>

                    {/* SECTION FOR SEGMENTATION */}
                    <div style={{ margin: 5, padding: 5, }}>
                    <div className='description' style={{ fontSize:20, }}>------------------</div>
                    <div className='description' style={{ fontSize:20, }}>SEGMENTATION</div>
                    {/* segmentationStartnode */}
                    <ToolboxNode nodetype="segmentationStartnode" nodename=" Segmentation (S)" />

                    {/* importPretrainedModel */}
                    <ToolboxNode nodetype="importPretrainedModel" nodename="Pretrained Model" />

                    {/* configureFineTune */}
                    <ToolboxNode nodetype="configureFineTune" nodename="Configure FineTune" />

                    {/* configureTranferLearning */}
                    <ToolboxNode nodetype="configureTranferLearning" nodename="Configure Transfer Learning" />

                    {/* addCnnLossLayer */}
                    <ToolboxNode nodetype="addCnnLossLayer" nodename="Add CnnLoss Layer" />

                    {/* setOutput */}
                    <ToolboxNode nodetype="setOutput_segmentation" nodename="Set Output" />

                    {/* build_TransferLearning */}
                    <ToolboxNode nodetype="build_TransferLearning" nodename="Build Transfer Learning" />

                    <div className='description'>SEGMENTATION LOAD </div>
                    <div className='description'>DATA n EVALUATION </div>
                    {/* segmentationDataStartNode */}
                    <ToolboxNode nodetype="segmentationDataStartNode" nodename=" Data Start Node" />

                    {/* setIterator_segmentation */}
                    <ToolboxNode nodetype="setIterator_segmentation" nodename="Setup Iterator" />

                    {/* generateIterator */}
                    <ToolboxNode nodetype="generateIterator" nodename=" Generate Iterator " />
                    
                    {/* train_segmentation */}
                    <ToolboxNode nodetype="train_segmentation" nodename=" Train " />

                    {/* validation_segmentation */}
                    <ToolboxNode nodetype="validation_segmentation" nodename="Validate" />
                </div>


            </aside>
        )
    }
}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * ToolboxNode props
 * nodetype: The nodetype of the toolbox node
 * nodename: Name of the node (Appear in Toolbar)
*/
interface ToolboxNodeProps {
    nodetype: string;
    nodename: string;
}

class ToolboxNode extends Component <ToolboxNodeProps, {}> {
    OnDragStart = (event : DragEvent<HTMLDivElement> , nodeType: string) => {
        event.dataTransfer.setData('application/reactflow', nodeType);
        event.dataTransfer.effectAllowed = 'move';
    }

    render = () => {
        return (
            <div className="toolbar-element toolbar-node" onDragStart={(event) => this.OnDragStart(event, this.props.nodetype)} draggable>
                {this.props.nodename}
            </div>
        )
    }
}