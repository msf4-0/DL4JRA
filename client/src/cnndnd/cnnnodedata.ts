import {Dictionary, Flipmode, ActivationFunctionTypes, ConvolutionModeTypes, WeightInitTypes, GradientNormalizationTypes, OptimizationAlgorithmTypes, LossFunctionTypes, PoolingType} from "../globalcomponents/interfaces"

class CNNNodeService {
    /**
     * Prepare/initialize data for each node
     * Note that different node type will have different data
     * Data appears in all node -> name
     * @param nodetype 
     * @returns nodedata
    */
    preparedata = (nodetype: string) : Dictionary => {
        switch(nodetype) {
            case "FlipImage":
                return this.prepareFlipImage();
            case "RotateImage":
                return this.prepareRotateImage();
            case "ResizeImage":
                return this.prepareResizeImage();
            case "DatasetAutoSplitStartNode":
                return { name: "(S) D-AS" };
            case "TrainingDatasetStartNode":
                return { name: "(S) TD" };
            case "ValidationDatasetStartNode":
                return { name: "(S) VD" };
            case "LoadDataset":
                return this.prepareLoadDataset();
            case "GenerateDatasetIterator":
                return { name: "Dataset Iterator" };
            case "CNNStartNode":
                return { name: "Startnode" };
            case "CNNConfiguration":
                return this.prepareCNNConfiguration();
            case "ConvolutionLayer":
                return this.prepareConvolutionLayer();
            case "SubsamplingLayer":
                return this.prepareSubsamplingLayer();
            case "DenseLayer":
                return this.prepareDenseLayer();
            case "OutputLayer":
                return this.prepareOutputLayer();
            case "LocalResponseNormalizationLayer":  // ==========================================================
                return this.prepareLocalResponseNormalizationLayer();
            case "SetInputType":
                return this.prepareSetInputType();
            case "ConstructCNN":
                return { name: "Construct" };
            case "TrainCNN":
                return this.prepareTrainCNN();
            case "ValidateCNN":
                return { name: "Validate" }
            case "ExportCNN":
                return this.prepareExportCNN();
            default:
                return { name: nodetype };
        }
    }

    /**
     * FlipImage node data
     * 1. flipmode (Default = FLIP_BOTH_AXIS)
    */
    prepareFlipImage = () : Dictionary => {
        let name = "Flip";
        let flipmode = Flipmode.FLIP_BOTH_AXIS;
        return { name, flipmode };
    }

    /**
     * RotateImage node data
     * 1. angle - Angle of rotation (Default = 0)
    */
    prepareRotateImage = () : Dictionary => {
        let name = "Rotate";
        let angle = 0;
        return { name, angle };
    }

    /**
     * ResizeImage node data
     * 1. imagewidth - New image width (Default = 100)
     * 2. imageheight - New image height (Default = 100)
    */
    prepareResizeImage = () : Dictionary => {
        let name = "Resize";
        let imagewidth = 100;
        let imageheight = 100;
        return { name, imagewidth, imageheight };
    }

    /**
     * LoadDataset node data
     * 1. path - Path to dataset folder (Default = "C://DatasetFolder")
     * 2. imagewidth - Dataset's image width (Default = 50)
     * 3. imageheight - Dataset's image height (Default = 50)
     * 4. channels - Dataset's image color channel (Default = 3)
     * 5. numLabels - Number of labels/classes (Default = 2)
     * 6. batchsize - Dataset iterator batchsize (Default = 1)
    */
    prepareLoadDataset = () : Dictionary => {
        let name = "Load dataset";
        let path = "C://Users//User//.deeplearning4j//data//dog-breed-identification";
        let imagewidth = 224;
        let imageheight = 224;
        let channels = 3;
        let numLabels = 5;
        let batchsize = 32;
        return {name, path, imagewidth, imageheight, channels, numLabels, batchsize };
    }

    /**
     * CNNConfiguration node data
     * 1. seed - Random seed
     * 2. learningrate - Learning rate of network/model (Default = 0.001)
     * 3. optimizationalgorithm - Optimization algorithm used by network (Default = STOCHASTIC_GRADIENT_DESCENT)
    */
    prepareCNNConfiguration = () : Dictionary => {
        let name="Configuration";
        let seed=Math.floor(Math.random() * 1000);
        let learningrate = 0.001;
        let optimizationalgorithm = OptimizationAlgorithmTypes.STOCHASTIC_GRADIENT_DESCENT;
        let convolutionMode = ConvolutionModeTypes.Truncate;
        let activationfunction = ActivationFunctionTypes.RELU;
        let weightInit = WeightInitTypes.XAVIER;     
        let gradientNormalization = GradientNormalizationTypes.RenormalizeL2PerLayer
        return {name, seed, learningrate, optimizationalgorithm, convolutionMode, activationfunction, weightInit,
             gradientNormalization};
    }

