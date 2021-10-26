package com.dl4jra.server.cnn;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.datavec.image.transform.FlipImageTransform;
import org.datavec.image.transform.ImageTransform;
import org.datavec.image.transform.PipelineImageTransform;
import org.datavec.image.transform.ResizeImageTransform;
import org.datavec.image.transform.RotateImageTransform;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

public class CNNDatasetGenerator {
	private ArrayList<Pair<ImageTransform, Double>> transforms = new ArrayList<Pair<ImageTransform, Double>>();

	private int numLabels, batchsize;
	private FileSplit filesplit;
	private ImageRecordReader recordReader;

	/**
	 * Load image dataset
	 * @param path - Path to image dataset
	 * @param imagewidth - Width of image dataset 
	 * @param imageheight - Height of image dataset
	 * @param channels - Channel of image dataset
	 * @param numLabels - Number of labels
	 * @param batchsize - Iterator batch size
	 * @param randomize - Randomize dataset ordering
	 * @throws Exception
	 */
	public void LoadData(String path, int imagewidth, int imageheight, int channels, int numLabels, int batchsize,
			boolean randomize) throws Exception {

		this.numLabels = numLabels;
		this.batchsize = batchsize;

		Random random = new Random(System.currentTimeMillis());
		File mainpath = new File(path);
		if (!mainpath.exists() || !mainpath.isDirectory())
			throw new Exception("Training directory not found");
		if (randomize)
			this.filesplit = new FileSplit(mainpath, NativeImageLoader.ALLOWED_FORMATS, random);
		else
			this.filesplit = new FileSplit(mainpath, NativeImageLoader.ALLOWED_FORMATS);

		ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
		this.recordReader = new ImageRecordReader(imagewidth, imageheight, channels, labelMaker);
	}

	/**
	 * Add FlipImageTransform to dataset
	 * @param flipmode (0 - Flips around x axis, > 0 - Flips around y-axis, < 0 - Flips around both axes)
	 * @throws Exception
	 */
	public void FlipImage(int flipmode) throws Exception {
		transforms.add(new Pair<>(new FlipImageTransform(flipmode), 1.0));
	}

	/**
	 * Add ResizeImageTransform to dataset
	 * @param newwidth
	 * @param newheight
	 * @throws Exception
	 */
	public void ResizeImage(int newwidth, int newheight) throws Exception {
		transforms.add(new Pair<>(new ResizeImageTransform(newwidth, newheight), 1.0));
	}

	/**
	 * Add RotateImageTransform to dataset
	 * @param angle - Angle of rotation
	 * @throws Exception
	 */
	public void RotateImage(float angle) throws Exception {
		transforms.add(new Pair<>(new RotateImageTransform(angle), 1.0));
	}

	/**
	 * Generate dataset iterator
	 * @return DataSetIterator object
	 * @throws Exception
	 */
	public DataSetIterator GetDatasetIterator() throws Exception {
		if (this.filesplit == null || this.recordReader == null || this.numLabels == 0 || this.batchsize == 0)
			throw new Exception("Dataset has not been loaded");
		this.recordReader.initialize(this.filesplit, new PipelineImageTransform(transforms, false));
		DataSetIterator dataIter = new RecordReaderDataSetIterator(this.recordReader, this.batchsize, 1,
				this.numLabels);
		DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
		scaler.fit(dataIter);
		dataIter.setPreProcessor(scaler);
		return dataIter;
	}
}
