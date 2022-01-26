package com.dl4jra.server.cnn.layerbuilder;

import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.RNNFormat;
import org.deeplearning4j.nn.conf.layers.Convolution1DLayer;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.weights.WeightInit;
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
	 * @param paddingx - Padding's width
	 * @param paddingy - Padding's height
	 * @param activationfunction - Layer's activation function
	 * @return ConvolutionLayer object
	 */
	public static ConvolutionLayer GenerateLayer(int nIn, int nOut, int kernalx, int kernaly, int stridex, int stridey,
												 int paddingx, int paddingy, Activation activationfunction, double dropOut, double biasInit,
												 ConvolutionMode convolutionMode)
	{
		ConvolutionLayer.Builder convoLayer = new ConvolutionLayer.Builder();
		convoLayer.nIn(nIn);
		convoLayer.nOut(nOut);
		convoLayer.kernelSize(kernalx, kernaly);
		convoLayer.stride(stridex, stridey);
		convoLayer.padding(paddingx, paddingy);
		if(activationfunction != null) {
			convoLayer.activation(activationfunction);
		}
		if(dropOut != 0){
			convoLayer.dropOut(dropOut);
		}
		if(biasInit != 0){
			convoLayer.biasInit(biasInit);
		}
		if(convolutionMode != null) {
			convoLayer.convolutionMode(convolutionMode);
		}
		return convoLayer.build();
	}

	public static ConvolutionLayer GenerateLayer(int nOut, int kernalx, int kernaly, int stridex, int stridey,
												 int paddingx, int paddingy, Activation activationfunction, double dropOut, double biasInit,
												 ConvolutionMode convolutionMode)
	{
		ConvolutionLayer.Builder convoLayer = new ConvolutionLayer.Builder();
		convoLayer.nOut(nOut);
		convoLayer.kernelSize(kernalx, kernaly);
		convoLayer.stride(stridex, stridey);
		convoLayer.padding(paddingx, paddingy);
		if(activationfunction != null) {
			convoLayer.activation(activationfunction);
		}
		if(dropOut != 0){
			convoLayer.dropOut(dropOut);
		}
		if(biasInit != 0){
			convoLayer.biasInit(biasInit);
		}
		if(convolutionMode != null) {
		convoLayer.convolutionMode(convolutionMode);
		}
		return convoLayer.build();
	}

	public static ConvolutionLayer GenerateLayer_CG(int kernalSize, int nIn, int nOut, Activation activationfunction)
	{
		Convolution1DLayer.Builder convoLayer = new Convolution1DLayer.Builder();
		convoLayer.kernelSize(kernalSize);
		convoLayer.nIn(nIn);
		convoLayer.nOut(nOut);
		if(activationfunction != null) {
			convoLayer.activation(activationfunction);
		}
		return convoLayer.build();
	}
}
