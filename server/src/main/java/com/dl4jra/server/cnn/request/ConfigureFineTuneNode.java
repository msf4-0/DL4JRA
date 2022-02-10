package com.dl4jra.server.cnn.request;

public class ConfigureFineTuneNode extends Nodeclass {
    private int seed;

    public ConfigureFineTuneNode(String nodeId, int seed){
        super(nodeId);
        this.seed = seed;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }
}
