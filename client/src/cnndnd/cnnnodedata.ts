import {Dictionary, Flipmode, ActivationFunctionTypes, ConvolutionModeTypes, WeightInitTypes,
     GradientNormalizationTypes, OptimizationAlgorithmTypes, LossFunctionTypes, PoolingType, 
     RNNFormatTypes} from "../globalcomponents/interfaces"

class CNNNodeService {
  /**
   * Prepare/initialize data for each node
   * Note that different node type will have different data
   * Data appears in all node -> name
   * @param nodetype
   * @returns nodedata
   */
  preparedata = (nodetype: string): Dictionary => {
    switch (nodetype) {
      case "FlipImage":
        return this.prepareFlipImage();
      case "RotateImage":
        return this.prepareRotateImage();
      case "ResizeImage":
        return this.prepareResizeImage();
      case "DatasetAutoSplitStartNode":
        return { name: "StartNode Auto-Split" };
      case "TrainingDatasetStartNode":
        return { name: "Training StartNode Image" };
      case "ValidationDatasetStartNode":
        return { name: "Validating StartNode Image" };
      case "TrainingDatasetStartNodeCSV":
        return { name: "Training StartNode CSV" };
      case "ValidationDatasetStartNodeCSV":
        return { name: "Validating StartNode CSV" };
      case "LoadDataset":
        return this.prepareLoadDataset();
      case "LoadDatasetCSV":
        return this.prepareLoadDatasetCSV();
      case "GenerateDatasetIterator":
        return { name: "Dataset Iterator Image" };
      case "GenerateDatasetIteratorCSV":
        return { name: "Dataset Iterator CSV" };
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
      case "LocalResponseNormalizationLayer": // ==========================================================
        return this.prepareLocalResponseNormalizationLayer();
      case "SetInputType":
        return this.prepareSetInputType();
      case "ConstructCNN":
        return { name: "Construct" };
      case "TrainNN":
        return this.prepareTrainNN();
      case "TrainNNNoUi":
        return this.prepareTrainNNNoUi();
      case "ValidateNN":
        return { name: "Validate" };
      case "ExportNN":
        return this.prepareExportNN();
      //================= Computation Graph Configuration =================
      case "RNNStartNode":
        return { name: "Startnode" };
      case "RNNConfiguration":
        return this.prepareRNNConfiguration();
      case "AddInput":
        return this.prepareAddInput();
      case "SetOutput":
        return this.prepareSetOutput();
      case "Convolution1DLayer":
        return this.prepareConvolution1DLayer();
      case "LSTM":
        return this.prepareLSTM();
      case "RnnOutputLayer":
        return this.prepareRnnOutputLayer();
      case "ConstructNetworkRNN":
        return { name: "ComputationGraph Construct" };
      case "EvaluateModelRNN":
        return { name: "Evaluate Model" };
      //================= SEGMENTATION =================
      case "segmentationStartnode":
        return { name: "Startnode" };
      case "importPretrainedModel":
        return { name: "Import Pretained Model (UNET)" };
      case "configureFineTune":
        return this.prepareConfigureFineTune();
      case "configureTranferLearning":
        return this.prepareConfigureTranferLearning();
      case "addCnnLossLayer":
        return this.prepareAddCnnLossLayer();
      case "setOutput_segmentation":
        return this.prepareSetOutput();
      case "build_TransferLearning":
        return { name: "Build network" };
      case "segmentationDataStartNode":
        return { name: "Data StartNode" };
      case "setIterator_segmentation":
        return this.prepareSetIterator();
      case "generateIterator":
        return { name: "Generate Iterator" };
      //================= RETRAIN PRETRAINED MODEL FOR ODETECTION=================
      case "ODetectionStartNode":
        return { name: "Dataset ODetection (S) " };
      case "LoadDatasetODetection":
        return this.prepareLoadDatasetODetection();
      case "GenerateDatasetIteratorODetection":
        return this.prepareGenerateDatasetIteratorODetection();
      case "EditPretrainedStartNode":
        return { name: "ReTrainning StartNode (S) " };
      case "ImportTinyYolo":
        return { name: "Import TinyYolo " };
      case "LoadPretrainedModel":
        return this.prepareLoadPretrainedModel();
      case "ConfigTransferLearningNetwork_ODetection":
        return this.prepareConfigTransferLearningNetwork_ODetection();
      case "Train_Test_PretrainedModel":
        return this.prepareTrain_Test_PretrainedModel();
      case "ImportVgg16":
        return { name: "Import Vgg16" };
      case "ImportVgg19":
        return { name: "Import Vgg19" };
      case "ImportSqueezeNet":
        return { name: "Import SqueezeNet" };
      case "ImportYolo2":
        return { name: "Import Yolo2" };
      case "ConfigTransferLearning_IClassification":
        return this.prepareConfigTransferLearning_IClassification();
      case "LoadCsvDataGeneral":
        return this.prepareLoadDatasetCSVGeneral();

      default:
        return { name: nodetype };
    }
  };

