package com.dl4jra.server.odtraining;

import java.io.File;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.objdetect.Yolo2OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.transferlearning.FineTuneConfiguration;
import org.deeplearning4j.nn.transferlearning.TransferLearning;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.deeplearning4j.zoo.model.TinyYOLO;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.RmsProp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dl4jra.server.odetection.ODDatasetGenerator;

public class TinyYoloTransferLearner {
	private static final Logger LOGGER = LoggerFactory.getLogger(TinyYoloTransferLearner.class);
	private static final int INPUT_WIDTH = 416;
	private static final int INPUT_HEIGHT = 416;
	private static final int CHANNELS = 3;

	private static final int GRID_WIDTH = 13;
	private static final int GRID_HEIGHT = 13;
	
	private static final int BOXES_NUMBER = 5;
	private static final double[][] PRIOR_BOXES = {{1.5, 1.5}, {2, 2}, {3,3}, {3.5, 8}, {4, 9}};
	
	// YOLO LOSS FUNCTION PARAMETERS 
	private static final double LAMDBA_COORD = 5;
	private static final double LAMDBA_NO_OBJECT = 0.5;
			
	private DataSetIterator TrainingDatasetIterator;
	private FineTuneConfiguration finetuneconf;
	private ComputationGraph network;
	private ComputationGraph pretrained;
	
	// CONSTRUCTOR - LOAD TINYYOLO2 PRETRAINED MODEL
	public TinyYoloTransferLearner() throws Exception {
		this.pretrained = (ComputationGraph) TinyYOLO.builder().build().initPretrained();
	}
	
	// RESET TRAINING NETWORK
	public void ResetTrainer() {
		this.TrainingDatasetIterator = null;
		this.finetuneconf = null;
		this.network = null;
	}
	
	// LOAD TRANSFER LEARNING DATA
	public void LoadTransferLearningDataset(String directorypath, int batchsize) throws Exception {
		this.TrainingDatasetIterator = ODDatasetGenerator.LoadData(directorypath, INPUT_WIDTH, INPUT_HEIGHT, CHANNELS, GRID_WIDTH, GRID_HEIGHT, batchsize);
	}
	
	// FINETUNE CONFIGURATION
	public void FineTune(int seed, double learningrate) throws Exception {
		this.finetuneconf = new FineTuneConfiguration.Builder()
			.seed(seed)
			.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
			.gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
			.gradientNormalizationThreshold(1.0)
			.updater(new RmsProp(learningrate))
			.activation(Activation.IDENTITY)
			.miniBatch(true)
			.trainingWorkspaceMode(WorkspaceMode.ENABLED)
			.build();
	}
	
	// CONSTRUCT TRANSFER LEARNING MODEL
	//  nOut = B * (5 + C) WHERE B = NUMBER OF BOXES and C = NUMBER OF CLASSES
	public void ConstructModel(int numLabels) throws Exception {
		if (this.finetuneconf == null)
			throw new Exception("Finetune configuration is not set");
		INDArray priors = Nd4j.create(PRIOR_BOXES);
		this.network = new TransferLearning.GraphBuilder(this.pretrained)
			.fineTuneConfiguration(this.finetuneconf)
			.setInputTypes(InputType.convolutional(INPUT_HEIGHT, INPUT_WIDTH, CHANNELS))
			.removeVertexAndConnections("conv2d_9") // Remove 2nd last layer
			.removeVertexAndConnections("outputs") // Remove last (output) layer
			.addLayer("conv2d_9", 
					new ConvolutionLayer.Builder(1, 1)
						.nIn(1024).nOut(BOXES_NUMBER * (5 + numLabels))
						.stride(1, 1).convolutionMode(ConvolutionMode.Same)
						.weightInit(WeightInit.UNIFORM)
						.hasBias(false)
						.activation(Activation.IDENTITY)
						.build(), "leaky_re_lu_8")
			.addLayer("outputs",
					new Yolo2OutputLayer.Builder()
						.lambdaNoObj(LAMDBA_NO_OBJECT)
						.lambdaCoord(LAMDBA_COORD)
						.boundingBoxPriors(priors)
						.build(), "conv2d_9")
			.setOutputs("outputs")
			.build();
	}
	
	// TRAIN MODEL FOR x EPOCHS
	public void TrainModel(int epochNum) throws Exception {
		if (this.network == null)
			throw new Exception("Model network is not constructed");
		for (int index = 0; index < epochNum; index++) {
			LOGGER.info("CURRENT EPOCHS: " + index + "/" + epochNum);
			this.TrainingDatasetIterator.reset();
			while(this.TrainingDatasetIterator.hasNext())
				this.network.fit(this.TrainingDatasetIterator.next());
		}
	}
	
	// SAVE MODEL TO DIRECTORY
	public void SaveModel(String directory, String filename) throws Exception {
		File location = new File(directory);
		boolean directoryexists = location.exists() && location.isDirectory();
		if (! directoryexists)
			throw new Exception("Model saving directory does not exist");
		if (this.network == null)
			throw new Exception("Model is not constructed");
		ModelSerializer.writeModel(this.network, new File(directory + "/" + filename + ".data"), true);
	}
}






