package com.dl4jra.server.cnn;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.datavec.image.loader.BaseImageLoader;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CNNDatasetGeneratorHelperTest{
    @TempDir
    Path parentFolder;

    /**
     * Test to ensure DatasetGeneratorHelper correctly identifies if there will be sufficient samples
     * to ensure that there is not a mismatch between the test and train iterators
     * Step1: Create 2 temp folders and populate them as follows:
     *
     *  parentFolder1
     *  --label1
     *      -> 5 images
     *  --label2
     *      -> 5 Images
     *
     *  parentFolder2
     *  --label3
     *      -> 5 images
     *  --label4
     *      -> 3 images
     *
     *  Dataset balance iterator will be ran on both of them with trainpercentage = 80
     * @throws IOException
     */
    @Test
    public void DatasetLabelBalanceVerifierTest() throws IOException {
        // get allowed formats
        String[] allowedExtensions = BaseImageLoader.ALLOWED_FORMATS;

        // creation of temporary folders
        File label1 = parentFolder.resolve("parentFolder1/label1").toFile();
        label1.mkdirs();
        File label2 = parentFolder.resolve("parentFolder1/label2").toFile();
        label2.mkdirs();
        File label3 = parentFolder.resolve("parentFolder2/label3").toFile();
        label3.mkdirs();
        File label4 = parentFolder.resolve("parentFolder2/label4").toFile();
        label4.mkdirs();

        File output;

        // creation of temp images
        for (int i = 0; i < 5; i++) {
            output = new File(label1, "image" + i + ".jpg");
            output.createNewFile();
            output = new File(label2, "image" + i + ".jpg");
            output.createNewFile();
            output = new File(label3, "image" + i + ".jpg");
            output.createNewFile();
        }

        // 3 images in the last folder
        for (int i = 0; i < 3; i++) {
            output = new File(label4, "image" + i + ".jpg");
            output.createNewFile();
        }

        Assertions.assertTrue(CNNDatasetGeneratorHelper.DatasetLabelBalanceVerifier(parentFolder.resolve("parentFolder1").toFile(), 80, allowedExtensions));
        Assertions.assertFalse(CNNDatasetGeneratorHelper.DatasetLabelBalanceVerifier(parentFolder.resolve("parentFolder2").toFile(), 80, allowedExtensions));
    }
}
