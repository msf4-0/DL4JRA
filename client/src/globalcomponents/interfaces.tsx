
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
}

/**
 * Loss function types
 * More on "org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction"
*/
export enum LossFunctionTypes {
    MSE = "MSE",
    NEGATIVELOGLIKELIHOOD = "NEGATIVELOGLIKELIHOOD",
    POISSON = "POISSON",
}

/**
 * Pooling type for subsampling layer (min/max/average)
*/
export enum PoolingType {
    MIN = "MIN",
    MAX = "MAX",
    AVERAGE = "AVERAGE",
}