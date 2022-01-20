package com.dl4jra.server.cnn.request;

public class AddInputNode extends Nodeclass {
    private String inputName;

    public AddInputNode(String nodeId, String inputName) {
        super(nodeId);
        this.inputName = inputName;
    }

    public String getInputName() {
        return inputName;
    }

    public void setInputName(String inputName) {
        this.inputName = inputName;
    }
}
