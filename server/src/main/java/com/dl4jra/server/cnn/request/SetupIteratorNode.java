package com.dl4jra.server.cnn.request;

public class SetupIteratorNode extends Nodeclass {
    private String path, maskFolderName;
    private int imagewidth, imageheight, channels, batchsize;
    private double trainPerc;

    public SetupIteratorNode(String nodeId, String path, int imagewidth, int imageheight, int channels, int batchsize, int numLabels,
                           String maskFolderName, double trainPerc) {
        super(nodeId);
        this.path = path;
        this.imagewidth = imagewidth;
        this.imageheight = imageheight;
        this.channels = channels;
        this.batchsize = batchsize;
        this.maskFolderName = maskFolderName;
        this.trainPerc = trainPerc;
    }

    @Override
    public String toString() {
        return String.format("Loading dataset from : %s\nDimension: %d x %d x %d\nBatchsize: %d\nTrainPerc: %f\n" +
                        "Mask Foldler Name: %s",
                this.path, this.imagewidth, this.imageheight, this.channels, this.batchsize, this.trainPerc, this.maskFolderName);
    }

    public double getTrainPerc() {
        return trainPerc;
    }

    public void setTrainPerc(double trainPerc) {
        this.trainPerc = trainPerc;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMaskFolderName() {
        return maskFolderName;
    }

    public void setMaskFolderName(String maskFolderName) {
        this.maskFolderName = maskFolderName;
    }

    public int getImagewidth() {
        return imagewidth;
    }

    public void setImagewidth(int imagewidth) {
        this.imagewidth = imagewidth;
    }

    public int getImageheight() {
        return imageheight;
    }

    public void setImageheight(int imageheight) {
        this.imageheight = imageheight;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public int getBatchsize() {
        return batchsize;
    }

    public void setBatchsize(int batchsize) {
        this.batchsize = batchsize;
    }
}
