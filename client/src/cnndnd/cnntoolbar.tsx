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
                    
                    {/* TRAINING DATASET STARTNODE  */}
                    <ToolboxNode nodetype="TrainingDatasetStartNode" nodename="TDS (S)" />

                    {/* VALIDATION DATASET STARTNODE  */}
                    <ToolboxNode nodetype="ValidationDatasetStartNode" nodename="VDS (S)" />

                    {/* LOAD TRAINING DATASET  */}
                    <ToolboxNode nodetype="LoadDataset" nodename="Load DS" />

                    {/* LOAD TRAINING DATASET  */}
                    <ToolboxNode nodetype="GenerateDatasetIterator" nodename="D.Iterator" />
                </div>


                {/* SECTION FOR CONVOLUTIONAL NEURAL NETWORK  */}
                <div style={{ margin: 5, padding: 5, }}>
                    <div className='description'>CNN</div>

                    {/* CNN START NODE */}
                    <ToolboxNode nodetype="CNNStartNode" nodename="NN (S)" />

                    {/* CNN CONFIGURATION */}
                    <ToolboxNode nodetype="CNNConfiguration" nodename="Configurations" />

                    {/* CONVOLUTION LAYER */}
                    <ToolboxNode nodetype="ConvolutionLayer" nodename="Convolutional" />

                    {/* SUBSAMPLING LAYER */}
                    <ToolboxNode nodetype="SubsamplingLayer" nodename="Subsampling" />

                     {/* DENSE LAYER */}
                     <ToolboxNode nodetype="DenseLayer" nodename="Dense" />

                     {/* OUTPUT LAYER */}
                     <ToolboxNode nodetype="OutputLayer" nodename="Output" />

                     {/* SET INPUT TYPE */}
                     <ToolboxNode nodetype="SetInputType" nodename="I.Type" />

                    {/* CONSTRUCT CNN NODE */}
                    <ToolboxNode nodetype="ConstructCNN" nodename="Construct" />

                     {/* TRAIN CNN */}
                     <ToolboxNode nodetype='TrainCNN' nodename='Train' />

                     {/* VALIDATE */}
                    <ToolboxNode nodetype='ValidateCNN' nodename="Validate" />

                    {/* EXPORT */}
                    <ToolboxNode nodetype="ExportCNN" nodename="Export" />
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