  //RETRAIN PRETRAINED MODEL FOR ODETECTION
  prepareLoadDatasetODetection = (): Dictionary => {
    let name = "Load Dataset ODetection";
    let trainPath = "C:\\Users\\User\\.deeplearning4j\\data\\fruits\\train";
    let testPath = "C:\\Users\\User\\.deeplearning4j\\data\\fruits\\test";
    return { name, trainPath, testPath };
  };

  prepareGenerateDatasetIteratorODetection = (): Dictionary => {
    let name = "Generate Dataset Iterator ODetection";
    let batchsize = 8;
    return { name, batchsize };
  };

  prepareLoadPretrainedModel = (): Dictionary => {
    let name = "Load Pretrained Model";
    let path = "E:\\SHRDC\\models\\TINYYOLO.zip";
    return { name, path };
  };

  prepareConfigTransferLearningNetwork_ODetection = (): Dictionary => {
    let name = "Config Transfer Learning ODetection";
    let learningrate = 0.0001;
    return { name, learningrate };
  };

  prepareTrain_Test_PretrainedModel = (): Dictionary => {
    let name = "Train_Test_PretrainedModel";
    let epochs = 40;
    return { name, epochs };
  };

  // SEGMENTATION
  prepareConfigureFineTune = (): Dictionary => {
    let name = "Configure FineTune";
    let seed = 12345;
    return { name, seed };
  };

  prepareConfigureTranferLearning = (): Dictionary => {
    let name = "Configure Transfer Learning";
    let featurizeExtractionLayer = "conv2d_4";
    let vertexName = "activation_23";
    let nInName = "conv2d_1";
    let nIn = 1;
    let nInWeightInit = WeightInitTypes.XAVIER;
    let nOutName = "conv2d_23";
    let nOut = 1;
    let nOutWeightInit = WeightInitTypes.XAVIER;
    return {
      name,
      featurizeExtractionLayer,
      vertexName,
      nInName,
      nIn,
      nInWeightInit,
      nOutName,
      nOut,
      nOutWeightInit,
    };
  };

  prepareAddCnnLossLayer = (): Dictionary => {
    let name = "Add CnnLossLayer";
    let layerName = "output";
    let lossfunction = LossFunctionTypes.XENT;
    let activationfunction = ActivationFunctionTypes.SIGMOID;
    let layerInput = "conv2d_23";
    return { name, layerName, lossfunction, activationfunction, layerInput };
  };

  prepareSetIterator = (): Dictionary => {
    let name = "Setup Iterator";
    let path =
      "C:\\Users\\User\\.deeplearning4j\\data\\data-science-bowl-2018\\data-science-bowl-2018\\data-science-bowl-2018-2\\train\\inputs";
    let batchsize = 2;
    let trainPerc = 0.1;
    let channels = 1;
    let maskFolderName = "masks";
    return { name, path, batchsize, trainPerc, channels, maskFolderName };
  };

  // RNN
  prepareRNNConfiguration = (): Dictionary => {
    let name = "RNN Configuration";
    let seed = 12345;
    let learningrate = 0.05;
    let optimizationalgorithm =
      OptimizationAlgorithmTypes.STOCHASTIC_GRADIENT_DESCENT;
    let weightInit = WeightInitTypes.XAVIER;
    return { name, seed, learningrate, optimizationalgorithm, weightInit };
  };

  prepareAddInput = (): Dictionary => {
    let name = "Add Input";
    let inputName = "trainFeatures";
    return { name, inputName };
  };

  prepareSetOutput = (): Dictionary => {
    let name = "Set Output";
    let outputName = "predictActivity";
    return { name, outputName };
  };

  prepareConvolution1DLayer = (): Dictionary => {
    let name = "1D Convolutional Layer";
    let layerName = "CNN";
    let kernalSize = 1;
    let nIn = 9;
    let nOut = 32;
    let activationfunction = ActivationFunctionTypes.RELU;
    let layerInput = "trainFeatures";
    return {
      name,
      layerName,
      kernalSize,
      nIn,
      nOut,
      activationfunction,
      layerInput,
    };
  };

  prepareLSTM = (): Dictionary => {
    let name = "LSTM";
    let layerName = "LSTM";
    let nIn = 32;
    let nOut = 100;
    let activationfunction = ActivationFunctionTypes.TANH;
    let layerInput = "CNN";
    return { name, layerName, nIn, nOut, activationfunction, layerInput };
  };

