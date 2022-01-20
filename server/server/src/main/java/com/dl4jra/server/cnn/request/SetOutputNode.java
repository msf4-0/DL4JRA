package com.dl4jra.server.cnn.request;

public class SetOutputNode extends Nodeclass{
    private String outputName;

    public SetOutputNode(String nodeId, String outputName) {
        super(nodeId);
        this.outputName = outputName;
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }
}
