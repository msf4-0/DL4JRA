package com.dl4jra.server.cnn.layerbuilder;

import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer.PoolingType;
public class PoolingLayerBuilder {
	
	/**
	 * Build subsampling layer
	 * @param kernalx - Kernal's width
	 * @param kernaly - Kernal's height
	 * @param stridex - Stride's width
	 * @param stridey - Stride's height
	 * @param poolingType - Pooling type (min/max/average)
	 * @return
	 */
	public static SubsamplingLayer GenerateLayer(int kernalx, int kernaly, int stridex, 
			int stridey, PoolingType poolingType) {
		SubsamplingLayer.Builder poolingLayer = new SubsamplingLayer.Builder();
		poolingLayer.kernelSize(kernalx, kernaly);
		poolingLayer.stride(stridex, stridey);
		poolingLayer.poolingType(poolingType);
		return poolingLayer.build();
	}
}
