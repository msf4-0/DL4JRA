package com.dl4jra.server.cnn;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer.PoolingType;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import com.dl4jra.server.cnn.layerbuilder.ConvoLayerBuilder;
import com.dl4jra.server.cnn.layerbuilder.DenseLayerBuilder;
import com.dl4jra.server.cnn.layerbuilder.OutputLayerBuilder;
import com.dl4jra.server.cnn.layerbuilder.PoolingLayerBuilder;

public class CNNConfiguration {
	private NeuralNetConfiguration.ListBuilder ListBuilder;

	/**
	 * Initialize configuration
	 * @param seed - Random seed
	 * @param learningrate - Learning rate of network
	 * @param optimizationalgorithm - Optimization algorithm
	 */
	public void Initialize (int seed, double learningrate, OptimizationAlgorithm optimizationalgorithm) {
		NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
		builder.seed(seed);
		builder.updater(new Adam(learningrate));
		builder.optimizationAlgo(optimizationalgorithm);
		this.ListBuilder = builder.list();
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
	public void AppendConvolutionLayer(int ordering, int nIn, int nOut, int kernalx, int kernaly, int stridex, int stridey, Activation activationfunction) throws Exception {
		if (! NetworkConfigured())
			throw new Exception("Neural network is not configured yet");
		ConvolutionLayer convoLayer = ConvoLayerBuilder.GenerateLayer(nIn, nOut, kernalx, kernaly, stridex, stridey, activationfunction);
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
	public void AppendSubsamplingLayer (int ordering, int kernalx, int kernaly, int stridex, int stridey, PoolingType poolingType) throws Exception{
		if (! NetworkConfigured())
			throw new Exception("Neural network is not configured yet");
		SubsamplingLayer poolingLayer = PoolingLayerBuilder.GenerateLayer(kernalx, kernaly, stridex, stridey, poolingType);
		this.ListBuilder.layer(ordering, poolingLayer);
	}
	
	/**
	 * Append dense layer
	 * @param ordering - Ordering of layer
	 * @param nOut - Number of node
	 * @param activationfunction - Layer's activation function
	 * @throws Exception
	 */
	public void AppendDenseLayer (int ordering, int nOut, Activation activationfunction) throws Exception{
		if (! NetworkConfigured())
			throw new Exception("Neural network is not configured yet");
		DenseLayer denseLayer = DenseLayerBuilder.GenerateLayer(nOut, activationfunction);
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
	public void AppendOutputLayer (int ordering, int nOut, Activation activationfunction, LossFunction lossfunction) throws Exception{
		if (! NetworkConfigured())
			throw new Exception("Neural network is not configured yet");
		OutputLayer outputLayer = OutputLayerBuilder.GenerateLayer(nOut, activationfunction, lossfunction);
		this.ListBuilder.layer(ordering, outputLayer);
	}
	
	/**
	 * Set input type
	 * @param imagewidth - Width of image dataset
	 * @param imageheight - Height of image dataset
	 * @param channels - (Color) channel of image dataset
	 * @throws Exception
	 */
	public void SetInputType(int imagewidth, int imageheight, int channels) throws Exception {
		if (! NetworkConfigured())
			throw new Exception("Neural network is not configured yet");
		this.ListBuilder.setInputType(InputType.convolutional(imageheight, imagewidth, channels));
	}

	/**
	 * Build MultiLayerConfiguration
	 * @return MultiLayerConfiguration object
	 * @throws Exception
	 */
	public MultiLayerConfiguration build() throws Exception {
		return this.ListBuilder.build();
	}

	/**
	 * @return boolean - True if network is configured, else False
	 */
	private boolean NetworkConfigured() { return this.ListBuilder != null; }
}
