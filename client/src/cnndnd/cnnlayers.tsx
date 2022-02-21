import React, {Component, } from 'react'
import { Handle, Position, NodeProps } from 'react-flow-renderer'

/**
 * Background color of each node type
 * Feel free to change into any hex colour you like!
*/
enum NodeBackgroundColors  {
    ErrorBackground = "#FF5050",
    ImagePreprocessing = "#D4FFCB",
    DatasetAutoSplit = "#CBFFF8",
    TrainingDataset = "#CBFFF8",
    ValidationDataset = "#F5FFCB",
    CNNStartNode = "#4B5191",
    CNNConfiguration = "#4B5191",
    ConvolutionLayer = "#4B5191",
    SubsamplingLayer = "#4B5191",
    DenseLayer = "#4B5191",
    OutputLayer = "#4B5191",
    LocalResponseNormalizationLayer = "#4B5191",   
    SetInputType = "#4B5191",
    ConstructCNN = "#4B5191",
    TrainCNN = "#4B5191",
    ValidateCNN = "#4B5191",
    ExportCNN = "#4B5191",
    lightBlue = "#4B5191",
    lightPurple = "#BA68C8",

}

/**
 * Text color of each node type
 * Dark background -> Bright text color
 * Bright background -> Dark text color
*/
enum NodeTextColors {
    Error= "black",
    ImagePreprocessing = "black",
    DatasetAutoSplit = 'black',
    TrainingDataset = "black",
    ValidationDataset = "black",
    CNNStartNode = "white",
    CNNConfiguration = "white",
    ConvolutionLayer = "white",
    SubsamplingLayer = "white",
    DenseLayer = "white",
    OutputLayer = "white",
    LocalResponseNormalizationLayer = "white",  // ===============================
    SetInputType = "white",
    ConstructCNN = "white",
    TrainCNN = "white",
    ValidateCNN = "white",
    ExportCNN = "white",
    white = "white",
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

// DatasetAutoSplitStartNode node
export class DatasetAutoSplitStartNode extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.DatasetAutoSplit, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.DatasetAutoSplit, 
            }}>
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

// TrainingDatasetStartNodeCSV node
export class TrainingDatasetStartNodeCSV extends Component<NodeProps> {
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

// ValidationDatasetStartNodeCSV node
export class ValidationDatasetStartNodeCSV extends Component <NodeProps>{
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

// LoadDataset node
export class LoadDatasetCSV extends Component<NodeProps> {
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
            </div>
        )
    }
}

// GenerateDatasetIteratorCSV node
export class GenerateDatasetIteratorCSV extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.TrainingDataset, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.TrainingDataset, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
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
export class LocalResponseNormalizationLayer extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.LocalResponseNormalizationLayer, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.LocalResponseNormalizationLayer,
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
export class TrainNN extends Component<NodeProps>{
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
export class ValidateNN extends Component<NodeProps> {
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
export class ExportNN extends Component<NodeProps> {
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

// =============================================================================================================
// CNNStartNode node
export class RNNStartNode extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightBlue, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// RNNConfiguration node
export class RNNConfiguration extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightBlue, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}
// AddInputNode node
export class AddInput extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightBlue, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// SetOutput node
export class SetOutput extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightBlue, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

// ConvolutionLayer1D node
export class Convolution1DLayer extends Component<NodeProps> {
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

// LSTM node
export class LSTM extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightBlue, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}


// RnnOutputLayer node
export class RnnOutputLayer extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightBlue, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

export class ConstructNetworkRNN extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightBlue, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}


// =============================================================================================================
// SEGMENTATION
// SegmentationStartnode
export class segmentationStartnode  extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

//ImportModel node
export class importPretrainedModel extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

//configureFineTune node
export class configureFineTune extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

//configureTranferLearning node
export class configureTranferLearning extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

//addCnnLossLayer node
export class addCnnLossLayer extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

//setOutput_segmentation node
export class setOutput_segmentation extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

//build_TransferLearning node
export class build_TransferLearning extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
            </div>
        )
    }
}

//setIterator_segmentation node
export class setIterator_segmentation extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

//generateIterator node
export class generateIterator extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

//train_segmentation node
export class train_segmentation extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}
//validation_segmentation node
export class validation_segmentation extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
            </div>
        )
    }
}
//segmentationDataStartNode
export class segmentationDataStartNode extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}


// Edit pre train model for object detection

//ODetectionStartNode node
export class ODetectionStartNode extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

//LoadDatasetODetection node
export class LoadDatasetODetection extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}

//GenerateDatasetIteratorODetection node
export class GenerateDatasetIteratorODetection extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
            </div>
        )
    }
}
//EditPretrainedStartNode node
export class EditPretrainedStartNode extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}
//ImportTinyYolo node
export class ImportTinyYolo extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}
//LoadPretrainedModel node
export class LoadPretrainedModel extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}
//ConfigTransferLearningNetwork_ODetection node
export class ConfigTransferLearningNetwork_ODetection extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
                <OutgoingConnectionHandle />
            </div>
        )
    }
}
//Train_Test_PretrainedModel node
export class Train_Test_PretrainedModel extends Component<NodeProps> {
    render = () => {
        return (
            <div 
                className="cnn-node"
                style={{ backgroundColor: this.props.data.error? NodeBackgroundColors.ErrorBackground : NodeBackgroundColors.lightPurple, 
                color: this.props.data.error? NodeTextColors.Error : NodeTextColors.white, 
            }}>
                <IncomingConnectionHandle />
                <div>{this.props.data.name}</div>
            </div>
        )
    }
}