    /**
     * ConvolutionLayer node data
     * 1. kernalx/kernaly - Dimension (size) of kernal used by convolution layer (Default = 2)
     * 2. stridex/stridey - Number of pixels (dimension) shifts over the input matrix (Default = 1)
     * 3. nIn - Number of input (In convo layer, nIn = color channels of dataset) (Default = 3)
     * 4. nOut - Number of output (Default = 10)
     * 5. activationfunction - Activation function used by layer (Default = RELU)
    */
    prepareConvolutionLayer = () : Dictionary => {
        let name = "Convolutional Layer";
        // let ifEdit = true;
        let kernalx = 2, kernaly = 2;
        let stridex = 1, stridey = 1;
        // ===========================================================================
        let paddingx = 0, paddingy = 0;
        let nIn = 3;
        let nOut = 10;
        let activationfunction = ActivationFunctionTypes.RELU;        
        let dropOut = 0, biasInit = 0;
        let convolutionMode = ConvolutionModeTypes.Truncate;
        return {name, kernalx, kernaly, stridex, stridey, paddingx, paddingy, nIn, nOut, activationfunction,
             dropOut, biasInit, convolutionMode};
    }

    /**
     * SubsamplingLayer node data
     * 1. kernalx/kernaly - Dimension (size) of kernal used by subsampling layer (Default = 2)
     * 2. stridex/stridey - Number of pixels (dimension) shifts over the input matrix (Default = 1)
     * 3. poolingtype - Pooling type used in current layer (Default = MAX)
    */
    prepareSubsamplingLayer = () : Dictionary => {
        let name = "Subsampling Layer";
        let kernalx = 2, kernaly = 2;
        let stridex = 1, stridey = 1;
        // ===========================================================================
        let paddingx = 0, paddingy = 0;
        let poolingtype = PoolingType.MAX;
        let convolutionMode = ConvolutionModeTypes.Truncate;
        return { name, kernalx, kernaly, stridex, stridey, paddingx, paddingy, poolingtype,
            convolutionMode};
    }

    /**
     * DenseLayer node data
     * 1. nIn - Input size/ Output size of previous layer (Default = 0)
     * 2. nOut - Output size/ Number of node in dense layer (Default = 5)
     * 3. activationfunction - Activation function used by layer (Default = RELU)
    */
    prepareDenseLayer = () : Dictionary => {
        let name = "Dense Layer";
        let nIn = 0;
        let nOut = 5;
        let activationfunction = ActivationFunctionTypes.RELU;        
        let dropOut = 0, biasInit = 0;
        let weightInit = WeightInitTypes.XAVIER
        return {name, nIn, nOut, activationfunction, dropOut, biasInit, weightInit};
    }

    /**
     * OutputLayer node data
     * 1. nIn - Input size/ Output size of previous layer (Default = 10)
     * 2. nOut - Number of output/classes (Default = 2)
     * 3. activationfunction - Activation function used by layer (Default = RELU)
     * 4. lossfunction - Loss function used by output layer (Default = NEGATIVELOGLIKELIHOOD)
    */
    prepareOutputLayer = () : Dictionary => {
        let name = "Output Layer";
        let nIn = 3;
        let nOut = 2;
        let activationfunction = ActivationFunctionTypes.RELU;
        let lossfunction = LossFunctionTypes.NEGATIVELOGLIKELIHOOD;
        let weightInit = WeightInitTypes.XAVIER
        return { name, nIn, nOut, activationfunction, lossfunction, weightInit };
    }

    //========================================================================================
    prepareLocalResponseNormalizationLayer = () : Dictionary => {
        let name = "Local Response Normalization Layer";
        return { name };
    }
    

    /**
     * SetInputType node data
     * 1. imagewidth - Dataset's (input) image width (Default = 50)
     * 2. imageheight - Dataset's (input) image height (Default = 50)
     * 3. channels - Dataset's (input) image color channel (Default = 3)
    */
    prepareSetInputType = () : Dictionary => {
        let name = "Input type"
        let imagewidth = 50;
        let imageheight = 50;
        let channels = 3;
        return { name, imagewidth, imageheight, channels };
    }

    /**
     * TrainCNN node data
     * 1. epochs - Number of epochs for training (Default = 200)
     * 2. scorelistener - Interval to calculate/evaluate the score of network (Default = 10)
    */
    prepareTrainCNN = () : Dictionary => {
        let name = "Train CNN";
        let epochs = 200;
        let scoreListener = 10;
        return { name, epochs, scoreListener };
    }

    /**
     * ExportCNN node data
     * 1. path - Directory to save the model
     * 2. filename - Filename of the model (saved as .zip)
    */
    prepareExportCNN = () : Dictionary => {
        let name = "Export CNN";
        let path = "";
        let filename = "";
        return { name, path, filename };
    }
}


export default new CNNNodeService();