  prepareRnnOutputLayer = (): Dictionary => {
    let name = "Rnn Output Layer";
    let layerName = "predictActivity";
    let RNNFormat = RNNFormatTypes.NCW;
    let nIn = 100;
    let nOut = 6;
    let lossfunction = LossFunctionTypes.MCXENT;
    let activationfunction = ActivationFunctionTypes.SOFTMAX;
    let layerInput = "LSTM";
    return {
      name,
      layerName,
      RNNFormat,
      nIn,
      nOut,
      lossfunction,
      activationfunction,
      layerInput,
    };
  };

  /**
   * FlipImage node data
   * 1. flipmode (Default = FLIP_BOTH_AXIS)
   */
  prepareFlipImage = (): Dictionary => {
    let name = "Flip";
    let flipmode = Flipmode.FLIP_BOTH_AXIS;
    return { name, flipmode };
  };

  /**
   * RotateImage node data
   * 1. angle - Angle of rotation (Default = 0)
   */
  prepareRotateImage = (): Dictionary => {
    let name = "Rotate";
    let angle = 0;
    return { name, angle };
  };

  /**
   * ResizeImage node data
   * 1. imagewidth - New image width (Default = 100)
   * 2. imageheight - New image height (Default = 100)
   */
  prepareResizeImage = (): Dictionary => {
    let name = "Resize";
    let imagewidth = 100;
    let imageheight = 100;
    return { name, imagewidth, imageheight };
  };

  /**
   * LoadDataset node data
   */
  prepareLoadDataset = (): Dictionary => {
    let name = "Load dataset";
    let path = "C://Users//User//.deeplearning4j//data//humanactivity";
    let imagewidth = 224;
    let imageheight = 224;
    let channels = 3;
    let numLabels = 5;
    let batchsize = 32;
    return {
      name,
      path,
      imagewidth,
      imageheight,
      channels,
      numLabels,
      batchsize,
    };
  };

  prepareLoadDatasetCSVGeneral = (): Dictionary => {
    let name = "Load dataset";
    let path = "C://Users//Luke Yeo//Downloads//bird.csv";
    let labelIndex = -1;
    let numLabels = 6;
    let numSkipLines = 0;
    let fractionTrain = 0.8;
    // let delimeterInString = ",";
    return { name, path, labelIndex, numLabels, numSkipLines, fractionTrain};
  };

  prepareLoadDatasetCSV = (): Dictionary => {
    let name = "Load dataset";
    let path = "C://Users//User//.deeplearning4j//data//humanactivity";
    let batchsize = 64;
    let numSkipLines = 0;
    let numClassLabels = 6;
    // let delimeterInString = ",";
    return { name, path, batchsize, numClassLabels, numSkipLines };
  };

  /**
   * CNNConfiguration node data
   * 1. seed - Random seed
   * 2. learningrate - Learning rate of network/model (Default = 0.001)
   * 3. optimizationalgorithm - Optimization algorithm used by network (Default = STOCHASTIC_GRADIENT_DESCENT)
   */
  prepareCNNConfiguration = (): Dictionary => {
    let name = "Configuration";
    let seed = Math.floor(Math.random() * 1000);
    let learningrate = 0.001;
    let optimizationalgorithm =
      OptimizationAlgorithmTypes.STOCHASTIC_GRADIENT_DESCENT;
    let convolutionMode = ConvolutionModeTypes.Truncate;
    let activationfunction = ActivationFunctionTypes.RELU;
    let weightInit = WeightInitTypes.XAVIER;
    let gradientNormalization =
      GradientNormalizationTypes.RenormalizeL2PerLayer;
    let l2 = 0;
    return {
      name,
      seed,
      learningrate,
      optimizationalgorithm,
      convolutionMode,
      activationfunction,
      weightInit,
      gradientNormalization,
      l2,
    };
  };

  /**
   * ConvolutionLayer node data
   * 1. kernalx/kernaly - Dimension (size) of kernal used by convolution layer (Default = 2)
   * 2. stridex/stridey - Number of pixels (dimension) shifts over the input matrix (Default = 1)
   * 3. nIn - Number of input (In convo layer, nIn = color channels of dataset) (Default = 3)
   * 4. nOut - Number of output (Default = 10)
   * 5. activationfunction - Activation function used by layer (Default = RELU)
   */
  prepareConvolutionLayer = (): Dictionary => {
    let name = "Convolutional Layer";
    // let ifEdit = true;
    let kernalx = 2,
      kernaly = 2;
    let stridex = 1,
      stridey = 1;
    let paddingx = 0,
      paddingy = 0;
    let nInMultiLayer = "Auto-filled";
    let nOut = 10;
    let activationfunction = ActivationFunctionTypes.RELU;
    let dropOut = 0,
      biasInit = 0;
    let convolutionMode = ConvolutionModeTypes.Truncate;
    return {
      name,
      kernalx,
      kernaly,
      stridex,
      stridey,
      paddingx,
      paddingy,
      nInMultiLayer,
      nOut,
      activationfunction,
      dropOut,
      biasInit,
      convolutionMode,
    };
  };

