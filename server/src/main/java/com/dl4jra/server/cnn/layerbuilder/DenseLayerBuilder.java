package com.dl4jra.server.cnn.layerbuilder;

import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.nd4j.linalg.activations.Activation;

public class DenseLayerBuilder {
	
	/**
	 * Build dense layer
	 * @param nOut - Number of node
	 * @param activationfunction - Layer's activation function
	 * @return DenseLayer object
	 */
	public static DenseLayer GenerateLayer(int nOut, Activation activationFunction) {
		DenseLayer.Builder denseLayer = new DenseLayer.Builder();
		denseLayer.nOut(nOut);
		denseLayer.activation(activationFunction);
		denseLayer.weightInit(new UniformDistribution(0, 1));
		return denseLayer.build();
	}
	
	public static DenseLayer GenerateLayer(int nIn, int nOut, Activation activationFunction) {
		DenseLayer.Builder denseLayer = new DenseLayer.Builder();
		denseLayer.nIn(nIn);
		denseLayer.nOut(nOut);
		denseLayer.activation(activationFunction);
		denseLayer.weightInit(new UniformDistribution(0, 1));
		return denseLayer.build();
	}
}
