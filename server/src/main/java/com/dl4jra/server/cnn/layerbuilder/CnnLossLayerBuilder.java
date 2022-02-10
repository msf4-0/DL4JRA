package com.dl4jra.server.cnn.layerbuilder;

import org.deeplearning4j.nn.conf.layers.CnnLossLayer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class CnnLossLayerBuilder {
    public static CnnLossLayer GenerateLayer(LossFunctions.LossFunction lossFunction, Activation activationfunction)
    {
        CnnLossLayer.Builder builder = new CnnLossLayer.Builder();
        if(lossFunction != null){
            builder.lossFunction(lossFunction);
        }
        if(activationfunction != null) {
            builder.activation(activationfunction);
        }
        return builder.build();
    }
}
