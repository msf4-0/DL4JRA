package com.dl4jra.server.odetection;

import java.io.File;
import java.util.Random;

import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.objdetect.ObjectDetectionRecordReader;
import org.datavec.image.recordreader.objdetect.impl.VocLabelProvider;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

public class ODDatasetGenerator {
	
	/**
	 * Generate Object Detection Dataset
	 * @param path - Path to image dataset
	 * @param imagewidth - Width of image dataset 
	 * @param imageheight - Height of image dataset
	 * @param channels - Channel of image dataset
	 * @param gridheight - Detector's grid height
	 * @param gridwidth - Detector's grid width
	 * @param batchsize - Iterator batch size
	 * @return DataSetIterator
	 * @throws Exception
	 */
	public static DataSetIterator LoadData(String path, int imagewidth, int imageheight, int channels, int gridheight,
			int gridwidth, int batchsize) throws Exception {
		Random random = new Random(System.currentTimeMillis());
		File location = new File(path);
		boolean directoryexists = location.exists() && location.isDirectory();
		if (!directoryexists)
			throw new Exception("Training directory does not exist");
		FileSplit data = new FileSplit(location, NativeImageLoader.ALLOWED_FORMATS, random);
		ObjectDetectionRecordReader recordreader = new ObjectDetectionRecordReader(imagewidth, imageheight, channels,
				gridheight, gridwidth, new VocLabelProvider(path));
		recordreader.initialize(data);

		DataSetIterator datasetiterator = new RecordReaderDataSetIterator(recordreader, batchsize, 1, 1, true);
		datasetiterator.setPreProcessor(new ImagePreProcessingScaler(0, 1));
		return datasetiterator;
	}
}
