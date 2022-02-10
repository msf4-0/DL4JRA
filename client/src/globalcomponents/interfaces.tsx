
export interface Dictionary  {
    [key: string] : any
}

/**
 * Flipmode in ResizeImageTransform (Java)
 * -1 = FLIP_BOTH_AXIS  
 * 0 = FLIP_X_AXIS
 * 1 = FLIP_Y_AXIS
*/
export enum Flipmode {
    FLIP_BOTH_AXIS = "-1",
    FLIP_X_AXIS = "0",
    FLIP_Y_AXIS = "1",
}

/**
 * Activation function types
 * More on "import org.nd4j.linalg.activations.Activation"
*/
export enum ActivationFunctionTypes {
    RELU = "RELU",
    SELU = "SELU",
    TANH = "TANH",
    SIGMOID = "SIGMOID",
    SOFTMAX = "SOFTMAX",
    null = "null",
}

/**
 * Convolution Mode types
*/
export enum ConvolutionModeTypes {
    Truncate = "Truncate",
    Same = "Same",
    Causal = "Causal",
    Strict = "Strict",
    null = "null",
}


/**
 * Weight Init types
*/
export enum WeightInitTypes {
    DISTRIBUTION = "DISTRIBUTION", 
    ZERO = "ZERO", 
    ONES = "ONES", 
    SIGMOID_UNIFORM = "SIGMOID_UNIFORM", 
    NORMAL = "NORMAL", 
    LECUN_NORMAL = "LECUN_NORMAL", 
    UNIFORM = "UNIFORM", 
    XAVIER = "XAVIER", 
    XAVIER_UNIFORM = "XAVIER_UNIFORM", 
    XAVIER_FAN_IN = "XAVIER_FAN_IN", 
    XAVIER_LEGACY = "XAVIER_LEGACY", 
    RELU = "RELU",
    RELU_UNIFORM = "RELU_UNIFORM", 
    IDENTITY = "IDENTITY", 
    LECUN_UNIFORM = "LECUN_UNIFORM", 
    VAR_SCALING_NORMAL_FAN_IN = "VAR_SCALING_NORMAL_FAN_IN", 
    VAR_SCALING_NORMAL_FAN_OUT = "VAR_SCALING_NORMAL_FAN_OUT", 
    VAR_SCALING_NORMAL_FAN_AVG = "VAR_SCALING_NORMAL_FAN_AVG",
    VAR_SCALING_UNIFORM_FAN_IN = "VAR_SCALING_UNIFORM_FAN_IN", 
    VAR_SCALING_UNIFORM_FAN_OUT = "VAR_SCALING_UNIFORM_FAN_OUT", 
    VAR_SCALING_UNIFORM_FAN_AVG = "VAR_SCALING_UNIFORM_FAN_AVG",
    null = "null",
}

/**
 * Weight Init types
*/
export enum GradientNormalizationTypes {
    None = "None", 
    RenormalizeL2PerLayer = "RenormalizeL2PerLayer", 
    RenormalizeL2PerParamType = "RenormalizeL2PerParamType", 
    ClipElementWiseAbsoluteValue = "ClipElementWiseAbsoluteValue", 
    ClipL2PerLayer = "ClipL2PerLayer", 
    ClipL2PerParamType = "ClipL2PerParamType",
    null = "null",
}

/**
 * Optimization algorithm types
 * More on "org.deeplearning4j.nn.api.OptimizationAlgorithm"
 */
export enum OptimizationAlgorithmTypes {
    CONJUGATE_GRADIENT = "CONJUGATE_GRADIENT",
    LBFGS = "LBFGS",
    LINE_GRADIENT_DESCENT = "LINE_GRADIENT_DESCENT",
    STOCHASTIC_GRADIENT_DESCENT = "STOCHASTIC_GRADIENT_DESCENT",
    null = 'null',
}

/**
 * Loss function types
 * More on "org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction"
*/
export enum LossFunctionTypes {
    MSE = "MSE",
    NEGATIVELOGLIKELIHOOD = "NEGATIVELOGLIKELIHOOD",
    POISSON = "POISSON",
    MCXENT = "MCXENT",
    XENT = "XENT",

}

/**
 * Pooling type for subsampling layer (min/max/average)
*/
export enum PoolingType {
    MIN = "MIN",
    MAX = "MAX",
    AVERAGE = "AVERAGE",
}


//===============================
export enum RNNFormatTypes {
    NCW = "NCW",
    NWC = "NWC",
}