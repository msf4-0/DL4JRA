package com.dl4jra.server.cnn.layerbuilder;

import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer.PoolingType;
public class PoolingLayerBuilder {
	
	/**
	 * Build subsampling layer
	 * @param kernalx - Kernal's width
	 * @param kernaly - Kernal's height
	 * @param stridex - Stride's width
	 * @param stridey - Stride's height
	 * @param paddingx - Padding's width
	 * @param paddingy - Padding's height
	 * @param poolingType - Pooling type (min/max/average)
	 * @return
	 */
	public static SubsamplingLayer GenerateLayer(int kernalx, int kernaly, int stridex,
												 int stridey, int paddingx, int paddingy, PoolingType poolingType,
												 ConvolutionMode convolutionMode) {
		SubsamplingLayer.Builder poolingLayer = new SubsamplingLayer.Builder();
		poolingLayer.kernelSize(kernalx, kernaly);
		poolingLayer.stride(stridex, stridey);
		poolingLayer.padding(paddingx, paddingy);
		poolingLayer.poolingType(poolingType);
		if(convolutionMode != null){
			poolingLayer.convolutionMode(convolutionMode);
		}
		return poolingLayer.build();
	}
}