  /**
   * SubsamplingLayer node data
   * 1. kernalx/kernaly - Dimension (size) of kernal used by subsampling layer (Default = 2)
   * 2. stridex/stridey - Number of pixels (dimension) shifts over the input matrix (Default = 1)
   * 3. poolingtype - Pooling type used in current layer (Default = MAX)
   */
  prepareSubsamplingLayer = (): Dictionary => {
    let name = "Subsampling Layer";
    let kernalx = 2,
      kernaly = 2;
    let stridex = 1,
      stridey = 1;
    let paddingx = 0,
      paddingy = 0;
    let poolingtype = PoolingType.MAX;
    let convolutionMode = ConvolutionModeTypes.Truncate;
    return {
      name,
      kernalx,
      kernaly,
      stridex,
      stridey,
      paddingx,
      paddingy,
      poolingtype,
      convolutionMode,
    };
  };

  /**
   * DenseLayer node data
   * 1. nIn - Input size/ Output size of previous layer (Default = 0)
   * 2. nOut - Output size/ Number of node in dense layer (Default = 5)
   * 3. activationfunction - Activation function used by layer (Default = RELU)
   */
  prepareDenseLayer = (): Dictionary => {
    let name = "Dense Layer";
    let nOut = 5;
    let activationfunction = ActivationFunctionTypes.RELU;
    let dropOut = 0,
      biasInit = 0;
    let weightInit = WeightInitTypes.XAVIER;
    return { name, nOut, activationfunction, dropOut, biasInit, weightInit };
  };

  /**
   * OutputLayer node data
   * 1. nIn - Input size/ Output size of previous layer (Default = 10)
   * 2. nOut - Number of output/classes (Default = 2)
   * 3. activationfunction - Activation function used by layer (Default = RELU)
   * 4. lossfunction - Loss function used by output layer (Default = NEGATIVELOGLIKELIHOOD)
   */
  prepareOutputLayer = (): Dictionary => {
    let name = "Output Layer";
    let nOut = 2;
    let activationfunction = ActivationFunctionTypes.RELU;
    let lossfunction = LossFunctionTypes.NEGATIVELOGLIKELIHOOD;
    let weightInit = WeightInitTypes.XAVIER;
    return { name, nOut, activationfunction, lossfunction, weightInit };
  };

  //========================================================================================
  prepareLocalResponseNormalizationLayer = (): Dictionary => {
    let name = "Local Response Normalization Layer";
    return { name };
  };

  /**
   * SetInputType node data
   * 1. imagewidth - Dataset's (input) image width (Default = 50)
   * 2. imageheight - Dataset's (input) image height (Default = 50)
   * 3. channels - Dataset's (input) image color channel (Default = 3)
   */
  prepareSetInputType = (): Dictionary => {
    let name = "Input type";
    let imagewidth = 50;
    let imageheight = 50;
    let channels = 3;
    return { name, imagewidth, imageheight, channels };
  };

  /**
   * TrainCNN node data
   * 1. epochs - Number of epochs for training (Default = 200)
   * 2. scorelistener - Interval to calculate/evaluate the score of network (Default = 10)
   */
  prepareTrainNN = (): Dictionary => {
    let name = "Train NN";
    let epochs = 200;
    let scoreListener = 10;
    return { name, epochs, scoreListener };
  };

/**
 * TrainCNN node data
 * 1. epochs - Number of epochs for training (Default = 200)
 * 2. scorelistener - Interval to calculate/evaluate the score of network (Default = 10)
 */
    prepareTrainNNNoUi = (): Dictionary => {
    let name = "TrainNNNoUi";
    let epochs = 200;
    let scoreListener = 10;
    return { name, epochs, scoreListener };
  };

  /**
   * ExportCNN node data
   * 1. path - Directory to save the model
   * 2. filename - Filename of the model (saved as .zip)
   */
  prepareExportNN = (): Dictionary => {
    let name = "Export NN";
    let path = "";
    let filename = "";
    return { name, path, filename };
  };

  /**
   * Transfer Learning node for Image classification
   */
  prepareConfigTransferLearning_IClassification = (): Dictionary => {
    let name = "Config Transfer Learning IClassification";
    let learningrate = 0.0001;
    return { name, learningrate };
  };

}



export default new CNNNodeService();