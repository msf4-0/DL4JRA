package com.dl4jra.server.cnn.layerbuilder;

import org.deeplearning4j.nn.conf.layers.LSTM;
import org.nd4j.linalg.activations.Activation;

public class LSTMLayerBuilder {

    public static LSTM GenerateLayer(int nIn, int nOut, Activation activationfunction)
    {
        LSTM.Builder LSTM = new LSTM.Builder();
        LSTM.nIn(nIn);
        LSTM.nOut(nOut);
        if(activationfunction != null) {
            LSTM.activation(activationfunction);
        }
        return LSTM.build();
    }
}
