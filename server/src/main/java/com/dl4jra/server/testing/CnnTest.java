package com.dl4jra.server.testing;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.RNNFormat;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer.PoolingType;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.zoo.model.UNet;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import com.dl4jra.server.cnn.CNN;

public class CnnTest {

	/**
	 * CNN Functionality Testing
	 * 1. Initialize CNN class
	 * 2. Load Training dataset and its iterator
	 * 3. Initialize CNN configuration
	 * 4. Convolution layer -> Subsampling layer -> Dense layer -> Output layer
	 * 5. Set input type
	 * 6. Construct network
	 * 7. Train network
	 * 8. Save model
	 */
	public static void main(String[] args) {
		/* Please change the values accordingly to your dataset */
//		DOG BREED CNN
		String datasetpath = "C:\\Users\\User\\.deeplearning4j\\data\\dog-breed-identification";
		int inputwidth = 224;
		int inputheight = 224;
		int inputchannel = 3;
		int numberlabels = 5;
		int batchsize = 32;
		int epochs = 10;
		int scorelistener = 1;
		String directorytosave = "D://AppGeneratedDataset";
		String filename = "testmodel"; // Without .zip extension

//		HUMAN ACTIVITY CLASSIFICATION LSTM
//		String pathTrain = "C:\\Users\\User\\.deeplearning4j\\data\\humanactivity\\train";
//		String pathTest = "C:\\Users\\User\\.deeplearning4j\\data\\humanactivity\\test";
//		int numClassLabels = 6;
//		int batchsize = 64;
//		int epochs = 15;
//		int numSkipLines = 0;
//		int scorelistener = 1;
//		String directorytosave = "D://AppGeneratedDataset";
//		String filename = "testmodel"; // Without .zip extension

//		String path = "C:\\Users\\User\\.deeplearning4j\\data\\carvana-masking-challenge\\carvana-masking-challenge\\train\\inputs";
//		String path = "C:\\Users\\User\\.deeplearning4j\\data\\data-science-bowl-2018\\data-science-bowl-2018\\data-science-bowl-2018-2\\train\\inputs";
//		int inputwidth = 224;
//		int inputheight = 224;
//		int inputchannel = 1;
//		int batchsize = 2;
//		int epochs = 1;
//		double trainPerc = 0.1;

		try {
			CNN cnn = new CNN();

//			DOG BREED CNN
			cnn.LoadDatasetAutoSplit(datasetpath, inputwidth, inputheight, inputchannel, numberlabels, batchsize);
			cnn.GenerateDatasetAutoSplitIterator();
			cnn.InitializeConfigurations(123, 0.001, OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT,
					ConvolutionMode.Same, Activation.RELU, WeightInit.XAVIER, GradientNormalization.RenormalizeL2PerLayer);
			cnn.AppendConvolutionLayer(0,  inputchannel, 96, 11, 11, 4, 4,
					0, 0, null, 0, 0, ConvolutionMode.Truncate);
			cnn.AppendLocalResponseNormalizationLayer(1);
			cnn.AppendSubsamplingLayer(2, 3, 3, 2, 2, 1, 1,
					PoolingType.MAX, null);
			cnn.AppendConvolutionLayer(3, inputchannel,256 , 5,5, 1, 1,
					2, 2, null, 0, 0.1, ConvolutionMode.Truncate);
			cnn.AppendSubsamplingLayer(4, 3, 3, 2, 2, 0, 0,
					PoolingType.MAX, ConvolutionMode.Truncate);
			cnn.AppendLocalResponseNormalizationLayer(5);
			cnn.AppendConvolutionLayer(6, inputchannel,384 , 3,3, 1, 1, 0, 0,
					null, 0, 0, ConvolutionMode.Same);
			cnn.AppendConvolutionLayer(7,  inputchannel,384 , 3,3, 1, 1, 0, 0,
					null, 0.2, 0.1, null);
			cnn.AppendConvolutionLayer(8,inputchannel, 256 , 3,3, 1, 1, 0, 0,
					null, 0.2, 0.1, null);
			cnn.AppendSubsamplingLayer(9, 3, 3, 2, 2, 0, 0,
					PoolingType.MAX, ConvolutionMode.Truncate);
			cnn.AppendDenseLayer(10, 4096, null, 0.5, 0.1, WeightInit.XAVIER);
			cnn.AppendDenseLayer(11, 4096, null, 0.5, 0.1, WeightInit.XAVIER);
			cnn.AppendOutputLayer(12, numberlabels, Activation.SOFTMAX, LossFunction.NEGATIVELOGLIKELIHOOD, WeightInit.XAVIER);
			cnn.SetInputType(inputwidth, inputheight, inputchannel);
			cnn.ConstructNetwork();
			cnn.TrainNetwork(epochs, scorelistener);
			cnn.ValidateNetwork();
			cnn.SaveModal(directorytosave, filename);

//			HUMAN ACTIVITY CLASSIFICATION LSTM
//			cnn.LoadTrainingDatasetCSV(pathTrain, numSkipLines, numClassLabels, batchsize);
//			cnn.GenerateTrainingDatasetIteratorCSV();
//			cnn.LoadTestingDatasetCSV(pathTest, numSkipLines, numClassLabels, batchsize);
//			cnn.GenerateValidatingDatasetIteratorCSV();
//			cnn.InitializeConfigurationsGraphBuilder(12345, 0.05, OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT,
//					WeightInit.XAVIER);
//			cnn.AddInput("trainFeatures");
//			cnn.SetOutput("predictActivity");
//			cnn.AppendConvolutionLayer_CG("CNN", 1, 9, 32, Activation.RELU, "trainFeatures");
//			cnn.AppendLSTMLayer("LSTM", 32,100, Activation.TANH, "CNN");
//			cnn.AppendRnnOutputLayer("predictActivity", RNNFormat.NCW, 100, 6, LossFunction.MCXENT,
//					Activation.SOFTMAX, "LSTM");
//			cnn.ConstructNetworkRNN();
//			cnn.TrainNetwork(epochs, 1);
//			cnn.ValidateNetwork();
//			cnn.EvaluateModel_CG();

			// SEGMENTATION CARS/CELL
//			cnn.importPretrainedModel();
//			cnn.configureFineTune(12345);
//			cnn.configureTranferLearning("conv2d_4",
//					"activation_23",
//					"conv2d_1", 1, WeightInit.XAVIER,
//					"conv2d_23", 1, WeightInit.XAVIER);
//			cnn.addCnnLossLayer("output", LossFunction.XENT, Activation.SIGMOID, "conv2d_23");
//			cnn.setOutput("output");
//			cnn.build_TransferLearning();
//			cnn.setIterator_segmentation(path, batchsize, trainPerc, inputwidth, inputheight, inputchannel, "masks");
//			cnn.generateIterator();
//			cnn.train_segmentation(epochs);
//			cnn.validation_segmentation();


		} catch (Exception exception) {
			System.out.println("EXCEPTION : " + exception.getMessage());
		}

	}

}
