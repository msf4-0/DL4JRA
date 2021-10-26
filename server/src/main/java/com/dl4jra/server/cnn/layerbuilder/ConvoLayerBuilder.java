package com.dl4jra.server.cnn.layerbuilder;

import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.nd4j.linalg.activations.Activation;

public class ConvoLayerBuilder {
	/**
	 * Build convolution layer
	 * @param nIn - Dataset channels 
	 * @param nOut - Number of outputs
	 * @param kernalx - Kernal's width
	 * @param kernaly - Kernal's height
	 * @param stridex - Stride's width
	 * @param stridey - Stride's height
	 * @param activationfunction - Layer's activation function
	 * @return ConvolutionLayer object
	 */
	public static ConvolutionLayer GenerateLayer(int nIn, int nOut, int kernalx, int kernaly, int stridex, int stridey,
			Activation activationfunction) {
		ConvolutionLayer.Builder convoLayer = new ConvolutionLayer.Builder();
		convoLayer.nIn(nIn);
		convoLayer.nOut(nOut);
		convoLayer.kernelSize(kernalx, kernaly);
		convoLayer.stride(stridex, stridey);
		convoLayer.activation(activationfunction);
		return convoLayer.build();
	}
}
