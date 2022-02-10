package com.dl4jra.server.cnn.request;

import org.deeplearning4j.nn.weights.WeightInit;

public class ConfigureTransferLearningNode extends Nodeclass{
    private String featurizeExtractionLayer ,vertexName, nInName, nOutName, nInWeightInit, nOutWeightInit;
    private int nIn, nOut;
    public ConfigureTransferLearningNode(String nodeId, String featurizeExtractionLayer, String vertexName,
                                         String nInName, String nOutName, String nInWeightInit, String nOutWeightInit,
                                         int nIn, int nOut) {
        super(nodeId);
        this.featurizeExtractionLayer = featurizeExtractionLayer;
        this.vertexName = vertexName;
        this.nInName = nInName;
        this.nIn = nIn;
        this.nInWeightInit = nInWeightInit;
        this.nOutName = nOutName;
        this.nOut = nOut;
        this.nOutWeightInit = nOutWeightInit;
    }

    public String getFeaturizeExtractionLayer() {
        return featurizeExtractionLayer;
    }

    public void setFeaturizeExtractionLayer(String featurizeExtractionLayer) {
        this.featurizeExtractionLayer = featurizeExtractionLayer;
    }

    public String getVertexName() {
        return vertexName;
    }

    public void setVertexName(String vertexName) {
        this.vertexName = vertexName;
    }

    public String getnInName() {
        return nInName;
    }

    public void setnInName(String nInName) {
        this.nInName = nInName;
    }

    public String getnOutName() {
        return nOutName;
    }

    public void setnOutName(String nOutName) {
        this.nOutName = nOutName;
    }

    public String getnInWeightInit() {
        return nInWeightInit;
    }

    public void setnInWeightInit(String nInWeightInit) {
        this.nInWeightInit = nInWeightInit;
    }

    public String getnOutWeightInit() {
        return nOutWeightInit;
    }

    public void setnOutWeightInit(String nOutWeightInit) {
        this.nOutWeightInit = nOutWeightInit;
    }

    public int getnIn() {
        return nIn;
    }

    public void setnIn(int nIn) {
        this.nIn = nIn;
    }

    public int getnOut() {
        return nOut;
    }

    public void setnOut(int nOut) {
        this.nOut = nOut;
    }
}
