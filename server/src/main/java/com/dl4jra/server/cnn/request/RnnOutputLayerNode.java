package com.dl4jra.server.cnn.request;

public class RnnOutputLayerNode extends Nodeclass{
    private int nIn, nOut;
    private String RNNFormat, lossfunction, activationfunction, layerName, layerInput;

    public RnnOutputLayerNode(String nodeId, String RNNFormat, int nIn, int nOut,
                              String lossfunction, String activationfunction, String layerName, String layerInput) {
        super(nodeId);
        this.nIn = nIn;
        this.nOut = nOut;
        this.RNNFormat = RNNFormat;
        this.lossfunction = lossfunction;
        this.activationfunction = activationfunction;
        this.layerInput = layerInput;
        this.layerName = layerName;
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

    public String getLossfunction() {
        return lossfunction;
    }

    public void setLossfunction(String lossfunction) {
        this.lossfunction = lossfunction;
    }

    public String getActivationfunction() {
        return activationfunction;
    }

    public void setActivationfunction(String activationfunction) {
        this.activationfunction = activationfunction;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public String getLayerInput() {
        return layerInput;
    }

    public void setLayerInput(String layerInput) {
        this.layerInput = layerInput;
    }

    public String getRNNFormat() {
        return RNNFormat;
    }

    public void setRNNFormat(String RNNFormat) {
        this.RNNFormat = RNNFormat;
    }
}
