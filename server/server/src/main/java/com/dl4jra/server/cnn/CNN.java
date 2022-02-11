package com.dl4jra.server.cnn;

import java.io.File;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.RNNFormat;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer.PoolingType;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.InvocationType;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.dl4jra.server.cnn.response.UpdateResponse;
import com.dl4jra.server.globalresponse.Messageresponse;

public class CNN {
	// Neural network properties
	private CNNConfiguration cnnconfig;
	private MultiLayerNetwork multiLayerNetwork;
	private ComputationGraph computationGraph;
	private boolean networkconstructed;

	// Training dataset properties
	private DataSetIterator TrainingDatasetIterator;
	private CNNDatasetGenerator TrainingDatasetGenerator;
	
	// Validation dataset properties
	private CNNDatasetGenerator ValidationDatasetGenerator;
	private DataSetIterator ValidationDatasetIterator;


	// Constructor
	public CNN() {
		this.multiLayerNetwork = null;
		this.computationGraph = null;
		this.networkconstructed = false;
		this.cnnconfig = new CNNConfiguration();

		this.TrainingDatasetGenerator = new CNNDatasetGenerator();
		this.TrainingDatasetIterator = null;
		
		this.ValidationDatasetGenerator = new CNNDatasetGenerator();
		this.ValidationDatasetIterator = null;
	}
	
	/**
	 * Load training dataset
	 * @param path - Path to training dataset
	 * @param imagewidth - Width of image dataset 
	 * @param imageheight - Height of image dataset
	 * @param channels - Channels of image dataset
	 * @param numLabels - Number of labels
	 * @param batchsize - Iterator batch size
	 * @throws Exception
	 */
	public void LoadTrainingDataset(String path, int imagewidth, int imageheight, int channels, int numLabels, int batchsize) throws Exception{
		this.TrainingDatasetGenerator.LoadData(path, imagewidth, imageheight, channels, numLabels, batchsize, true);
	}

	public void LoadDatasetAutoSplit(String path, int imagewidth, int imageheight, int channels, int numLabels, int batchsize) throws Exception{
		this.TrainingDatasetGenerator.LoadDataAutoSplit(path, imagewidth, imageheight, channels, numLabels, batchsize, true);
	}

	/**
	 * Flip training dataset 
	 * @param flipmode - Flip image (x-axis/y-axis/both axis)
	 * @throws Exception
	 */
	public void FlipTrainingDataset(int flipmode) throws Exception {
		this.TrainingDatasetGenerator.FlipImage(flipmode);
	}
	
	/**
	 * Rotate training dataset
	 * @param angle - Angle of rotation
	 * @throws Exception
	 */
	public void RotateTrainingDataset(float angle) throws Exception{
		this.TrainingDatasetGenerator.RotateImage(angle);
	}
	
	/**
	 * Resize training dataset
	 * @param width - Width of image after resize
	 * @param height - Height of image after resize
	 * @throws Exception
	 */
	public void ResizeTrainingDataset(int width, int height) throws Exception {
		this.TrainingDatasetGenerator.ResizeImage(width, height);
	}
	
	/**
	 * Generate training dataset iterator
	 * @throws Exception
	 */
	public void GenerateTrainingDatasetIterator() throws Exception{
		this.TrainingDatasetIterator = this.TrainingDatasetGenerator.GetDatasetIterator();
	}

	public void GenerateDatasetAutoSplitIterator() throws Exception{
		this.TrainingDatasetIterator = this.TrainingDatasetGenerator.trainIterator();
		this.ValidationDatasetIterator = this.TrainingDatasetGenerator.testIterator();
	}

	/**
	 * Load validation dataset
	 * @param path - Path to validation dataset
	 * @param imagewidth - Width of image dataset 
	 * @param imageheight - Height of image dataset
	 * @param channels - Channels of image dataset
	 * @param numLabels - Number of labels
	 * @param batchsize - Iterator batch size
	 * @throws Exception
	 */
	public void LoadValidationDataset(String path, int imagewidth, int imageheight, int channels, int numLabels, int batchsize) throws Exception {
		this.ValidationDatasetGenerator.LoadData(path, imagewidth, imageheight, channels, numLabels, batchsize, true);
	}
	
