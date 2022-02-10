package com.dl4jra.server.cnn;

import com.dl4jra.server.cnn.layerbuilder.*;
import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer.PoolingType;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.transferlearning.FineTuneConfiguration;
import org.deeplearning4j.nn.transferlearning.TransferLearning;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.nd4j.linalg.schedule.ScheduleType;
import org.nd4j.linalg.schedule.StepSchedule;

public class CNNConfiguration {
	private NeuralNetConfiguration.ListBuilder ListBuilder;
	private ComputationGraphConfiguration.GraphBuilder GraphBuilder;
	private FineTuneConfiguration FineTuneBuilder;
	private ComputationGraph computationGraph;
	private TransferLearning.GraphBuilder tranferlearningBuilder;

	CNNConfiguration(){
		this.ListBuilder = null;
		this.GraphBuilder = null;
		this.FineTuneBuilder = null;
		this.computationGraph = null;
	}

	public ComputationGraphConfiguration.GraphBuilder getGraphBuilder() {
		return GraphBuilder;
	}

	public NeuralNetConfiguration.ListBuilder getListBuilder() {
		return ListBuilder;
	}

	public FineTuneConfiguration getFineTuneBuilder() {
		return FineTuneBuilder;
	}

	public ComputationGraph getComputationGraph() {
		return computationGraph;
	}

	/**
	 * Initialize configuration
	 * @param seed - Random seed
	 * @param learningrate - Learning rate of network
	 * @param optimizationalgorithm - Optimization algorithm
	 */
	public void InitializeListBuilder(int seed, double learningrate, OptimizationAlgorithm optimizationalgorithm,
									  ConvolutionMode convolutionMode, Activation activation, WeightInit weightInit,
									  GradientNormalization gradientNormalization) {
		NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
		builder.seed(seed);
		if(optimizationalgorithm != null) {
			builder.optimizationAlgo(optimizationalgorithm);
		}
		if(weightInit != null) {
			builder.weightInit(weightInit);
		}
		if(activation != null) {
			builder.activation(activation);
		}
		builder.updater(new Adam(learningrate));
		if(convolutionMode != null) {
			builder.convolutionMode(convolutionMode);
		}
		builder.gradientNormalization(gradientNormalization);
		builder.l2(5 * 1e-4);
		this.ListBuilder = builder.list();
	}

	public void InitializeGraphBuilder(int seed, double learningrate, OptimizationAlgorithm optimizationalgorithm,
									   WeightInit weightInit){
		NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
		builder.trainingWorkspaceMode(WorkspaceMode.NONE);
		builder.inferenceWorkspaceMode(WorkspaceMode.NONE);
		builder.seed(seed);
		if(weightInit != null) {
			builder.weightInit(weightInit);
		}
		if(optimizationalgorithm != null) {
			builder.optimizationAlgo(optimizationalgorithm);
		}
		builder.updater(new Adam(learningrate));
		this.GraphBuilder = builder.graphBuilder();
	}
	
	/**
	 * Append convolution layer 
	 * @param ordering - Ordering of layer
	 * @param nIn - Dataset channels 
	 * @param nOut - Number of outputs
	 * @param kernalx - Kernal's width
	 * @param kernaly - Kernal's height
	 * @param stridex - Stride's width
	 * @param stridey - Stride's height
	 * @param activationfunction - Layer's activation function
	 * @throws Exception
	 */
	public void AppendConvolutionLayer(int ordering, int nIn, int nOut, int kernalx, int kernaly, int stridex, int stridey,
									   int paddingx, int paddingy, Activation activationfunction, double dropOut, double biasInit,
									   ConvolutionMode convolutionMode) throws Exception {
		if (! isNetworkConfigured_List())
			throw new Exception("Neural network is not configured yet");
		ConvolutionLayer convoLayer = ConvoLayerBuilder.GenerateLayer(nIn, nOut, kernalx, kernaly, stridex, stridey,paddingx, paddingy,
				activationfunction, dropOut, biasInit, convolutionMode);
		this.ListBuilder.layer(ordering, convoLayer);
	}

