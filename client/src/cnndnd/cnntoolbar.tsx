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
                    <div className='description'>DL4J BACKEND</div>
                    <DL4JBackend 
                        websocketconnected={this.props.websocketconnected}
                        backendiscpu={this.props.backendiscpu}
                        backendisgpu={this.props.backendisgpu}
                    />
                </div>

                <div style={{ margin: 5, padding: 5, }}>
                    <div className='description'>ACTION</div>
                    <Button className='toolbar-element' block onClick={this.props.hardcodedcnntesting} disabled={! this.props.websocketconnected}>CNN(TEST)</Button>
                    <Button className='toolbar-element' block onClick={this.props.constructsequence} disabled={! this.props.websocketconnected}>CONSTRUCT</Button>
                </div>

                <div style={{ margin: 5, padding: 5, }}>   
                    <div className='description'>IMAGE PRE-PROCESS</div>
                    {/* FLIP IMAGE */}
                    <ToolboxNode nodetype="FlipImage" nodename="Flip" />
                    
                    {/* ROTATE IMAGE */}
                    <ToolboxNode nodetype="RotateImage" nodename="Rotate" />

                    {/* RESIZE IMAGE */}
                    <ToolboxNode nodetype="ResizeImage" nodename="Resize" />
                </div>

                {/* SECTION FOR TRAINING DATASET */}
                <div style={{ margin: 5, padding: 5, }}>   
                    <div className='description'>DATASETS</div>
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
                    <ToolboxNode nodetype="GenerateDatasetIterator" nodename="Image Date Iterator" />

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
                    <div className='description'>NN</div>

                    {/* TRAIN CNN */}
                     <ToolboxNode nodetype='TrainCNN' nodename='Train' />

                    {/* VALIDATE */}
                    <ToolboxNode nodetype='ValidateCNN' nodename="Validate" />

                    {/* EXPORT */}
                    <ToolboxNode nodetype="ExportCNN" nodename="Export" />

                </div>
                {/* SECTION FOR CONVOLUTIONAL NEURAL NETWORK  */}
                <div style={{ margin: 5, padding: 5, }}>
                    <div className='description'>CNN</div>
                    {/* CNN START NODE */}
                    <ToolboxNode nodetype="CNNStartNode" nodename="NN (S)" />

                    {/* CNN CONFIGURATION */}
                    <ToolboxNode nodetype="CNNConfiguration" nodename="CNN Configurations" />

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
                    <ToolboxNode nodetype="ConstructCNN" nodename="CNN Construct" />
                </div>

                {/* SECTION FOR CONVOLUTIONAL NEURAL NETWORK  */}
                <div style={{ margin: 5, padding: 5, }}>
                    <div className='description'>RNN</div>
                     {/* RNN STARTNODE */}
                     <ToolboxNode nodetype="RNNStartNode" nodename=" NN (S)" />


                    {/* RNN CONFIGURATION */}
                    <ToolboxNode nodetype="RNNConfiguration" nodename="RNN Configurations" />

                    {/* ADD INPUT */}
                    <ToolboxNode nodetype="AddInput" nodename="Add Input" />

                    {/* SET OUPUT */}
                    <ToolboxNode nodetype="SetOutput" nodename="Set Output" />

                    {/* LSTM */}
                    <ToolboxNode nodetype="LSTM" nodename="LSTM" />

                    {/* RNN OUPUT LAYER */}
                    <ToolboxNode nodetype="RnnOutputLayer" nodename="Rnn Output Layer" />

                    {/* CONSTRUCT NETWORK RNN */}
                    <ToolboxNode nodetype="ConstructNetworkRNN" nodename="RNN Construct" />
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