	/**
	 * Flip validation dataset
	 * @param flipmode - Flip image (x-axis/y-axis/both axis)
	 * @throws Exception
	 */
	public void FlipValidationDataset(int flipmode) throws Exception {
		this.ValidationDatasetGenerator.FlipImage(flipmode);
	}
	
	/**
	 * Rotate validation dataset
	 * @param angle - Angle of rotation
	 * @throws Exception
	 */
	public void RotateValidationDataset(float angle) throws Exception {
		this.ValidationDatasetGenerator.RotateImage(angle);
	}
	
	/**
	 * Resize validation dataset
	 * @param width - Width of image after resize
	 * @param height - Height of image after resize
	 * @throws Exception
	 */
	public void ResizeValidationDataset(int width, int height) throws Exception {
		this.ValidationDatasetGenerator.ResizeImage(width, height);
	}
	
	/**
	 * Generate validation dataset iterator
	 * @throws Exception
	 */
	public void GenerateValidationDatasetIterator() throws Exception {
		this.ValidationDatasetIterator = this.ValidationDatasetGenerator.GetDatasetIterator();
	}

	public void LoadTrainingDatasetCSV(String path, int numSkipLines, int numClassLabels, int batchsize, char delimeter) throws Exception{
		this.TrainingDatasetGenerator.LoadTrainDataCSV(path, numSkipLines, numClassLabels, batchsize, delimeter);
	}

	public void LoadTestingDatasetCSV(String path, int numSkipLines, int numClassLabels, int batchsize, char delimeter) throws Exception{
		this.ValidationDatasetGenerator.LoadTestDataCSV(path, numSkipLines, numClassLabels, batchsize, delimeter);
	}


	public void GenerateTrainingDatasetIteratorCSV() throws Exception {
		this.TrainingDatasetIterator = this.TrainingDatasetGenerator.trainDataSetIteratorCSV();
	}

	public void GenerateValidatingDatasetIteratorCSV() throws Exception{
		this.ValidationDatasetIterator = this.ValidationDatasetGenerator.testDataSetIteratorCSV();
	}
	
	/**
	 * Initialize configuration
	 * @param seed - Random seed
	 * @param learningrate - Learning rate of network
	 * @param optimizationalgorithm - Optimization algorithm
	 */
	public void InitializeConfigurations(int seed, double learningrate, OptimizationAlgorithm optimizationalgorithm,
										 ConvolutionMode convolutionMode, Activation activation, WeightInit weightInit,
										 GradientNormalization gradientNormalization) {
		this.cnnconfig.InitializeListBuilder(seed, learningrate, optimizationalgorithm, convolutionMode, activation, weightInit,
				gradientNormalization);
	}

	public void InitializeConfigurationsGraphBuilder(int seed, double learningrate, OptimizationAlgorithm optimizationalgorithm,
													 WeightInit weightInit) {
		this.cnnconfig.InitializeGraphBuilder(seed, learningrate, optimizationalgorithm, weightInit);
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
									   ConvolutionMode convolutionMode) throws Exception{
		int numChannel = 3;
		int firstLayer = 0;
		boolean boo = (nIn == numChannel && ordering != firstLayer);
		if(boo){
			AppendConvolutionLayer(ordering, nOut, kernalx, kernaly, stridex, stridey, paddingx, paddingy,
					activationfunction, dropOut, biasInit, convolutionMode);
		}
		else {
			this.cnnconfig.AppendConvolutionLayer(ordering, nIn, nOut, kernalx, kernaly, stridex, stridey, paddingx, paddingy,
					activationfunction, dropOut, biasInit, convolutionMode);
		}
	}


