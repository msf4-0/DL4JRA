package com.dl4jra.server.cnn;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.RNNFormat;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer.PoolingType;
import org.deeplearning4j.nn.conf.layers.objdetect.Yolo2OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.objdetect.DetectedObject;
import org.deeplearning4j.nn.layers.objdetect.YoloUtils;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.transferlearning.FineTuneConfiguration;
import org.deeplearning4j.nn.transferlearning.TransferLearning;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.InvocationType;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.TinyYOLO;
import org.deeplearning4j.zoo.model.UNet;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.dl4jra.server.cnn.response.UpdateResponse;
import com.dl4jra.server.globalresponse.Messageresponse;

import static org.bytedeco.opencv.global.opencv_core.CV_8U;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgproc.FONT_HERSHEY_DUPLEX;
import static org.bytedeco.opencv.helper.opencv_core.RGB;

public class CNN {
	// Neural network properties
	private CNNConfiguration cnnconfig;
	private MultiLayerNetwork multiLayerNetwork;
	private ComputationGraph computationGraph, pretrained;
	private boolean networkconstructed;

	// Training dataset properties
	private DataSetIterator TrainingDatasetIterator;
	private CNNDatasetGenerator TrainingDatasetGenerator;
	
	// Validation dataset properties
	private CNNDatasetGenerator ValidationDatasetGenerator;
	private DataSetIterator ValidationDatasetIterator;

	private RecordReaderDataSetIterator trainGenerator;
	private RecordReaderDataSetIterator validationGenerator;

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

