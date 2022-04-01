package com.dl4jra.server.cnn.flowsaving.classes;

public class FLFlowNode {
    /* (SingleTab) Node class */
    private Node node;

    public FLFlowNode() {

    }

    /**
     * Constructor
     * @param node - node data
     */
    public FLFlowNode(Node node) {
        this.node = node;
    }

    /* node getter function */
    public Node getNode() {
        return node;
    }

    /* node setter function */
    public void setNode(Node node) {
        this.node = node;
    }
}
