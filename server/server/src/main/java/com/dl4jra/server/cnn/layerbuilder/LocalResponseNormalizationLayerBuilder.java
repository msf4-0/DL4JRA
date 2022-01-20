package com.dl4jra.server.cnn.layerbuilder;

import org.deeplearning4j.nn.conf.layers.LocalResponseNormalization;

public class LocalResponseNormalizationLayerBuilder {
    public static LocalResponseNormalization GenerateLayer() {
        LocalResponseNormalization.Builder localResponseNormalizationLayer = new LocalResponseNormalization.Builder();
        return localResponseNormalizationLayer.build();
    }
}