	public void AppendConvolutionLayer(int ordering, int nOut, int kernalx, int kernaly, int stridex, int stridey,
									   int paddingx, int paddingy, Activation activationfunction, double dropOut, double biasInit,
									   ConvolutionMode convolutionMode) throws Exception {
		if (! isNetworkConfigured_List())
			throw new Exception("Neural network is not configured yet");
		ConvolutionLayer convoLayer = ConvoLayerBuilder.GenerateLayer(nOut, kernalx, kernaly, stridex, stridey,paddingx, paddingy,
				activationfunction, dropOut, biasInit, convolutionMode);
		this.ListBuilder.layer(ordering, convoLayer);
	}
	
	/**
	 * Append subsampling layer
	 * @param ordering - Ordering of layer
	 * @param kernalx - Kernal's width
	 * @param kernaly - Kernal's height
	 * @param stridex - Stride's width
	 * @param stridey - Stride's height
	 * @param poolingType - Pooling type (min/max/average)
	 * @throws Exception
	 */
	public void AppendSubsamplingLayer (int ordering, int kernalx, int kernaly, int stridex, int stridey, int paddingx, int paddingy,
										PoolingType poolingType, ConvolutionMode convolutionMode) throws Exception{
		if (! isNetworkConfigured_List())
			throw new Exception("Neural network is not configured yet");
		SubsamplingLayer poolingLayer = PoolingLayerBuilder.GenerateLayer(kernalx, kernaly, stridex, stridey, paddingx, paddingy,
				poolingType, convolutionMode);
		this.ListBuilder.layer(ordering, poolingLayer);
	}
	
	/**
	 * Append dense layer
	 * @param ordering - Ordering of layer
	 * @param nOut - Number of node
	 * @param activationfunction - Layer's activation function
	 * @throws Exception
	 */
	public void AppendDenseLayer (int ordering,int nOut, Activation activationfunction, double dropOut, double biasInit,
								  WeightInit weightInit) throws Exception{
		if (! isNetworkConfigured_List())
			throw new Exception("Neural network is not configured yet");
		DenseLayer denseLayer = DenseLayerBuilder.GenerateLayer(nOut, activationfunction, dropOut, biasInit, weightInit);
		this.ListBuilder.layer(ordering, denseLayer);
	}
	
	/**
	 * Append output layer
	 * @param ordering - Ordering of layer
	 * @param nOut - Number of classes
	 * @param activationfunction - Layer's activation function
	 * @param lossfunction - Layer's loss function
	 * @throws Exception
	 */
	public void AppendOutputLayer (int ordering, int nOut, Activation activationfunction, LossFunction lossfunction, WeightInit weightInit) throws Exception{
		if (! isNetworkConfigured_List())
			throw new Exception("Neural network is not configured yet");
		OutputLayer outputLayer = OutputLayerBuilder.GenerateLayer(nOut, activationfunction, lossfunction, weightInit);
		this.ListBuilder.layer(ordering, outputLayer);
	}

//=============================================================================================
	public void AppendLocalResponseNormalizationLayer(int ordering) throws Exception{
		if (! isNetworkConfigured_List())
			throw new Exception("Neural network is not configured yet");
		LocalResponseNormalization localResponseNormalizationLayer = LocalResponseNormalizationLayerBuilder.GenerateLayer();
		this.ListBuilder.layer(ordering, localResponseNormalizationLayer);
	}

	public void AppendLSTMLayer(String name, int nIn, int nOut, Activation activationfunction, String layerInput) throws Exception {
		if (!isNetworkConfigured_Graph())
			throw new Exception("Neural network is not configured yet");
		LSTM lstm = LSTMLayerBuilder.GenerateLayer(nIn, nOut, activationfunction);
		this.GraphBuilder.layer(name, lstm, layerInput);
	}

	public void AppendRnnOutputLayer(String name, RNNFormat rnnFormat, int nIn, int nOut, LossFunction lossFunction ,
									 Activation activationfunction, String layerInput) throws Exception {
		if (!isNetworkConfigured_Graph())
			throw new Exception("Neural network is not configured yet");
		RnnOutputLayer rnnOutputLayer = RnnOutputLayerBuilder.GenerateLayer(rnnFormat, nIn, nOut, lossFunction, activationfunction);
		this.GraphBuilder.layer(name, rnnOutputLayer, layerInput);
	}

