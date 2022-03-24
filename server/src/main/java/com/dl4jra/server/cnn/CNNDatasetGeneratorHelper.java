package com.dl4jra.server.cnn;

import com.dl4jra.server.cnn.request.SetOutputNode;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import static java.lang.Math.floor;

/**
 * Class containing helper methods for CNNDatasetGenerator
 * TODO: Write junit tests for the file
 */
@Component
public class CNNDatasetGeneratorHelper {

    /**
     * Function to check if the dataset and the trainPerc given by the user will lead to a test and
     * train mismatch. That would occur if there is one labeled folder that has few enough samples
     * that after dividing the dataset between the test and train subsets, the test subset wouldnt have
     * a sample from all labels, leading to a validation error because of the mismatch between the
     * number of labels in the test and train iterators.
     *
     * @param parentDir
     * @param trainPerc
     * @return false if there are insufficient samples, true otherwise
     */
    static boolean DatasetLabelBalanceVerifier(File parentDir, Integer trainPerc, String[] allowedExtensions) {
        int numLabels;
        int lowerPercentage;
        int totalFiles;
        int minFolderSize = Integer.MAX_VALUE;

        File[] directories = parentDir.listFiles(File::isDirectory);

        // Section to find the minimum folder size
        assert directories != null;
        numLabels = directories.length;

        for (File directory : directories) {

            // Check if the current thread is interrupted, if so, break the loop.
            if (Thread.currentThread().isInterrupted()) {
                break;
            }

            int currentFolderSize = 0;
            for (String aFile : Objects.requireNonNull(directory.list())) {
                if (Arrays.stream(allowedExtensions).anyMatch(aFile::endsWith)) {
                    currentFolderSize++;
                }
            }
            minFolderSize = Math.min(minFolderSize, currentFolderSize);
        }

        // Section to calculate the number of files left over after random pruning by BalancedPathFilter
        totalFiles = minFolderSize * numLabels;
        // check if the folder with the lowest percentage of the dataset has at least the same number of samples
        // as the number of labels
        lowerPercentage = trainPerc > 50 ? 100 - trainPerc : trainPerc;

        return floor((totalFiles * lowerPercentage) / 100) >= numLabels;
    }
}
