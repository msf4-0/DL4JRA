package com.dl4jra.server.cnn;

import com.dl4jra.server.cnn.layerbuilder.*;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer.PoolingType;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ops.impl.loss.L2Loss;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

public class CNNConfiguration {
	private NeuralNetConfiguration.ListBuilder ListBuilder;

	/**
	 * Initialize configuration
	 * @param seed - Random seed
	 * @param learningrate - Learning rate of network
	 * @param optimizationalgorithm - Optimization algorithm
	 */
	public void Initialize (int seed, double learningrate, OptimizationAlgorithm optimizationalgorithm,
							ConvolutionMode convolutionMode, Activation activation, WeightInit weightInit,
							GradientNormalization gradientNormalization) {
		NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
		builder.seed(seed);
//		builder.optimizationAlgo(optimizationalgorithm);
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
		if (! NetworkConfigured())
			throw new Exception("Neural network is not configured yet");
		ConvolutionLayer convoLayer = ConvoLayerBuilder.GenerateLayer(nIn, nOut, kernalx, kernaly, stridex, stridey,paddingx, paddingy,
				activationfunction, dropOut, biasInit, convolutionMode);
		this.ListBuilder.layer(ordering, convoLayer);
	}

	public void AppendConvolutionLayer(int ordering, int nOut, int kernalx, int kernaly, int stridex, int stridey,
									   int paddingx, int paddingy, Activation activationfunction, double dropOut, double biasInit,
									   ConvolutionMode convolutionMode) throws Exception {
		if (! NetworkConfigured())
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
		if (! NetworkConfigured())
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
		if (! NetworkConfigured())
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
		if (! NetworkConfigured())
			throw new Exception("Neural network is not configured yet");
		OutputLayer outputLayer = OutputLayerBuilder.GenerateLayer(nOut, activationfunction, lossfunction, weightInit);
		this.ListBuilder.layer(ordering, outputLayer);
	}

//=============================================================================================
	public void AppendLocalResponseNormalizationLayer(int ordering) throws Exception{
		if (! NetworkConfigured())
			throw new Exception("Neural network is not configured yet");
		LocalResponseNormalization localResponseNormalizationLayer = LocalResponseNormalizationLayerBuilder.GenerateLayer();
		this.ListBuilder.layer(ordering, localResponseNormalizationLayer);
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
