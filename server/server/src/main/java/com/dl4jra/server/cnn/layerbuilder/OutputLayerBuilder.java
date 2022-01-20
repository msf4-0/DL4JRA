package com.dl4jra.server.cnn.layerbuilder;

import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
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
	public static OutputLayer GenerateLayer(int nOut, Activation activationfunction, LossFunction lossfunction, WeightInit weightInit) {
		OutputLayer.Builder outputLayer = new OutputLayer.Builder();
		outputLayer.nOut(nOut);
		if(activationfunction != null) {
			outputLayer.activation(activationfunction);
		}
		outputLayer.lossFunction(lossfunction);
		if(weightInit != null) {
			outputLayer.weightInit(weightInit);
		}
		return outputLayer.build();
	}
	
	public static OutputLayer GenerateLayer(int nIn, int nOut, Activation activationfunction, LossFunction lossfunction, WeightInit weightInit) {
		OutputLayer.Builder outputLayer = new OutputLayer.Builder();
		outputLayer.nIn(nIn);
		outputLayer.nOut(nOut);
		if(activationfunction != null) {
			outputLayer.activation(activationfunction);
		}
		outputLayer.lossFunction(lossfunction);
		if(weightInit != null) {
			outputLayer.weightInit(weightInit);
		}
		return outputLayer.build();
	}
}
