package com.dl4jra.server.cnn.layerbuilder;

import org.deeplearning4j.nn.conf.RNNFormat;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

public class RnnOutputLayerBuilder {
    public static RnnOutputLayer GenerateLayer(RNNFormat rnnFormat, int nIn, int nOut, LossFunction lossfunction,
                                               Activation activationfunction)
    {
        RnnOutputLayer.Builder rnnOutputLayer = new RnnOutputLayer.Builder();
        rnnOutputLayer.dataFormat(rnnFormat);
        rnnOutputLayer.nIn(nIn);
        rnnOutputLayer.nOut(nOut);
        rnnOutputLayer.lossFunction(lossfunction);
        if(activationfunction != null) {
            rnnOutputLayer.activation(activationfunction);
        }
        return rnnOutputLayer.build();
    }
}
