package com.dl4jra.server.cnn.layerbuilder;

import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

public class OutputLayerBuilder {
	
	/**
	 * Build output layer
	 * @param nOut - Number of classes
	 * @param activationfunction - Layer's activation function
	 * @param lossfunction - Layer's loss function
	 * @return
	 */
	public static OutputLayer GenerateLayer(int nOut, Activation activationfunction, LossFunction lossfunction) {
		OutputLayer.Builder outputLayer = new OutputLayer.Builder();
		outputLayer.nOut(nOut);
		outputLayer.activation(activationfunction);
		outputLayer.lossFunction(lossfunction);
		outputLayer.weightInit(new UniformDistribution(0, 1));
		return outputLayer.build();
	}
	
	public static OutputLayer GenerateLayer(int nIn, int nOut, Activation activationfunction, LossFunction lossfunction) {
		OutputLayer.Builder outputLayer = new OutputLayer.Builder();
		outputLayer.nIn(nIn);
		outputLayer.nOut(nOut);
		outputLayer.activation(activationfunction);
		outputLayer.lossFunction(lossfunction);
		outputLayer.weightInit(new UniformDistribution(0, 1));
		return outputLayer.build();
	}
}
