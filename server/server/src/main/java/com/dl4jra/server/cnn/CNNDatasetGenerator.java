package com.dl4jra.server.cnn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.api.split.NumberedFileInputSplit;
import org.datavec.image.loader.BaseImageLoader;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.datavec.image.transform.FlipImageTransform;
import org.datavec.image.transform.ImageTransform;
import org.datavec.image.transform.PipelineImageTransform;
import org.datavec.image.transform.ResizeImageTransform;
import org.datavec.image.transform.RotateImageTransform;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

public class CNNDatasetGenerator {
	private ArrayList<Pair<ImageTransform, Double>> transforms = new ArrayList<Pair<ImageTransform, Double>>();

	private int numLabels, batchsize, numClassLabels;
	private int trainPerc = 80;
	private FileSplit filesplit;
	private InputSplit trainData,testData;
	private ImageRecordReader recordReader;
	//Images are of format given by allowedExtension
	private static final String [] allowedExtensions = BaseImageLoader.ALLOWED_FORMATS;
	//Random number generator
	private static final Random rng  = new Random(123);
	//scale input to 0 - 1
	private static DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
	private static ImageTransform transform;
	private int height, width, channels;
	// For CSV loading
	private SequenceRecordReader trainFeatures, trainLabels, testFeatures, testLabels;

	public CNNDatasetGenerator() {
	}

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
	public void LoadDataAutoSplit(String path, int imagewidth, int imageheight, int channels, int numLabels, int batchsize,
						 boolean randomize) throws Exception {

		this.numLabels = numLabels;
		this.batchsize = batchsize;
		this.height = imageheight;
		this.width = imagewidth;
		this.channels = channels;

		ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();

		File parentDir = new File(path);
		this.filesplit = new FileSplit(parentDir, allowedExtensions, rng);
		BalancedPathFilter pathFilter = new BalancedPathFilter(rng, allowedExtensions, labelMaker);

		if (trainPerc >= 100) {
			throw new IllegalArgumentException("Percentage of data set aside for training has to be less than 100%. Test percentage = 100 - training percentage, has to be greater than 0");
		}

		//Split the image files into train and test
		InputSplit[] filesInDirSplit = this.filesplit.sample(pathFilter, trainPerc, 100-trainPerc);
//        this.trainData = filesInDirSplit[0];
//        this.testData = filesInDirSplit[1];
		trainData = filesInDirSplit[0];
        testData = filesInDirSplit[1];
//		this.recordReader = new ImageRecordReader(imagewidth, imageheight, channels, labelMaker);
	}

	public void LoadTrainDataCSV(String path, int numSkipLines, int numClassLabels, int batchsize, char delimeter) throws IOException, InterruptedException {
		this.numClassLabels = numClassLabels;
		this.batchsize = batchsize;
		String locDelimeter = Character.toString(delimeter);

		File trainBaseDir = new File(path);
		File trainFeaturesDir = new File(trainBaseDir, "features");
		File trainLabelsDir = new File(trainBaseDir, "labels");

		trainFeatures = new CSVSequenceRecordReader(numSkipLines, locDelimeter);
		trainFeatures.initialize(new NumberedFileInputSplit( trainFeaturesDir.getAbsolutePath()+ "/%d.csv", 0, 7351));

		trainLabels = new CSVSequenceRecordReader(numSkipLines, locDelimeter);
		trainLabels.initialize(new NumberedFileInputSplit(trainLabelsDir.getAbsolutePath()+"/%d.csv", 0, 7351));
	}

	public void LoadTestDataCSV(String path, int numSkipLines, int numClassLabels, int batchsize, char delimeter) throws IOException, InterruptedException {
		this.numClassLabels = numClassLabels;
		this.batchsize = batchsize;
		String locDelimeter = Character.toString(delimeter);

		File testBaseDir = new File(path);
		File testFeaturesDir = new File(testBaseDir, "features");
		File testLabelsDir = new File(testBaseDir, "labels");

		testFeatures = new CSVSequenceRecordReader(numSkipLines, locDelimeter);
		testFeatures.initialize(new NumberedFileInputSplit( testFeaturesDir.getAbsolutePath()+ "/%d.csv", 0, 2946));

		testLabels = new CSVSequenceRecordReader(numSkipLines, locDelimeter);
		testLabels.initialize(new NumberedFileInputSplit(testLabelsDir.getAbsolutePath()+"/%d.csv", 0, 2946));
	}

	public DataSetIterator trainDataSetIteratorCSV() throws Exception {
		DataSetIterator trainIter = new SequenceRecordReaderDataSetIterator(trainFeatures, trainLabels, batchsize, numClassLabels,
				false, SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_END);
		return trainIter;
	}

	public DataSetIterator testDataSetIteratorCSV() throws Exception {
		DataSetIterator testIter = new SequenceRecordReaderDataSetIterator(testFeatures, testLabels, batchsize, numClassLabels,
				false, SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_END);
		return testIter;
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
//		DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
		scaler.fit(dataIter);
		dataIter.setPreProcessor(scaler);
		return dataIter;
	}

	public DataSetIterator trainIterator() throws Exception {
		return makeIterator(trainData, true);
	}

	public DataSetIterator testIterator() throws Exception {
		return makeIterator(testData, false);
	}


	private DataSetIterator makeIterator(InputSplit split, boolean training) throws Exception {
		ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
		recordReader = new ImageRecordReader(height, width, channels, labelMaker);
		if (training && transform != null){
			recordReader.initialize(split,transform);
		}else{
			recordReader.initialize(split);
		}
		DataSetIterator dataIter = new RecordReaderDataSetIterator(recordReader, batchsize, 1,
				numLabels);
		scaler.fit(dataIter);
		dataIter.setPreProcessor(scaler);
		return dataIter;
	}
}
