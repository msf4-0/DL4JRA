import React, {Component, } from 'react'
import { Handle, Position, NodeProps } from 'react-flow-renderer'

/**
 * Background color of each node type
 * Feel free to change into any hex colour you like!
*/
enum NodeBackgroundColors  {
    ErrorBackground = "#FF5050",
    ImagePreprocessing = "#D4FFCB",
    TrainingDataset = "#CBFFF8",
    ValidationDataset = "#F5FFCB",
    CNNStartNode = "#4B5191",
    CNNConfiguration = "#4B5191",
    ConvolutionLayer = "#4B5191",
    SubsamplingLayer = "#4B5191",
    DenseLayer = "#4B5191",
    OutputLayer = "#4B5191",
    SetInputType = "#4B5191",
    ConstructCNN = "#4B5191",
    TrainCNN = "#4B5191",
    ValidateCNN = "#4B5191",
    ExportCNN = "#4B5191",
}

/**
 * Text color of each node type
 * Dark background -> Bright text color
 * Bright background -> Dark text color
*/
enum NodeTextColors {
    Error= "black",
    ImagePreprocessing = "black",
    TrainingDataset = "black",
    ValidationDataset = "black",
    CNNStartNode = "white",
    CNNConfiguration = "white",
    ConvolutionLayer = "white",
    SubsamplingLayer = "white",
    DenseLayer = "white",
    OutputLayer = "white",
    SetInputType = "white",
    ConstructCNN = "white",
    TrainCNN = "white",
    ValidateCNN = "white",
    ExportCNN = "white",
}


// Incoming handle (LEFT SIDE) component of node
class IncomingConnectionHandle extends Component {

    render = () => {
        return (
            <Handle
                type='target'
                className='node-custom-handle'
                position={Position.Left}
                style={{background: "#000000"}}
            >
            </Handle>
        )
    }
}

// Outgoing handle (RIGHT SIDE) component of node
class OutgoingConnectionHandle extends Component {

    render = () => {
        return (
            <Handle
                type='source'
                className='node-custom-handle'
                position={Position.Right}
                style={{background: "#000000"}}
            >
            </Handle>
        )
    }
}

// FlipImage node
export class FlipImage extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.ImagePreprocessing, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.ImagePreprocessing, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// RotateImage node
export class RotateImage extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.ImagePreprocessing, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.ImagePreprocessing, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// ResizeImage node
export class ResizeImage extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.ImagePreprocessing, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.ImagePreprocessing, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// TrainingDatasetStartNode node
export class TrainingDatasetStartNode extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.TrainingDataset, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.TrainingDataset, 
            }}>
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// ValidationDatasetStartNode node
export class ValidationDatasetStartNode extends Component <NodeProps>{
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.ValidationDataset, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.ValidationDataset, 
            }}>
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// LoadDataset node
export class LoadDataset extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.TrainingDataset, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.TrainingDataset, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// GenerateDatasetIterator node
export class GenerateDatasetIterator extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.TrainingDataset, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.TrainingDataset, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// CNNStartNode node
export class CNNStartNode extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.CNNStartNode, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.CNNStartNode, 
            }}>
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// CNNConfiguration node
export class CNNConfiguration extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.CNNConfiguration, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.CNNConfiguration, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// ConvolutionLayer node
export class ConvolutionLayer extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.ConvolutionLayer, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.ConvolutionLayer, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// SubsamplingLayer node
export class SubsamplingLayer extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.SubsamplingLayer, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.SubsamplingLayer, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// DenseLayer node
export class DenseLayer extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.DenseLayer, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.DenseLayer, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// OutputLayer node
export class OutputLayer extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.OutputLayer, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.OutputLayer,
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// SetInputType node
export class SetInputType extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.SetInputType, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.SetInputType, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// ConstructCNN node
export class ConstructCNN extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.ConstructCNN, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.ConstructCNN, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// TrainCNN node
export class TrainCNN extends Component<NodeProps>{
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.TrainCNN, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.TrainCNN, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// ValidateCNN node
export class ValidateCNN extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.ValidateCNN, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.ValidateCNN, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// ExportCNN node
export class ExportCNN extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.ExportCNN, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.ExportCNN, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}