	public RecordReaderDataSetIterator getTrainGenerator() {
		return trainGenerator;
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

//	public void LoadTrainingDatasetCSV(String path, int numSkipLines, int numClassLabels, int batchsize, char delimeter) throws Exception{
////		this.TrainingDatasetGenerator.LoadTrainDataCSV(path, numSkipLines, numClassLabels, batchsize, delimeter);
////	}
////
////	public void LoadTestingDatasetCSV(String path, int numSkipLines, int numClassLabels, int batchsize, char delimeter) throws Exception{
////		this.TrainingDatasetGenerator.LoadTestDataCSV(path, numSkipLines, numClassLabels, batchsize, delimeter);
////	}

	public void LoadTrainingDatasetCSV(String path, int numSkipLines, int numClassLabels, int batchsize) throws Exception{
		this.TrainingDatasetGenerator.LoadTrainDataCSV(path, numSkipLines, numClassLabels, batchsize);
	}

	public void LoadTestingDatasetCSV(String path, int numSkipLines, int numClassLabels, int batchsize) throws Exception{
		this.TrainingDatasetGenerator.LoadTestDataCSV(path, numSkipLines, numClassLabels, batchsize);
	}


	public void GenerateTrainingDatasetIteratorCSV() throws Exception {
		this.TrainingDatasetIterator = this.TrainingDatasetGenerator.trainDataSetIteratorCSV();
	}

	public void GenerateValidatingDatasetIteratorCSV() throws Exception{
		this.ValidationDatasetIterator = this.TrainingDatasetGenerator.testDataSetIteratorCSV();
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
		int firstLayer = 0;
		boolean boo = (ordering != firstLayer);
		if(boo){
			this.cnnconfig.AppendConvolutionLayer(ordering, nOut, kernalx, kernaly, stridex, stridey, paddingx, paddingy,
					activationfunction, dropOut, biasInit, convolutionMode);
		}
		else {
			this.cnnconfig.AppendConvolutionLayer(ordering, nIn, nOut, kernalx, kernaly, stridex, stridey, paddingx, paddingy,
					activationfunction, dropOut, biasInit, convolutionMode);
		}
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

	public void AppendLSTMLayer (String name,int nIn, int nOut, Activation activationfunction, String layerInput) throws Exception {
		this.cnnconfig.AppendLSTMLayer(name, nIn, nOut, activationfunction, layerInput);
	}

	public void AppendRnnOutputLayer(String name, RNNFormat rnnFormat, int nIn, int nOut, LossFunction lossFunction,
									 Activation activationfunction, String layerInput) throws Exception {
		this.cnnconfig.AppendRnnOutputLayer(name, rnnFormat, 100, nOut, lossFunction, activationfunction, layerInput);
	}

	public void AddInput(String inputName){
		this.cnnconfig.AddInput(inputName);
	}

	public void SetOutput(String outputName){
		this.cnnconfig.SetOutput(outputName);
	}

	public void AppendConvolutionLayer_CG(String name, int kernalSize, int nIn, int nOut, Activation activationfunction, String layerInput) throws Exception {
		this.cnnconfig.AppendConvolutionLayer_CG(name, kernalSize, nIn, nOut, activationfunction, layerInput);
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
				new EvaluativeListener(ValidationDatasetIterator, 1, InvocationType.EPOCH_END)
		);
	}

	public void ConstructNetworkRNN(){
		this.computationGraph = new ComputationGraph(this.cnnconfig.build_Graph());
		this.computationGraph.init();
		this.networkconstructed = true;
		this.computationGraph.setListeners(
				new ScoreIterationListener(5),
				new EvaluativeListener(ValidationDatasetIterator, 1, InvocationType.EPOCH_END)
		);
	}



	public void EvaluateModel_CG(){
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
//		System.out.println(eval.stats());
	}


	/**
	 * Network training
	 * @param epochs - Number of epoch for network training
	 * @param scoreListener - Get the score of network (classifier) after (scoreListener)
	 * @throws Exception
	 */
	public void TrainNetwork (int epochs, int scoreListener) throws Exception {
		try {
			if (this.TrainingDatasetIterator == null)
				throw new Exception("There is no training dataset");
			if (!this.networkconstructed)
				throw new Exception("Neural network is not constructed");
			for (int counter = 0; counter < epochs; counter++) {
				if (this.multiLayerNetwork != null) {
					this.multiLayerNetwork.fit(this.TrainingDatasetIterator);
					if (epochs % scoreListener == 0)
						System.out.println("Score in epoch " + counter + " : " + this.multiLayerNetwork.score());
				}
				if (this.computationGraph != null) {
					this.computationGraph.fit(this.TrainingDatasetIterator);
					if (epochs % scoreListener == 0)
						System.out.println("Score in epoch " + counter + " : " + this.computationGraph.score());
				}
				this.TrainingDatasetIterator.reset();
			}
		}
		catch (Exception e){
			System.out.println(e.getMessage());
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

		for (int counter = 0; counter < epochs; counter++) {
			if(this.multiLayerNetwork != null) {
				this.TrainingDatasetIterator.reset();
				this.multiLayerNetwork.fit(this.TrainingDatasetIterator);
				message = counter;
				sseEmitter.send(SseEmitter.event().name("TrainingEpochUpdateEvent").data(message));
				if (counter % scoreListener == 0) {
					message = "Score in epoch " + counter + " : " + String.format("%.2f", this.multiLayerNetwork.score());
					sseEmitter.send(SseEmitter.event().name("TrainingScoreEvent").data(message));
				}
			}
			if(this.computationGraph != null){
				this.TrainingDatasetIterator.reset();
				this.computationGraph.fit(this.TrainingDatasetIterator);
				message = counter;
				sseEmitter.send(SseEmitter.event().name("TrainingEpochUpdateEvent").data(message));
				if (counter % scoreListener == 0) {
					message = "Score in epoch " + counter + " : " + String.format("%.2f", this.computationGraph.score());
					sseEmitter.send(SseEmitter.event().name("TrainingScoreEvent").data(message));
				}
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
		for (int counter = 0; counter < epochs; counter++) {
			if(this.multiLayerNetwork != null) {
				this.TrainingDatasetIterator.reset();
				this.multiLayerNetwork.fit(this.TrainingDatasetIterator);
				template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(counter + 1, epochs));
				if (counter % scoreListener == 0) {
					String message = "Score in epoch " + counter + " : " + String.format("%.2f", this.multiLayerNetwork.score());
					template.convertAndSend("/response/cnn/message", new Messageresponse(message));
				}
			}

			if(this.computationGraph != null) {
				this.computationGraph.fit(this.TrainingDatasetIterator);
				this.TrainingDatasetIterator.reset();
				template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(counter + 1, epochs));
				if (counter % scoreListener == 0) {
					String message = "Score in epoch " + counter + " : " + String.format("%.2f", this.computationGraph.score());
					template.convertAndSend("/response/cnn/message", new Messageresponse(message));
				}
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
		if(multiLayerNetwork != null) {
			Evaluation evaluation = this.multiLayerNetwork.evaluate(this.ValidationDatasetIterator);
			Object accuracy = "Network accuracy : " + evaluation.accuracy();
			sseEmitter.send(SseEmitter.event().name("ValidationAccuracyEvent").data(accuracy));
			Object message = "Network Validation Completed";
			sseEmitter.send(SseEmitter.event().name("ValidationCompleteEvent").data(message));
		}

		if(computationGraph != null) {
			Evaluation evaluation = this.computationGraph.evaluate(this.ValidationDatasetIterator);
			Object accuracy = "Network accuracy : " + evaluation.accuracy();
			sseEmitter.send(SseEmitter.event().name("ValidationAccuracyEvent").data(accuracy));
			Object message = "Network Validation Completed";
			sseEmitter.send(SseEmitter.event().name("ValidationCompleteEvent").data(message));
		}
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
		if(multiLayerNetwork != null) {
			Evaluation evaluation = this.multiLayerNetwork.evaluate(this.ValidationDatasetIterator);
			String message = "Network accuracy : " + evaluation.accuracy();
			template.convertAndSend("/response/cnn/message", new Messageresponse(message));
		}
		if(computationGraph != null) {
			Evaluation evaluation = this.computationGraph.evaluate(this.ValidationDatasetIterator);
			String message = "Network accuracy : " + evaluation.accuracy();
			template.convertAndSend("/response/cnn/message", new Messageresponse(message));
		}
	}

	// SEGMENTATION

	ZooModel zooModel;
	ComputationGraph unet, constructedModel;

	public void importPretrainedModel() throws IOException {
		zooModel = UNet.builder().build();

		unet = (ComputationGraph) zooModel.initPretrained(PretrainedType.SEGMENT);
		System.out.println(unet.summary());
	}

	public void configureFineTune(int seed){
		this.cnnconfig.configureFineTune(seed);
	}

	public void configureTranferLearning( String featurizeExtractionLayer, String vertexName,
										  String nInName, int nIn, WeightInit nInWeightInit,
										  String nOutName, int nOut, WeightInit nOutWeightInit){
		this.cnnconfig.configureTransferLearning(unet, featurizeExtractionLayer, vertexName, nInName, nIn, nInWeightInit,
				nOutName, nOut, nOutWeightInit);
	}

	public void addCnnLossLayer(String layerName, LossFunction lossFunction, Activation activation, String layerInput ){
		this.cnnconfig.addCnnLossLayer(layerName, lossFunction, activation, layerInput);
	}

	public void setOutput(String outputName){
		this.cnnconfig.setOutput(outputName);
	}

	public void build_TransferLearning(){
		constructedModel = this.cnnconfig.build_TransferLearning();
	}

	public void setIterator_segmentation(String path, int batchSize, double trainPerc, int imagewidth, int imageheight,
										 int channels, String maskFileName){
		this.TrainingDatasetGenerator.setIterator_segmentation(path, batchSize, trainPerc, imageheight, imagewidth, channels,
				maskFileName);
	}

	public void generateIterator() throws Exception{
		this.trainGenerator = this.TrainingDatasetGenerator.trainIterator_segmentation();
		this.validationGenerator = this.TrainingDatasetGenerator.testIterator_segmentation();
	}

	public void train_segmentation(int epoch) throws Exception {
		this.TrainingDatasetGenerator.train_segmentation(epoch, trainGenerator, constructedModel);
	}

	public void validation_segmentation() throws IOException {
		this.TrainingDatasetGenerator.validation_segmentation(validationGenerator, constructedModel);
	}



	// Object detection with pre trained model
	private int seed;
	private double[][] priorBoxes = {{1, 3}, {2.5, 6}, {3, 4}, {3.5, 8}, {4, 9}};
	private INDArray priors;
	public void importTinyYolo() throws IOException {
		seed = 123;
		Nd4j.getRandom().setSeed(seed);
		priors = Nd4j.create(priorBoxes);

		this.pretrained = (ComputationGraph) TinyYOLO.builder().build().initPretrained();;
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
		if (multiLayerNetwork != null)
			this.multiLayerNetwork.save(new File(path + "/" + name + ".zip"), true);
		else  if(computationGraph != null)
			this.computationGraph.save(new File(path + "/" + name + ".zip"), true);
	}

	/**
	 * Load CNN model
	 * @param path - Path to classifier
	 * @throws Exception
	 */
	public void LoadModal(String path) throws Exception {
		File file = new File(path);
		seed = 123;
		Nd4j.getRandom().setSeed(seed);
		priors = Nd4j.create(priorBoxes);

		this.pretrained = ComputationGraph.load(file, true);
	}

	public void configTransferLearningNetwork_ODetection(double learningRate){
		FineTuneConfiguration fineTuneConfiguration = new FineTuneConfiguration.Builder()
				.seed(seed)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
				.gradientNormalizationThreshold(1.0)
				.updater(new Adam.Builder().learningRate(learningRate).build())
				.l2(0.00001)
				.activation(Activation.IDENTITY)
				.trainingWorkspaceMode(WorkspaceMode.ENABLED)
				.inferenceWorkspaceMode(WorkspaceMode.ENABLED)
				.build();

		computationGraph = new TransferLearning.GraphBuilder(pretrained)
				.fineTuneConfiguration(fineTuneConfiguration)
				.removeVertexKeepConnections("conv2d_9")
				.removeVertexKeepConnections("outputs")
				.addLayer("conv2d_9",
						new ConvolutionLayer.Builder(1, 1 )
								.nIn(1024)
								.nOut(5 * (5 + trainGenerator.getLabels().size()))
								.stride(1, 1)
								.convolutionMode(ConvolutionMode.Same)
								.weightInit(WeightInit.XAVIER)
								.activation(Activation.IDENTITY)
								.build(),
						"leaky_re_lu_8")
				.addLayer("outputs",
						new Yolo2OutputLayer.Builder()
								.lambdaNoObj(0.5)
								.lambdaCoord(5.0)
								.boundingBoxPriors(priors.castTo(DataType.FLOAT))
								.build(),
						"conv2d_9")
				.setOutputs("outputs")
				.build();

	}

	public void evaluate_TINYYOLO(int epochs) throws Exception {

		computationGraph.setListeners(new ScoreIterationListener(1));
		for (int i = 1; i < epochs + 1; i++) {
			computationGraph.fit(trainGenerator);
			System.out.println("*** Completed epoch {" + i+ " } ***");
		}

		// Testing through visualization
//		NativeImageLoader imageLoader = new NativeImageLoader();
//		CanvasFrame canvas = new CanvasFrame("Validate Test Dataset");
//		OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
//		org.deeplearning4j.nn.layers.objdetect.Yolo2OutputLayer yout = (org.deeplearning4j.nn.layers.objdetect.Yolo2OutputLayer) computationGraph.getOutputLayer(0);
//		Mat convertedMat = new Mat();
//		Mat convertedMat_big = new Mat();
//
//		while (validationGenerator.hasNext()) {
//			org.nd4j.linalg.dataset.DataSet ds = validationGenerator.next();
//			INDArray features = ds.getFeatures();
//			INDArray results = computationGraph.outputSingle(features);
//			List<DetectedObject> objs = yout.getPredictedObjects(results, 0.3);
//			YoloUtils.nms(objs, 0.4);
//			Mat mat = imageLoader.asMat(features);
//			mat.convertTo(convertedMat, CV_8U, 255, 0);
//			int w = mat.cols() * 2;
//			int h = mat.rows() * 2;
//			resize(convertedMat, convertedMat_big, new Size(w, h));
//			convertedMat_big = drawResults(objs, convertedMat_big, w, h);
//			canvas.showImage(converter.convert(convertedMat_big));
//			canvas.waitKey();
//		}
//		canvas.dispose();
	}

	public void loadDatasetObjectDetection(String trainDirAddress, String testDirAddress){
		this.TrainingDatasetGenerator.loadDatasetObjectDetection(trainDirAddress, testDirAddress);
	}

	public void generateDataIteratorObjectDetection(int batchSize) throws Exception {
		trainGenerator = TrainingDatasetGenerator.trainIterator_ObjectDetection( batchSize);
		validationGenerator = TrainingDatasetGenerator.testIterator_ObjectDetection(1);
	}
//	private String labeltext = null;
	private Mat drawResults(List<DetectedObject> objects, Mat mat, int w, int h) {
		for (DetectedObject obj : objects) {
			double[] xy1 = obj.getTopLeftXY();
			double[] xy2 = obj.getBottomRightXY();
			String label = trainGenerator.getLabels().get(obj.getPredictedClass());
			int x1 = (int) Math.round(w * xy1[0] / 13);
			int y1 = (int) Math.round(h * xy1[1] / 13);
			int x2 = (int) Math.round(w * xy2[0] / 13);
			int y2 = (int) Math.round(h * xy2[1] / 13);
			//Draw bounding box
			Scalar GREEN = RGB(0, 255.0, 0);
			Scalar YELLOW = RGB(255, 255, 0);
			Scalar b1 = RGB(0, 0, 255);
			Scalar b2 = RGB(255, 0, 0);
			Scalar b3 = RGB(0, 255, 255);
			Scalar[] colormap = {GREEN, YELLOW, b1, b2, b3};
			rectangle(mat, new Point(x1, y1), new Point(x2, y2), colormap[obj.getPredictedClass()], 2, 0, 0);
			//Display label text
			String labeltext = label + " " + String.format("%.2f", obj.getConfidence() * 100) + "%";
			int[] baseline = {0};
			Size textSize = getTextSize(labeltext, FONT_HERSHEY_DUPLEX, 1, 1, baseline);
			rectangle(mat, new Point(x1 + 2, y2 - 2), new Point(x1 + 2 + textSize.get(0), y2 - 2 - textSize.get(1)), colormap[obj.getPredictedClass()], FILLED, 0, 0);
			putText(mat, labeltext, new Point(x1 + 2, y2 - 2), FONT_HERSHEY_DUPLEX, 1, RGB(0, 0, 0));
		}
		return mat;
	}
}
