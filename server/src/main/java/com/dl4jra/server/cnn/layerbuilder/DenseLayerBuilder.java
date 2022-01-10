package com.dl4jra.server.cnn.layerbuilder;

import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;

public class DenseLayerBuilder {
	public DenseLayerBuilder() {

	}

	/**
	 * Build dense layer
	 * @param nOut - Number of node
	 * @param activationFunction - Layer's activation function
	 * @return DenseLayer object
	 */
	public static DenseLayer GenerateLayer(int nOut, Activation activationFunction, double dropOut, double biasInit,
										   WeightInit weightInit){
		DenseLayer.Builder denseLayer = new DenseLayer.Builder();
		denseLayer.nOut(nOut);
		if(activationFunction != null) {
			denseLayer.activation(activationFunction);
		}
		if(weightInit != null) {
			denseLayer.weightInit(weightInit);
		}
		if(dropOut != 0){
			denseLayer.dropOut(dropOut);
		}
		if(biasInit != 0){
			denseLayer.biasInit(biasInit);
		}
		return denseLayer.build();
	}
	
	public static DenseLayer GenerateLayer(int nIn, int nOut, Activation activationFunction, WeightInit weightInit) {
		DenseLayer.Builder denseLayer = new DenseLayer.Builder();
		denseLayer.nIn(nIn);
		denseLayer.nOut(nOut);
		if(activationFunction != null) {
			denseLayer.activation(activationFunction);
		}
		if(weightInit != null) {
			denseLayer.weightInit(weightInit);
		}
		return denseLayer.build();
	}
}
