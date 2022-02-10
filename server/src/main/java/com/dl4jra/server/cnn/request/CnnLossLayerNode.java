package com.dl4jra.server.cnn.request;

public class CnnLossLayerNode extends Nodeclass{
    private String activationfunction, lossfunction, layerName, layerInput;

    public CnnLossLayerNode(String nodeId, String activationfunction, String lossFunction, String layerName, String layerInput){
        super(nodeId);
        this.layerName = layerName;
        this.layerInput = layerInput;
        this.activationfunction = activationfunction;
        this.lossfunction = lossFunction;
    }

    public String getActivationfunction() {
        return activationfunction;
    }

    public void setActivationfunction(String activationfunction) {
        this.activationfunction = activationfunction;
    }

    public String getLossfunction() {
        return lossfunction;
    }

    public void setLossfunction(String lossfunction) {
        this.lossfunction = lossfunction;
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
