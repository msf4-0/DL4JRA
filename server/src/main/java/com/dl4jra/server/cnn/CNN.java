package com.dl4jra.server.cnn;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.Callable;

import com.dl4jra.server.cnn.utilities.Visualization;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.RNNFormat;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer.PoolingType;
import org.deeplearning4j.nn.conf.layers.objdetect.Yolo2OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.objdetect.DetectedObject;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.transferlearning.FineTuneConfiguration;
import org.deeplearning4j.nn.transferlearning.TransferLearning;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.InvocationType;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.model.stats.StatsListener;
import org.deeplearning4j.ui.model.storage.FileStatsStorage;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.*;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.dl4jra.server.cnn.response.UpdateResponse;
import com.dl4jra.server.globalresponse.Messageresponse;

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

	// Zoomodel for access to model zoo
	private ZooModel zooModel;

	// Default values for segmentation Image record reader
	private int segHeight;
	private int segWidth;

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

		// Default values for segmentation Image record reader
		this.segHeight = 224;
		this.segWidth = 224;
	}

	public RecordReaderDataSetIterator getTrainGenerator() {
		return trainGenerator;
	}


	public class LoadTrainingDatasetExecutor implements Callable<Void> {
		private final String path;
		private final int imagewidth;
		private final int imageheight;
		private final int channels;
		private final int numLabels;
		private final int batchsize;

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
		public LoadTrainingDatasetExecutor(String path, int imagewidth, int imageheight, int channels, int numLabels, int batchsize){
			this.path = path;
			this.imagewidth = imagewidth;
			this.imageheight = imageheight;
			this.channels = channels;
			this.numLabels = numLabels;
			this.batchsize = batchsize;
		}
		@Override
		public Void call() throws Exception {
			TrainingDatasetGenerator.LoadData(path, imagewidth, imageheight, channels, numLabels, batchsize, true);
			return null;
		}
	}

	public class LoadDatasetAutoSplit implements Callable<Void> {
		private final String path;
		private final int imagewidth;
		private final int imageheight;
		private final int channels;
		private final int numLabels;
		private final int batchsize;

		public LoadDatasetAutoSplit(String path, int imagewidth, int imageheight, int channels, int numLabels, int batchsize){
			this.path = path;
			this.imagewidth = imagewidth;
			this.imageheight = imageheight;
			this.channels = channels;
			this.numLabels = numLabels;
			this.batchsize = batchsize;
		}
		@Override
		public Void call() throws Exception {
			TrainingDatasetGenerator.LoadDataAutoSplit(path, imagewidth, imageheight, channels, numLabels, batchsize, true);
			return null;
		}
	}

	public class FlipTrainingDataset implements Callable<Void> {
		private int flipmode;

		/**
		 * Flip training dataset
		 * @param flipmode - Flip image (x-axis/y-axis/both axis)
		 * @throws Exception
		 */
		public FlipTrainingDataset(int flipmode){
			this.flipmode = flipmode;
		}
		@Override
		public Void call() throws Exception {
			try {
				TrainingDatasetGenerator.FlipImage(flipmode);
				return null;
			} catch (InterruptedException ie) {
				return null;
			}

		}
	}
	


	public class RotateTrainingDataset implements Callable<Void> {
		private float angle;

		/**
		 * Rotate training dataset
		 * @param angle - Angle of rotation
		 * @throws Exception
		 */
		public RotateTrainingDataset(float angle) {
			this.angle = angle;
		}
			@Override
			public Void call() throws Exception {
				TrainingDatasetGenerator.RotateImage(angle);
				return null;
			}
	}

	public class ResizeTrainingDataset implements Callable<Void> {
		private final int width;
		private final int height;

		/**
		 * Resize training dataset
		 * @param width - Width of image after resize
		 * @param height - Height of image after resize
		 * @throws Exception
		 */
		public ResizeTrainingDataset(int width, int height) {
			this.width = width;
			this.height = height;
		}
		@Override
		public Void call() throws Exception {
			TrainingDatasetGenerator.ResizeImage(width, height);
			return null;
		}
	}
	

	public class GenerateTrainingDatasetIterator implements Callable<Void> {

		/**
		 * Generate training dataset iterator
		 * @throws Exception
		 */
		public GenerateTrainingDatasetIterator() {
		}
		@Override
		public Void call() throws Exception {
			TrainingDatasetIterator = TrainingDatasetGenerator.GetDatasetIterator();
			return null;
		}
	}

	public class GenerateDatasetAutoSplitIterator implements Callable<Void> {
		public GenerateDatasetAutoSplitIterator() {
		}

		@Override
		public Void call() throws Exception {
			TrainingDatasetIterator = TrainingDatasetGenerator.trainIterator();
			ValidationDatasetIterator = TrainingDatasetGenerator.testIterator();
			return null;
		}
	}

	public class LoadValidationDataset implements Callable<Void> {
		private final String path;
		private final int imagewidth;
		private final int imageheight;
		private final int channels;
		private final int numLabels;
		private final int batchsize;

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
		public LoadValidationDataset(String path, int imagewidth, int imageheight, int channels, int numLabels, int batchsize) {
			this.path = path;
			this.imagewidth = imagewidth;
			this.imageheight = imageheight;
			this.channels = channels;
			this.numLabels = numLabels;
			this.batchsize = batchsize;
		}
		@Override
		public Void call() throws Exception {
			ValidationDatasetGenerator.LoadData(path, imagewidth, imageheight, channels, numLabels, batchsize, true);
			return null;
		}
	}
	


	public class FlipValidationDataset implements Callable<Void> {
		private int flipmode;

			/**
			 * Flip validation dataset
			 * @param flipmode - Flip image (x-axis/y-axis/both axis)
			 * @throws Exception
			 */
		public FlipValidationDataset(int flipmode) {
			this.flipmode = flipmode;
		}
			@Override
			public Void call() throws Exception {
				ValidationDatasetGenerator.FlipImage(flipmode);
				return null;
			}
	}
	


	public class RotateValidationDataset implements Callable<Void> {
		private float angle;

		/**
		 * Rotate validation dataset
		 * @param angle - Angle of rotation
		 * @throws Exception
		 */
		public RotateValidationDataset(float angle) {
			this.angle = angle;
		}
		@Override
		public Void call() throws Exception {
			ValidationDatasetGenerator.RotateImage(angle);
			return null;
		}
	}

	public class ResizeValidationDataset implements Callable<Void> {
		private final int width;
		private final int height;

		/**
		 * Resize validation dataset
		 * @param width - Width of image after resize
		 * @param height - Height of image after resize
		 * @throws Exception
		 */
		public ResizeValidationDataset(int width, int height) {
			this.width = width;
			this.height = height;
		}
		@Override
		public Void call() throws Exception {
			ValidationDatasetGenerator.ResizeImage(width, height);
			return null;
		}
	}

	/**
	 * Generate validation dataset iterator
	 * @throws Exception
	 */
	public class GenerateValidationDatasetIterator implements Callable<Void> {
		@Override
		public Void call() throws Exception {
			ValidationDatasetIterator = ValidationDatasetGenerator.GetDatasetIterator();
			return null;
		}
	}



	public class LoadTrainingDatasetCSV implements Callable<Void> {
		private final String path;
		private final int numSkipLines;
		private final int numClassLabels;
		private final int batchsize;

		public LoadTrainingDatasetCSV(String path, int numSkipLines, int numClassLabels, int batchsize) {
			this.path = path;
			this.numSkipLines = numSkipLines;
			this.numClassLabels = numClassLabels;
			this.batchsize = batchsize;
		}
		@Override
		public Void call() throws Exception {
			TrainingDatasetGenerator.LoadTrainDataCSV(path, numSkipLines, numClassLabels, batchsize);
			return null;
		}
	}


	public class LoadTestingDatasetCSV implements Callable<Void> {
		private final String path;
		private final int numSkipLines;
		private final int numClassLabels;
		private final int batchsize;

		public LoadTestingDatasetCSV(String path, int numSkipLines, int numClassLabels, int batchsize) {
			this.path = path;
			this.numSkipLines = numSkipLines;
			this.numClassLabels = numClassLabels;
			this.batchsize = batchsize;
		}
		@Override
		public Void call() throws Exception {
			TrainingDatasetGenerator.LoadTestDataCSV(path, numSkipLines, numClassLabels, batchsize);
			return null;
		}
	}


	public class GenerateTrainingDatasetIteratorCSV implements Callable<Void> {
		@Override
		public Void call() throws Exception {
			TrainingDatasetIterator = TrainingDatasetGenerator.trainDataSetIteratorCSV();
			return null;
		}
	}


	public class GenerateValidatingDatasetIteratorCSV implements Callable<Void> {
		@Override
		public Void call() throws Exception {
			ValidationDatasetIterator = TrainingDatasetGenerator.testDataSetIteratorCSV();
			return null;
		}
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
//		this.multiLayerNetwork.setListeners(
//				new ScoreIterationListener(5),
//				new EvaluativeListener(ValidationDatasetIterator, 1, InvocationType.EPOCH_END)
//		);
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

	public class EvaluateModel_CG implements Callable<Void> {
		@Override
		public Void call() throws Exception {
			System.out.println("***** Test Evaluation *****");
			Evaluation eval = new Evaluation(6);
			ValidationDatasetIterator.reset();
			DataSet testDataSet = ValidationDatasetIterator.next(1);
			INDArray s = testDataSet.getFeatures();
			System.out.println(s);
			while(ValidationDatasetIterator.hasNext())
			{
				if (Thread.currentThread().isInterrupted()){
					break;
				}
				testDataSet = ValidationDatasetIterator.next();
				INDArray[] predicted = computationGraph.output(testDataSet.getFeatures());
				INDArray labels = testDataSet.getLabels();

				eval.evalTimeSeries(labels, predicted[0], testDataSet.getLabelsMaskArray());
			}
//
			return null;
		}
	}



	public class TrainNetwork implements Callable<Void> {
		private final int epochs;
		private final int scoreListener;

		/**
		 * Network training
		 * @param epochs - Number of epoch for network training
		 * @param scoreListener - Get the score of network (classifier) after (scoreListener)
		 * @throws Exception
		 */
		public TrainNetwork (int epochs, int scoreListener) {
			this.epochs = epochs;
			this.scoreListener = scoreListener;
		}
		@Override
		public Void call() throws Exception {

			try {
				if (TrainingDatasetIterator == null)
					throw new Exception("There is no training dataset");
				if (!networkconstructed)
					throw new Exception("Neural network is not constructed");
				for (int counter = 0; counter < epochs; counter++) {
					// Check if the current thread is interrupted, if so, break the loop.
					if (Thread.currentThread().isInterrupted()){
						break;
					}

					if (multiLayerNetwork != null) {
						multiLayerNetwork.setListeners(
								new ScoreIterationListener(scoreListener),
								new EvaluativeListener(ValidationDatasetIterator, 1, InvocationType.EPOCH_END)
						);
						multiLayerNetwork.fit(TrainingDatasetIterator);
						if (epochs % scoreListener == 0)
							System.out.println("Score in epoch " + counter + " : " + multiLayerNetwork.score());
					}
					if (computationGraph != null) {
						computationGraph.fit(TrainingDatasetIterator);
						if (epochs % scoreListener == 0)
							System.out.println("Score in epoch " + counter + " : " + computationGraph.score());
					}
					TrainingDatasetIterator.reset();
				}
			}
			catch (Exception e){
				System.out.println(e.getMessage());
			}
			return null;
		}
	}

	public class TrainNetworkSSEmitter implements Callable<Void> {
		private final int epochs;
		private final int scoreListener;
		private final SseEmitter sseEmitter;

		/**
		 * Training (Respond using SseEmitter)
		 * @param epochs - Number of epoch for network training
		 * @param scoreListener - Get the score of network (classifier) after (scoreListener)
		 * @param sseEmitter
		 * @throws Exception
		 */
		public TrainNetworkSSEmitter(int epochs, int scoreListener, SseEmitter sseEmitter) {
			this.epochs = epochs;
			this.scoreListener = scoreListener;
			this.sseEmitter = sseEmitter;
		}
		@Override
		public Void call() throws Exception {
			Object message;
			if (TrainingDatasetIterator == null) {
				message = "Training dataset not set";
				sseEmitter.send(SseEmitter.event().name("TrainingErrorEvent").data(message));
				throw new Exception("Training dataset not set");
			}
			if (!networkconstructed) {
				message = "Neural network is not constructed";
				sseEmitter.send(SseEmitter.event().name("TrainingErrorEvent").data(message));
				throw new Exception("Neural network is not constructed");
			}

			for (int counter = 0; counter < epochs; counter++) {

				// Check if the current thread is interrupted, if so, break the loop.
				if (Thread.currentThread().isInterrupted()){
					break;
				}

				if(multiLayerNetwork != null) {
					if (ValidationDatasetIterator != null){
						multiLayerNetwork.setListeners(
								new ScoreIterationListener(scoreListener),
								new EvaluativeListener(ValidationDatasetIterator, 1, InvocationType.EPOCH_END)
						);
					}
					else{
						multiLayerNetwork.setListeners(
								new ScoreIterationListener(scoreListener)
						);
					}


					TrainingDatasetIterator.reset();
					multiLayerNetwork.fit(TrainingDatasetIterator);
					message = counter;
					sseEmitter.send(SseEmitter.event().name("TrainingEpochUpdateEvent").data(message));
					if (counter % scoreListener == 0) {
						message = "Score in epoch " + counter + " : " + String.format("%.2f", multiLayerNetwork.score());
						sseEmitter.send(SseEmitter.event().name("TrainingScoreEvent").data(message));
					}
				}
				if(computationGraph != null){
					TrainingDatasetIterator.reset();
					computationGraph.fit(TrainingDatasetIterator);
					message = counter;
					sseEmitter.send(SseEmitter.event().name("TrainingEpochUpdateEvent").data(message));
					if (counter % scoreListener == 0) {
						message = "Score in epoch " + counter + " : " + String.format("%.2f", computationGraph.score());
						sseEmitter.send(SseEmitter.event().name("TrainingScoreEvent").data(message));
					}
				}
			}
			message = "Network Training completed";
			sseEmitter.send(SseEmitter.event().name("TrainingCompleteEvent").data(message));
			return null;
		}
	}

	public class TrainNetworkSimpMessagingTemplate implements Callable<Void> {
		private final int epochs;
		private final int scoreListener;
		private final SimpMessagingTemplate template;

		/**
		 * Training (Respond using Websocket)
		 * @param epochs - Number of epoch for network training
		 * @param scoreListener - Get the score of network (classifier) after (scoreListener)
		 * @param template
		 * @throws Exception
		 */
		public TrainNetworkSimpMessagingTemplate(int epochs, int scoreListener, SimpMessagingTemplate template) {
			this.epochs = epochs;
			this.scoreListener = scoreListener;
			this.template = template;
		}
		@Override
		public Void call() throws Exception {

			if (TrainingDatasetIterator == null) {
				throw new Exception("Training dataset not set");
			}
			if (!networkconstructed) {
				throw new Exception("Neural network is not constructed");
			}

			// start ui server
			UIServer uiServer = UIServer.getInstance();
			StatsStorage statsStorage = new FileStatsStorage(new File(System.getProperty("java.io.tmpdir"), "ui-stats.dl4j"));
			uiServer.attach(statsStorage);
			if(Desktop.isDesktopSupported())
			{
				Desktop.getDesktop().browse(new URI("http://localhost:9000"));
			}

			template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, epochs));
			for (int counter = 0; counter < epochs; counter++) {
				// Check if the current thread is interrupted, if so, break the loop.
				if (Thread.currentThread().isInterrupted()){
					uiServer.detach(statsStorage);
					statsStorage.close();
					System.out.println("stopping ui server");
					uiServer.stop();
					break;
				}

				if(multiLayerNetwork != null) {
					if (ValidationDatasetIterator != null){
						multiLayerNetwork.setListeners(
								new ScoreIterationListener(scoreListener),
								new EvaluativeListener(ValidationDatasetIterator, 1, InvocationType.EPOCH_END),
								new StatsListener(statsStorage, 5)
						);
					}
					else{
						multiLayerNetwork.setListeners(
								new ScoreIterationListener(scoreListener),
								new StatsListener(statsStorage, 5)
						);
					}
					TrainingDatasetIterator.reset();
					multiLayerNetwork.fit(TrainingDatasetIterator);
					template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(counter + 1, epochs));
					if (counter % scoreListener == 0) {
						String message = "Score in epoch " + counter + " : " + String.format("%.2f", multiLayerNetwork.score());
						template.convertAndSend("/response/cnn/message", new Messageresponse(message));
					}
				}
				System.out.println(computationGraph != null);
				if(computationGraph != null) {
					computationGraph.setListeners(
							new ScoreIterationListener((scoreListener)),
							new StatsListener(statsStorage, 5)
					);
					System.out.println("Starting Training");
					computationGraph.fit(TrainingDatasetIterator);
					System.out.println("After fit");
					TrainingDatasetIterator.reset();
					template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(counter + 1, epochs));
					if (counter % scoreListener == 0) {
						String message = "Score in epoch " + counter + " : " + String.format("%.2f", computationGraph.score());
						template.convertAndSend("/response/cnn/message", new Messageresponse(message));
					}
				}
			}
			uiServer.detach(statsStorage);
			statsStorage.close();
			System.out.println("stopping ui server");
			uiServer.stop();
			return null;
		}
	}
	
	/**
	 * Evaluation & validation
	 */
	public class ValidateNetwork implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			Evaluation evaluation;
			if (ValidationDatasetIterator == null)
				throw new Exception("There is no validation dataset");
			if (! networkconstructed)
				throw new Exception("Neural network is not constructed");
			if(multiLayerNetwork != null) {
				evaluation = multiLayerNetwork.evaluate(ValidationDatasetIterator);
				System.out.println("Accuracy - " + evaluation.accuracy());
				System.out.println("Network stats - \n" + evaluation.stats());
			}
			if(computationGraph != null){
				evaluation = computationGraph.evaluate(ValidationDatasetIterator);
				System.out.println("Accuracy - " + evaluation.accuracy());
				System.out.println("Network stats - \n" + evaluation.stats());
			}
			return null;
		}
	}


	public class ValidateNetworkSSEmitter implements Callable<Void> {
		private SseEmitter sseEmitter;

		/**
		 * Evaluation & validation (Respond using SseEmitter)
		 * @param sseEmitter
		 */
		public ValidateNetworkSSEmitter(SseEmitter sseEmitter) {
			this.sseEmitter = sseEmitter;
		}
		@Override
		public Void call() throws Exception {
			if (ValidationDatasetIterator == null) {
				Object message = "Validation dataset not set";
				sseEmitter.send(SseEmitter.event().name("ValidationErrorEvent").data(message));
				throw new Exception("Validation dataset not set");
			}
			if (! networkconstructed) {
				Object message = "Neural network is not constructed";
				sseEmitter.send(SseEmitter.event().name("ValidationErrorEvent").data(message));
				throw new Exception("Neural network is not constructed");
			}
			if(multiLayerNetwork != null) {
				Evaluation evaluation = multiLayerNetwork.evaluate(ValidationDatasetIterator);
				Object accuracy = "Network accuracy : " + evaluation.accuracy();
				sseEmitter.send(SseEmitter.event().name("ValidationAccuracyEvent").data(accuracy));
				Object message = "Network Validation Completed";
				sseEmitter.send(SseEmitter.event().name("ValidationCompleteEvent").data(message));
			}

			if(computationGraph != null) {
				Evaluation evaluation = computationGraph.evaluate(ValidationDatasetIterator);
				Object accuracy = "Network accuracy : " + evaluation.accuracy();
				sseEmitter.send(SseEmitter.event().name("ValidationAccuracyEvent").data(accuracy));
				Object message = "Network Validation Completed";
				sseEmitter.send(SseEmitter.event().name("ValidationCompleteEvent").data(message));
			}
			return null;
		}
	}
	

	public class ValidateNetworkSimpMessagingTemplate implements Callable<Void> {
		private SimpMessagingTemplate template;

		/**
		 * Evaluation & validation (Respond using Websocket)
		 * @param template
		 */
		public ValidateNetworkSimpMessagingTemplate(SimpMessagingTemplate template) {
			this.template = template;
		}

		@Override
		public Void call() throws Exception {
			if (ValidationDatasetIterator == null)
				throw new Exception("There is no validation dataset");
			if (!networkconstructed)
				throw new Exception("Neural network is not constructed");
			if(multiLayerNetwork != null) {
				Evaluation evaluation = multiLayerNetwork.evaluate(ValidationDatasetIterator);
				String message = "Network accuracy : " + evaluation.accuracy();
				template.convertAndSend("/response/cnn/message", new Messageresponse(message));
			}
			if(computationGraph != null) {
				Evaluation evaluation = computationGraph.evaluate(ValidationDatasetIterator);
				String message = "Network accuracy : " + evaluation.accuracy();
				template.convertAndSend("/response/cnn/message", new Messageresponse(message));
			}
			return null;
		}
	}

	// SEGMENTATION



	public void importPretrainedModel() throws IOException {
		pretrained = (ComputationGraph) UNet.builder().build().initPretrained(PretrainedType.SEGMENT);
//		System.out.println(unet.summary());
	}

	public void configureFineTune(int seed){
		this.cnnconfig.configureFineTune(seed);
	}

	public class configureFineTuneExecutor implements Callable<Void> {
		private int seed;
		public configureFineTuneExecutor (int seed){
			this.seed = seed;
		}
		@Override
		public Void call() throws Exception {
            cnnconfig.configureFineTune(seed);
			return null;
		}
	}

	public void configureTranferLearning( String featurizeExtractionLayer, String vertexName,
										  String nInName, int nIn, WeightInit nInWeightInit,
										  String nOutName, int nOut, WeightInit nOutWeightInit){
		this.cnnconfig.configureTransferLearning(pretrained, featurizeExtractionLayer, vertexName, nInName, nIn, nInWeightInit,
				nOutName, nOut, nOutWeightInit);
	}


	public void addCnnLossLayer(String layerName, LossFunction lossFunction, Activation activation, String layerInput ){
		this.cnnconfig.addCnnLossLayer(layerName, lossFunction, activation, layerInput);
	}

	public void setOutput(String outputName){
		this.cnnconfig.setOutput(outputName);
	}

	public void build_TransferLearning(){
		computationGraph = this.cnnconfig.build_TransferLearning();
	}


	public class setIterator_segmentation implements Callable<Void> {
		private final String path;
		private final int batchSize;
		private final double trainPerc;
		private int channels;
		private final String maskFileName;

		public setIterator_segmentation(String path, int batchSize, double trainPerc,
										int channels, String maskFileName) {
			this.path = path;
			this.batchSize = batchSize;
			this.trainPerc = trainPerc;
			this.channels = channels;
			this.maskFileName = maskFileName;
		}
		@Override
		public Void call() throws Exception {
			TrainingDatasetGenerator.setIterator_segmentation(path, batchSize, trainPerc, segHeight, segWidth, channels,
					maskFileName);
			return null;
		}
	}


	public class generateIterator implements Callable<Void> {
		@Override
		public Void call() throws Exception {
			trainGenerator = TrainingDatasetGenerator.trainIterator_segmentation();
			validationGenerator = TrainingDatasetGenerator.testIterator_segmentation();
			return null;
		}
	}


	public class train_segmentation implements Callable<Void> {
		private int epoch;
		public train_segmentation(int epoch) {
			this.epoch = epoch;
		}
		@Override
		public Void call() throws Exception {
			TrainingDatasetGenerator.train_segmentation(epoch, trainGenerator, computationGraph);
			return null;
		}
	}


	public class validation_segmentation implements Callable<Void> {
		@Override
		public Void call() throws Exception {
			TrainingDatasetGenerator.validation_segmentation(validationGenerator, computationGraph);
			return null;
		}
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

	/**
	 *
	 * NEW FEATURES THAT SHOULD BE REFACTORED
	 */
	//
	public void configTransferLearningNetwork_ODetection_Yolo2(double learningRate){
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
				.removeVertexKeepConnections("conv2d_23")
				.removeVertexKeepConnections("outputs")
				.addLayer("conv2d_23",
						new ConvolutionLayer.Builder(1, 1 )
								.nIn(1024)
								.nOut(5 * (5 + trainGenerator.getLabels().size()))
								.stride(1, 1)
								.convolutionMode(ConvolutionMode.Same)
								.weightInit(WeightInit.XAVIER)
								.activation(Activation.IDENTITY)
								.build(),
						"leaky_re_lu_22")
				.addLayer("outputs",
						new Yolo2OutputLayer.Builder()
								.lambdaNoObj(0.5)
								.lambdaCoord(5.0)
								.boundingBoxPriors(priors.castTo(DataType.FLOAT))
								.build(),
						"conv2d_23")
				.setOutputs("outputs")
				.build();
	}

//	public void evaluate_TINYYOLO(int epochs) throws Exception {
//
//		computationGraph.setListeners(new ScoreIterationListener(1));
//		for (int i = 1; i < epochs + 1; i++) {
//			// Check if the current thread is interrupted, if so, break the loop.
//			if (Thread.currentThread().isInterrupted()){
//				break;
//			}
//			computationGraph.fit(trainGenerator);
//			System.out.println("*** Completed epoch {" + i+ " } ***");
//		}



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
//	}

	public class evaluate_TINYYOLO implements Callable<Void> {
			private int epochs;

			public evaluate_TINYYOLO(int epochs) {
				this.epochs = epochs;
			}

			@Override
			public Void call() throws Exception {
				computationGraph.setListeners(new ScoreIterationListener(1));
				for (int i = 1; i < epochs + 1; i++) {
					// Check if the current thread is interrupted, if so, break the loop.
					if (Thread.currentThread().isInterrupted()) {
						break;
					}
					computationGraph.fit(trainGenerator);
					System.out.println("*** Completed epoch {" + i + " } ***");
				}
				return null;
			}
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
			// Check if the current thread is interrupted, if so, break the loop.
			if (Thread.currentThread().isInterrupted()){
				break;
			}

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



	public void configTransferLearningNetwork_vgg(double learningRate){
		// STEP 2: Configure the model configurations for layers that are not frozen by using FineTuneConfiguration
		FineTuneConfiguration fineTuneCOnf = new FineTuneConfiguration.Builder()
				.updater(new Nesterovs(5e-5))
				.seed(seed)
				.build();

		// STEP 3: Build the neural network configuration by using ComputationGraph
		computationGraph = new TransferLearning.GraphBuilder(this.pretrained)
				.fineTuneConfiguration(fineTuneCOnf)
				.setFeatureExtractor("fc2")
				.removeVertexKeepConnections("predictions")
				.addLayer("predictions",
						new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
								.nIn(4096).nOut(trainGenerator.getLabels().size())
								.weightInit(WeightInit.XAVIER)
								.activation(Activation.SOFTMAX).build(),
						"fc2")
				.build();
	};

	public void configTransferLearningNetwork_squeezenet(double learningRate){
		// STEP 2: Configure the model configurations for layers that are not frozen by using FineTuneConfiguration
		FineTuneConfiguration fineTuneCOnf = new FineTuneConfiguration.Builder()
				.updater(new Nesterovs(5e-5))
				.seed(seed)
				.build();

		ComputationGraph squeezeNetTransfer = new TransferLearning.GraphBuilder(computationGraph)
				.fineTuneConfiguration(fineTuneCOnf)
				.setFeatureExtractor("drop9")
				.removeVertexKeepConnections("conv10")
				.removeVertexAndConnections("relu10")
				.removeVertexAndConnections("global_average_pooling2d_5")
				.removeVertexAndConnections("loss")
				.addLayer("conv10",
						new ConvolutionLayer.Builder(1,1).nIn(512).nOut(trainGenerator.getLabels().size())
								.build(),
						"drop9")
				.addLayer("conv10_act", new ActivationLayer(Activation.RELU), "conv10")
				.addLayer("global_avg_pool", new GlobalPoolingLayer(org.deeplearning4j.nn.conf.layers.PoolingType.AVG), "conv10_act")
				.addLayer("softmax", new ActivationLayer(Activation.SOFTMAX), "global_avg_pool")
				.addLayer("loss", new LossLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD).build(), "softmax")
				.setOutputs("loss")
				.build();
	};



	/**
	 *
	 * NEW MODELS
	 */


	public void importvgg16() throws IOException {
		seed = 123;
		Nd4j.getRandom().setSeed(seed);
		priors = Nd4j.create(priorBoxes);
		this.pretrained  = (ComputationGraph) VGG16.builder().build().initPretrained();
	}

	public void importvgg19() throws IOException {
		seed = 123;
		Nd4j.getRandom().setSeed(seed);
		priors = Nd4j.create(priorBoxes);
		this.pretrained  = (ComputationGraph) VGG19.builder().build().initPretrained();
	}

	public void importSqueezeNet() throws IOException {
		seed = 123;
		Nd4j.getRandom().setSeed(seed);
		priors = Nd4j.create(priorBoxes);
		this.pretrained = (ComputationGraph) SqueezeNet.builder().build().initPretrained();
	}

	public void importYolo2() throws IOException {
		seed = 123;
		Nd4j.getRandom().setSeed(seed);
		priors = Nd4j.create(priorBoxes);
		this.pretrained = (ComputationGraph) YOLO2.builder().build().initPretrained();
	}

}
