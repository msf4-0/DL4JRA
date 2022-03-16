package com.dl4jra.server.testing;


import com.dl4jra.server.cnn.CNN;
import com.dl4jra.server.cnn.CNNDatasetGenerator;

public class CNNDatasetGeneratorTest {

    public static void main(String[] args) throws Exception {
        String test1 = "";
        String test2 = "";
        String test3 = "";
        CNNDatasetGenerator cnnDatasetGenerator = new CNNDatasetGenerator();
        cnnDatasetGenerator.LoadDataAutoSplit(test3,
                500, 500, 3, 4, 32, false);

    }


}
