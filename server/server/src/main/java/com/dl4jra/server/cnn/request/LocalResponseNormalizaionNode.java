package com.dl4jra.server.cnn.request;

public class LocalResponseNormalizaionNode extends Nodeclass{
        /* Local Response Normalizaion Node data */

        private int ordering;

        public LocalResponseNormalizaionNode() {

        }

        public LocalResponseNormalizaionNode(String nodeId, int ordering) {
            super(nodeId);
            this.ordering = ordering;
        }

        public int getOrdering() {
            return ordering;
        }

        public void setOrdering(int ordering) {
            this.ordering = ordering;
        }
}
