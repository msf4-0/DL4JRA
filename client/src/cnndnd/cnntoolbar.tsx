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
                    
                    {/* EXPORT */}
                    <ToolboxNode nodetype="ExportNN" nodename="Export" />
                </div>


                {/* SECTION FOR TRAINING DATASET */}
                <div style={{ margin: 5, padding: 5, }}>   
                    <div className='description' style={{ fontSize:20, }}>------------------</div>
                    <div className='description' style={{ fontSize:20, }}>------------------</div>
                    <div className='description'style={{ fontSize:20, }}>Neural Network</div>
                    <div className='description' > Train & Validate </div>
                    {/* TRAIN CNN */}
                     <ToolboxNode nodetype='TrainNN' nodename='Train' />

                    {/* VALIDATE */}
                    <ToolboxNode nodetype='ValidateNN' nodename="Validate" />
                    <div className='description'style={{ fontSize:17, }}>NN Datasets</div>

                    <div className='description' >Image inputs</div>
                    
                    {/* DATASET AUTO SPLIT STARTNODE  */}
                    <ToolboxNode nodetype="DatasetAutoSplitStartNode" nodename="StartNode Auto-Split" />

                    {/* TRAINING DATASET STARTNODE  */}
                    <ToolboxNode nodetype="TrainingDatasetStartNode" nodename="Training StartNode Image" />

                    {/* VALIDATION DATASET STARTNODE  */}
                    <ToolboxNode nodetype="ValidationDatasetStartNode" nodename="Validating StartNode Image" />

                    {/* LOAD TRAINING DATASET  */}
                    <ToolboxNode nodetype="LoadDataset" nodename="Load Image Dataset" />

                    {/* LOAD TRAINING DATASET  */}
                    <ToolboxNode nodetype="GenerateDatasetIterator" nodename="Image Data Iterator" />

                    <div style={{ margin: 2, padding: 2, }}>   
                        <div className='description'>Image preprocessing </div>
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
                    <ToolboxNode nodetype="ValidationDatasetStartNodeCSV" nodename="Validation StartNode CSV" />

                    {/* LOAD TRAINING DATASET CSV  */}
                    <ToolboxNode nodetype="LoadDatasetCSV" nodename="Load TimeSeries CSV" />

                    {/* LOAD TRAINING DATASET CSV General */}
                    <ToolboxNode nodetype="LoadCsvDataGeneral" nodename="Load Numerical CSV" />
                    
                    {/* LOAD TRAINING DATASET  */}
                    <ToolboxNode nodetype="GenerateDatasetIteratorCSV" nodename="CSV Data Iterator" />
                </div>


                {/* SECTION FOR CONVOLUTIONAL NEURAL NETWORK (Multilayer configuration) */}
                <div style={{ margin: 5, padding: 5, }}>
                    <div className='description' style={{ fontSize:20, }}>------------------</div>
                    {/* <div className='description'>MultiLayer</div> */}
                    <div className='description'>Multilayer Network</div>
                    <div className='description'>Configuration Network</div>
                    
                    {/* CNN START NODE */}
                    <ToolboxNode nodetype="CNNStartNode" nodename="MulitiLayer StartNode" />

                    {/* CNN CONFIGURATION */}
                    {/* <ToolboxNode nodetype="CNNConfiguration" nodename="MultiLayer Config" /> */}
                    <ToolboxNode nodetype="CNNConfiguration" nodename="Multilayer Config" />


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
                     <ToolboxNode nodetype="SetInputType" nodename="Image Input Configuration" />

                    {/* CONSTRUCT CNN NODE */}
                    <ToolboxNode nodetype="ConstructCNN" nodename="Construct Multilayer Network" />
                </div>

                <div style={{ margin: 5, padding: 5, }}>

                    <div className="description"> Pretrained- Models</div>
                    <div className="description"> Models for Image</div>
                    <div className="description"> Classification</div>

                    {/* ImportVgg16 */}
                    <ToolboxNode nodetype="ImportVgg16" nodename="Import Vgg16" />

                    {/* ImportVgg19 */}
                    <ToolboxNode nodetype="ImportVgg19" nodename="Import Vgg19" />

                    {/* ImportSqueezeNet */}
                    <ToolboxNode nodetype="ImportSqueezeNet" nodename="Import SqueezeNet" />

                    {/* ConfigTransferLearning_IClassification */}
                    <ToolboxNode nodetype="ConfigTransferLearning_IClassification" nodename="Config Transfer Learning Image Classification" />

                </div>


                {/* SECTION FOR NEURAL NETWORK (Computation graph configuration)  */}
                <div style={{ margin: 5, padding: 5, }}>
                <div className='description' style={{ fontSize:20, }}>------------------</div>
                    <div className='description'>Computation Graph</div>
                    <div className='description'>Configuration Network</div>
                    {/* RNN STARTNODE */}
                    <ToolboxNode nodetype="RNNStartNode" nodename=" ComputatinGraph StartNode" />

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
                    
                </div>


                {/* SECTION FOR SEGMENTATION */}
                <div style={{ margin: 5, padding: 5, }}>
                <div className='description' style={{ fontSize:20, }}>------------------</div>
                    <div className='description' style={{ fontSize:20, }}>------------------</div>
                    <div className='description'style={{ fontSize:20, }}>Segmentation</div>
                    <div className='description' > Train & Validate </div>
                    {/* train_segmentation */}
                    <ToolboxNode nodetype="train_segmentation" nodename=" Train " />

                    {/* validation_segmentation */}
                    <ToolboxNode nodetype="validation_segmentation" nodename="Validate" />

                    <div className='description'>Segmentation Load </div>
                    <div className='description'>Data </div>
                    {/* segmentationDataStartNode */}
                    <ToolboxNode nodetype="segmentationDataStartNode" nodename=" Data Start Node" />

                    {/* setIterator_segmentation */}
                    <ToolboxNode nodetype="setIterator_segmentation" nodename="Setup Iterator" />

                    {/* generateIterator */}
                    <ToolboxNode nodetype="generateIterator" nodename=" Generate Iterator " />
                    


                    <div className='description'>Segmentation Network</div>
                    <div className='description'>Configuration</div>


                    {/* segmentationStartnode */}
                    <ToolboxNode nodetype="segmentationStartnode" nodename=" Segmentation StartNode" />

                    {/* importPretrainedModel */}
                    <ToolboxNode nodetype="importPretrainedModel" nodename="Import Unet" />

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

                </div>

                
                {/* SECTION FOR Editing pre trained model for object detection */}
                    <div style={{ margin: 5, padding: 5, }}>
                    <div className='description' style={{ fontSize:20, }}>------------------</div>
                    <div className='description' style={{ fontSize:20, }}>------------------</div>
                    <div className='description'style={{ fontSize:20, }}>Object Detection</div>

                    {/* Train_Test_PretrainedModel */}
                    <ToolboxNode nodetype="Train_Test_PretrainedModel" nodename=" Train & Test" />
                    
                    <div className='description'> OD Dataset</div>
                    {/* ODetectionStartNode */}
                    <ToolboxNode nodetype="ODetectionStartNode" nodename=" Dataset ODetection (S)" />

                    {/* LoadDatasetODetection */}
                    <ToolboxNode nodetype="LoadDatasetODetection" nodename="Load Dataset ODetection" />

                    {/* GenerateDatasetIteratorODetection */}
                    <ToolboxNode nodetype="GenerateDatasetIteratorODetection" nodename="Dataset Iterator ODetection" />

                    <div className='description' >Train</div>
                    <div className='description' >Pretrained Model</div>
                    {/* EditPretrainedStartNode */}
                    <ToolboxNode nodetype="EditPretrainedStartNode" nodename=" StartNode (S) " />


                    {/* ImportTinyYolo */}
                    <ToolboxNode nodetype="ImportTinyYolo" nodename="Import New TinyYolo" />

                    {/* ImportYolo2 */}
                    <ToolboxNode nodetype="ImportYolo2" nodename="Import Yolo2" />

                    {/* LoadPretrainedModel */}
                    <ToolboxNode nodetype="LoadPretrainedModel" nodename=" Load Existing TinyYolo" />
                     
                     {/* ConfigTransferLearningNetwork_ODetection */}
                     <ToolboxNode nodetype="ConfigTransferLearningNetwork_ODetection" nodename=" Configure Transfer Learning" />
                    




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

