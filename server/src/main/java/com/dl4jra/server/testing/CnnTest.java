package com.dl4jra.server.testing;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer.PoolingType;
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
		String trainingdatasetpath = "";
		int inputwidth = 300;
		int inputheight = 300;
		int inputchannel = 3;
		int numberlabels = 2;
		int batchsize = 1;
		int epochs = 10;
		int scorelistener = 1;
		String directorytosave = "";
		String filename = ""; // Without .zip extension
		try {
			CNN cnn = new CNN();
			cnn.LoadTrainingDataset(trainingdatasetpath, inputwidth, inputheight, inputchannel, numberlabels, batchsize);
			cnn.GenerateTrainingDatasetIterator();
			cnn.InitializeConfigurations(1234, 0.0001, OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
			cnn.AppendConvolutionLayer(0, inputchannel, 10, 2, 2, 2, 2, Activation.RELU);
			cnn.AppendSubsamplingLayer(1, 2, 2, 2, 2, PoolingType.MAX);
			cnn.AppendDenseLayer(2, 10, Activation.RELU);
			cnn.AppendOutputLayer(3, numberlabels, Activation.SOFTMAX, LossFunction.NEGATIVELOGLIKELIHOOD);
			cnn.SetInputType(inputwidth, inputheight, inputchannel);
			cnn.ConstructNetwork();
			cnn.TrainNetwork(epochs, scorelistener);
			cnn.SaveModal(directorytosave, filename);
		} catch (Exception exception) {
			System.out.println("EXCEPTION : " + exception.getMessage());
		}

	}

}