	public void AddInput(String inputName){
		this.GraphBuilder.addInputs(inputName);
	}

	public void SetOutput(String outputName){
		this.GraphBuilder.setOutputs(outputName);
	}

	public void AppendConvolutionLayer_CG(String name, int kernalSize, int nIn, int nOut, Activation activationfunction, String layerInput) throws Exception {
		if (!isNetworkConfigured_Graph())
			throw new Exception("Neural network is not configured yet");
//		ConvolutionLayer convolutionLayer = ConvoLayerBuilder.GenerateLayer_CG(kernalSize, nIn, nOut, activationfunction);
		Convolution1DLayer convolution1DLayer = (Convolution1DLayer) ConvoLayerBuilder.GenerateLayer_CG(kernalSize, nIn, nOut, activationfunction);
		this.GraphBuilder.layer(name, convolution1DLayer, layerInput);
	}

	
	/**
	 * Set input type
	 * @param imagewidth - Width of image dataset
	 * @param imageheight - Height of image dataset
	 * @param channels - (Color) channel of image dataset
	 * @throws Exception
	 */
	public void SetInputType(int imagewidth, int imageheight, int channels) throws Exception {
		if (! isNetworkConfigured_List())
			throw new Exception("Neural network is not configured yet");
		this.ListBuilder.setInputType(InputType.convolutional(imageheight, imagewidth, channels));
	}

	/**
	 * Build MultiLayerConfiguration
	 * @return MultiLayerConfiguration object
	 */
	public MultiLayerConfiguration build(){
		return this.ListBuilder.build();
	}

	public ComputationGraphConfiguration build_Graph(){
		return this.GraphBuilder.build();
	}

	/**
	 * @return boolean - True if network is configured, else False
	 */
	private boolean isNetworkConfigured_List() { return this.ListBuilder != null; }

	private boolean isNetworkConfigured_Graph() { return this.GraphBuilder != null; }



	// SEGMENTATION

	public void configureFineTune(int seed){
		FineTuneConfiguration.Builder builder = new FineTuneConfiguration.Builder();
		builder.trainingWorkspaceMode(WorkspaceMode.ENABLED);
		builder.updater(new Adam(new StepSchedule(ScheduleType.EPOCH, 3e-4, 0.5, 5)));
		builder.seed(seed);
		this.FineTuneBuilder = builder.build();
	}

	public void configureTransferLearning(ComputationGraph network, String featurizeExtractionLayer,
										  String vertexName, String nInName, int nIn, WeightInit nInWeightInit,
										  String nOutName, int nOut, WeightInit nOutWeightInit){
		tranferlearningBuilder = new TransferLearning.GraphBuilder(network);
		tranferlearningBuilder.fineTuneConfiguration(FineTuneBuilder);
		tranferlearningBuilder.setFeatureExtractor(featurizeExtractionLayer);
		tranferlearningBuilder.removeVertexAndConnections(vertexName);
		tranferlearningBuilder.nInReplace(nInName, nIn, nInWeightInit);
		tranferlearningBuilder.nOutReplace(nOutName, nOut, nOutWeightInit);
	}

	public void addCnnLossLayer(String layerName, LossFunction lossFunction, Activation activation, String layerInput ){
		CnnLossLayer cnnLossLayer = CnnLossLayerBuilder.GenerateLayer(lossFunction, activation);
		tranferlearningBuilder.addLayer(layerName, cnnLossLayer, layerInput);
	}

	public void setOutput(String outputName){
		tranferlearningBuilder.setOutputs(outputName);
	}

	public ComputationGraph build_TransferLearning(){
		this.computationGraph = tranferlearningBuilder.build();
		computationGraph.summary();

		// Set listeners
		ScoreIterationListener scoreIterationListener = new ScoreIterationListener(1);
		computationGraph.setListeners(scoreIterationListener);
		System.out.println(computationGraph.summary());
		return this.computationGraph;
	}

}
