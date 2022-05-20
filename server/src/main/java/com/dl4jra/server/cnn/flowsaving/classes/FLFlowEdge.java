package com.dl4jra.server.cnn.flowsaving.classes;

public class FLFlowEdge {
    /* (SingleTab) Edge class */
    private Edge edge;

    public FLFlowEdge() {
    }

    /**
     * Constructor
     * @param edge - edge data
     */
    public FLFlowEdge(Edge edge) {
        this.edge = edge;
    }

    /* edge getter function */
    public Edge getEdge() {
        return edge;
    }

    /* edge setter function */
    public void setEdge(Edge edge) {
        this.edge = edge;
    }
}
