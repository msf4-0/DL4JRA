package com.dl4jra.server.cnn.request;

public class LSTMlayerNode extends Nodeclass{
    private int nIn, nOut;
    private String activationfunction, layerName, layerInput;

    public LSTMlayerNode(){

    }

    public LSTMlayerNode(String nodeId, int nIn, int nOut, String activationfunction, String layerInput, String layerName) {
        super(nodeId);
        this.nIn = nIn;
        this.nOut = nOut;
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
}