	public void AppendConvolutionLayer(int ordering, int nOut, int kernalx, int kernaly, int stridex, int stridey,
									   int paddingx, int paddingy, Activation activationfunction, double dropOut, double biasInit,
									   ConvolutionMode convolutionMode) throws Exception{
		this.cnnconfig.AppendConvolutionLayer(ordering, nOut, kernalx, kernaly, stridex, stridey, paddingx, paddingy,
				activationfunction, dropOut, biasInit, convolutionMode);
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
	public void AppendSubsamplingLayer (int ordering, int kernalx, int kernaly, int stridex, int stridey,
										int paddingx, int paddingy, PoolingType poolingType,
										ConvolutionMode convolutionMode) throws Exception {
		this.cnnconfig.AppendSubsamplingLayer(ordering, kernalx, kernaly, stridex, stridey, paddingx, paddingy, poolingType, convolutionMode);
	}
	
	/**
	 * Append dense layer
	 * @param ordering - Ordering of layer
	 * @param nOut - Number of node
	 * @param activationfunction - Layer's activation function
	 * @param dropOut
	 * @throws Exception
	 */
	public void AppendDenseLayer(int ordering, int nOut, Activation activationfunction, double dropOut, double biasInit,
								 WeightInit weightInit) throws Exception {
		this.cnnconfig.AppendDenseLayer(ordering, nOut, activationfunction, dropOut, biasInit, weightInit);
	}
	
	/**
	 * Append output layer
	 * @param ordering - Ordering of layer
	 * @param nOut - Number of classes
	 * @param activationfunction - Layer's activation function
	 * @param lossfunction - Layer's loss function
	 * @throws Exception
	 */
	public void AppendOutputLayer (int ordering, int nOut, Activation activationfunction, LossFunction lossfunction,
								   WeightInit weightInit) throws Exception {
		this.cnnconfig.AppendOutputLayer(ordering, nOut, activationfunction, lossfunction, weightInit);
	}

//	=============================================================================================================================
	public void AppendLocalResponseNormalizationLayer (int ordering) throws Exception {
		this.cnnconfig.AppendLocalResponseNormalizationLayer(ordering);
	}

	public void AppendLSTMLayer (String name, int nOut, Activation activationfunction, String layerInput) throws Exception {
		int nIn = this.TrainingDatasetIterator.inputColumns();
		this.cnnconfig.AppendLSTMLayer(name, nIn, nOut, activationfunction, layerInput);
	}

	public void AppendRnnOutputLayer(String name, RNNFormat rnnFormat, int nIn, int nOut, LossFunction lossFunction,
									 Activation activationfunction, String layerInput) throws Exception {
		this.cnnconfig.AppendRnnOutputLayer(name, rnnFormat, nIn, nOut, lossFunction, activationfunction, layerInput);
	}

	public void AddInput(String inputName){
		this.cnnconfig.AddInput(inputName);
	}

	public void SetOutput(String outputName){
		this.cnnconfig.SetOutput(outputName);
	}




	/**
	 * Set input type
	 * @param imagewidth - Width of image dataset
	 * @param imageheight - Height of image dataset
	 * @param channels - (Color) channel of image dataset
	 * @throws Exception
	 */
	public void SetInputType(int imagewidth, int imageheight, int channels) throws Exception{
		this.cnnconfig.SetInputType(imagewidth, imageheight, channels);
	}
	
	/**
	 * Construct network
	 * @throws Exception
	 */
	public void ConstructNetwork() throws Exception{
		this.multiLayerNetwork = new MultiLayerNetwork(this.cnnconfig.build());
		this.multiLayerNetwork.init();
		this.networkconstructed = true;
		this.multiLayerNetwork.setListeners(
				new ScoreIterationListener(5),
				new EvaluativeListener(TrainingDatasetIterator, 1, InvocationType.EPOCH_END),
				new EvaluativeListener(ValidationDatasetIterator, 1, InvocationType.EPOCH_END)
		);
	}

	public void ConstructNetworkRNN(){
		this.computationGraph = new ComputationGraph(this.cnnconfig.build_Graph());
		this.computationGraph.init();
		this.networkconstructed = true;
		this.computationGraph.setListeners(
				new ScoreIterationListener(5),
				new EvaluativeListener(TrainingDatasetIterator, 1, InvocationType.EPOCH_END),
				new EvaluativeListener(ValidationDatasetIterator, 1, InvocationType.EPOCH_END)
		);
	}

	public void EvaluateModelRNN(){
		System.out.println("***** Test Evaluation *****");
		Evaluation eval = new Evaluation(6);
		ValidationDatasetIterator.reset();
		DataSet testDataSet = ValidationDatasetIterator.next(1);
		INDArray s = testDataSet.getFeatures();
		System.out.println(s);
		while(ValidationDatasetIterator.hasNext())
		{
			testDataSet = ValidationDatasetIterator.next();
			INDArray[] predicted = this.computationGraph.output(testDataSet.getFeatures());
			INDArray labels = testDataSet.getLabels();

			eval.evalTimeSeries(labels, predicted[0], testDataSet.getLabelsMaskArray());
		}
		System.out.println(eval.stats());
	}


	/**
	 * Network training
	 * @param epochs - Number of epoch for network training
	 * @param scoreListener - Get the score of network (classifier) after (scoreListener)
	 * @throws Exception
	 */
	public void TrainNetwork (int epochs, int scoreListener) throws Exception {
		if (this.TrainingDatasetIterator == null)
			throw new Exception("There is no training dataset");
		if (! this.networkconstructed)
			throw new Exception("Neural network is not constructed");
		for (int counter = 0; counter < epochs; counter ++) {
			if(this.multiLayerNetwork != null) {
				this.multiLayerNetwork.fit(this.TrainingDatasetIterator);
				if (epochs % scoreListener == 0)
					System.out.println("Score in epoch " + counter + " : " + this.multiLayerNetwork.score());
			}
			if(this.computationGraph != null){
				this.computationGraph.fit(this.TrainingDatasetIterator);
				if (epochs % scoreListener == 0)
					System.out.println("Score in epoch " + counter + " : " + this.computationGraph.score());
			}
			this.TrainingDatasetIterator.reset();
			System.out.println("DONE2");
		}
	}
	
	/**
	 * Training (Respond using SseEmitter)
	 * @param epochs - Number of epoch for network training
	 * @param scoreListener - Get the score of network (classifier) after (scoreListener)
	 * @param sseEmitter
	 * @throws Exception
	 */
	public void TrainNetwork (int epochs, int scoreListener, SseEmitter sseEmitter) throws Exception {
		Object message;
		if (this.TrainingDatasetIterator == null) {
			message = "Training dataset not set";
			sseEmitter.send(SseEmitter.event().name("TrainingErrorEvent").data(message));
			throw new Exception("Training dataset not set");
		}
		if (! this.networkconstructed) {
			message = "Neural network is not constructed";
			sseEmitter.send(SseEmitter.event().name("TrainingErrorEvent").data(message));
			throw new Exception("Neural network is not constructed");
		}
		for (int counter = 0; counter < epochs; counter ++) {
			this.TrainingDatasetIterator.reset();
			this.multiLayerNetwork.fit(this.TrainingDatasetIterator);
			message = counter;
			sseEmitter.send(SseEmitter.event().name("TrainingEpochUpdateEvent").data(message));
			if (counter % scoreListener == 0) {
				message = "Score in epoch " + counter + " : " + String.format("%.2f", this.multiLayerNetwork.score());
				sseEmitter.send(SseEmitter.event().name("TrainingScoreEvent").data(message));
			}
		}
		message = "Network Training completed";
		sseEmitter.send(SseEmitter.event().name("TrainingCompleteEvent").data(message));
	}
	
	/**
	 * Training (Respond using Websocket)
	 * @param epochs - Number of epoch for network training
	 * @param scoreListener - Get the score of network (classifier) after (scoreListener)
	 * @param template
	 * @throws Exception
	 */
	public void TrainNetwork(int epochs, int scoreListener, SimpMessagingTemplate template) throws Exception {
		if (this.TrainingDatasetIterator == null) {
			throw new Exception("Training dataset not set");
		}
		if (! this.networkconstructed) {
			throw new Exception("Neural network is not constructed");
		}
		template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, epochs));
		for (int counter = 0; counter < epochs; counter ++) {
			this.TrainingDatasetIterator.reset();
			this.multiLayerNetwork.fit(this.TrainingDatasetIterator);
			template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(counter + 1, epochs));
			if (counter % scoreListener == 0) {
				String message = "Score in epoch " + counter + " : " + String.format("%.2f", this.multiLayerNetwork.score());
				template.convertAndSend("/response/cnn/message", new Messageresponse(message));
			}
		}
	}
	
	/**
	 * Evaluation & validation
	 * @throws Exception
	 */
	public void ValidateNetwork() throws Exception{
		Evaluation evaluation;
		if (this.ValidationDatasetIterator == null)
			throw new Exception("There is no validation dataset");
		if (! this.networkconstructed)
			throw new Exception("Neural network is not constructed");
		if(multiLayerNetwork != null) {
			evaluation = this.multiLayerNetwork.evaluate(this.ValidationDatasetIterator);
			System.out.println("Accuracy - " + evaluation.accuracy());
			System.out.println("Network stats - \n" + evaluation.stats());
		}
		if(computationGraph != null){
			evaluation = this.computationGraph.evaluate(this.ValidationDatasetIterator);
			System.out.println("Accuracy - " + evaluation.accuracy());
			System.out.println("Network stats - \n" + evaluation.stats());
		}


	}

	/**
	 * Evaluation & validation (Respond using SseEmitter)
	 * @param sseEmitter
	 * @throws Exception
	 */
	public void ValidateNetwork(SseEmitter sseEmitter) throws Exception{
		if (this.ValidationDatasetIterator == null) {
			Object message = "Validation dataset not set";
			sseEmitter.send(SseEmitter.event().name("ValidationErrorEvent").data(message));
			throw new Exception("Validation dataset not set");
		}
		if (! this.networkconstructed) {
			Object message = "Neural network is not constructed";
			sseEmitter.send(SseEmitter.event().name("ValidationErrorEvent").data(message));
			throw new Exception("Neural network is not constructed");
		}
		Evaluation evaluation = this.multiLayerNetwork.evaluate(this.ValidationDatasetIterator);
		Object accuracy = "Network accuracy : " + evaluation.accuracy();
		sseEmitter.send(SseEmitter.event().name("ValidationAccuracyEvent").data(accuracy));
		Object message = "Network Validation Completed";
		sseEmitter.send(SseEmitter.event().name("ValidationCompleteEvent").data(message));
	}
	
	/**
	 * Evaluation & validation (Respond using Websocket)
	 * @param template
	 * @throws Exception
	 */
	public void ValidateNetwork(SimpMessagingTemplate template) throws Exception {
		if (this.ValidationDatasetIterator == null)
			throw new Exception("There is no validation dataset");
		if (! this.networkconstructed)
			throw new Exception("Neural network is not constructed");
		Evaluation evaluation = this.multiLayerNetwork.evaluate(this.ValidationDatasetIterator);
		String message = "Network accuracy : " + evaluation.accuracy();
		template.convertAndSend("/response/cnn/message", new Messageresponse(message));
	}
	
	/**
	 * Save CNN model
	 * @param path - Directory to save model
	 * @param name - Filename of model 
	 * @throws Exception
	 */
	public void SaveModal(String path, String name) throws Exception {
		File directory = new File(path);
		if (!directory.exists() || ! directory.isDirectory())
			throw new Exception("Invalid path or not a directory");
		if (! this.networkconstructed )
			throw new Exception("Neural network is not constructed");
		this.multiLayerNetwork.save(new File(path + "/" + name + ".zip"), true);
	}
	
	/**
	 * Load CNN model
	 * @param path - Path to classifier 
	 * @throws Exception
	 */
	public void LoadModal(String path) throws Exception {
		File location = new File(path);
		this.multiLayerNetwork = MultiLayerNetwork.load(location, true);
		this.networkconstructed = true;
	}
	
}