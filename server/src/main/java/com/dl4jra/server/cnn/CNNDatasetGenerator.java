package com.dl4jra.server.cnn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.dl4jra.server.cnn.response.UpdateResponse;
import com.dl4jra.server.cnn.utilities.Visualization;
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
import org.datavec.image.transform.*;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.common.primitives.Pair;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.slf4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.swing.*;

import static org.bytedeco.opencv.global.opencv_imgproc.CV_RGB2GRAY;

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
	// Segmentation
	private static CustomLabelGenerator labelMaker;
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(
			CNNDatasetGenerator.class);


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
		trainData = filesInDirSplit[0];
        testData = filesInDirSplit[1];
	}

	public void setIterator_segmentation(String path, int batchSize, double trainPerc, int imagewidth, int imageheight,
										 int channels, String maskFileName){
		// set transform
		ImageTransform rgb2gray = new ColorConversionTransform(CV_RGB2GRAY);

		List<Pair<ImageTransform, Double>> pipeline = Arrays.asList(
				new Pair<>(rgb2gray, 1.0)
		);
		transform =  new PipelineImageTransform(pipeline, false);

		batchsize = batchSize;
		height = imageheight;
		width = imagewidth;
		this.channels = channels;

		File imagesPath = new File(path);
		FileSplit imageFileSplit = new FileSplit(imagesPath, NativeImageLoader.ALLOWED_FORMATS, new Random(12345));

		List<Pair<String, String>> replacement = Arrays.asList(
				new Pair<>("inputs", maskFileName),
				new Pair<>(".jpg", "_mask.png")
		);

		labelMaker = new CustomLabelGenerator(imageheight, imagewidth, channels, replacement);
		BalancedPathFilter imageSplitPathFilter = new BalancedPathFilter(new Random(12345), NativeImageLoader.ALLOWED_FORMATS, labelMaker);
		InputSplit[] imagesSplits = imageFileSplit.sample(imageSplitPathFilter, trainPerc, 1 - trainPerc);

		trainData = imagesSplits[0];
		testData = imagesSplits[1];
	}

	public void LoadTrainDataCSV(String path, int numSkipLines, int numClassLabels, int batchsize) throws IOException, InterruptedException {
		this.numClassLabels = numClassLabels;
		this.batchsize = batchsize;

		File trainBaseDir = new File(path);
		File trainFeaturesDir = new File(trainBaseDir, "features");
		File trainLabelsDir = new File(trainBaseDir, "labels");

		trainFeatures = new CSVSequenceRecordReader(numSkipLines, ",");
		trainFeatures.initialize(new NumberedFileInputSplit( trainFeaturesDir.getAbsolutePath()+ "/%d.csv", 0, 7351));

		trainLabels = new CSVSequenceRecordReader(numSkipLines, ",");
		trainLabels.initialize(new NumberedFileInputSplit(trainLabelsDir.getAbsolutePath()+"/%d.csv", 0, 7351));
	}

	public void LoadTestDataCSV(String path, int numSkipLines, int numClassLabels, int batchsize) throws IOException, InterruptedException {
		this.numClassLabels = numClassLabels;
		this.batchsize = batchsize;

		File testBaseDir = new File(path);
		File testFeaturesDir = new File(testBaseDir, "features");
		File testLabelsDir = new File(testBaseDir, "labels");

		testFeatures = new CSVSequenceRecordReader(numSkipLines, ",");
		testFeatures.initialize(new NumberedFileInputSplit( testFeaturesDir.getAbsolutePath()+ "/%d.csv", 0, 2946));

		testLabels = new CSVSequenceRecordReader(numSkipLines, ",");
		testLabels.initialize(new NumberedFileInputSplit(testLabelsDir.getAbsolutePath()+"/%d.csv", 0, 2946));
	}

	public DataSetIterator trainDataSetIteratorCSV(){
		return new SequenceRecordReaderDataSetIterator(trainFeatures, trainLabels, batchsize, numClassLabels,
				false, SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_END);
	}

	public DataSetIterator testDataSetIteratorCSV(){
		return new SequenceRecordReaderDataSetIterator(testFeatures, testLabels, batchsize, numClassLabels,
				false, SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_END);
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

	public RecordReaderDataSetIterator trainIterator_segmentation() throws Exception {
		return makeIterator_segmentation(trainData);
	}

	public RecordReaderDataSetIterator testIterator_segmentation() throws Exception {
		return makeIterator_segmentation(testData);
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

	private RecordReaderDataSetIterator makeIterator_segmentation(InputSplit split) throws Exception {
		recordReader = new ImageRecordReader(height, width, channels, labelMaker);

		recordReader.initialize(split, transform);
		RecordReaderDataSetIterator dataIter = new RecordReaderDataSetIterator(recordReader, batchsize, 1, 1, true);
		dataIter.setPreProcessor(scaler);

		return dataIter;
	}


	public void train_segmentation(int epoch, RecordReaderDataSetIterator trainGenerator, ComputationGraph model){
		//visualize
//		JFrame frame = Visualization.initFrame("Viz");
//		System.out.println(10);
//		JPanel panel = Visualization.initPanel(
//				frame,
//				batchsize,
//				height,
//				width,
//				1
//		);

		System.out.println(2);
		System.out.println(epoch);

		for (int i = 0; i < epoch; i++) {

			log.info("Epoch: " + i);

			while (trainGenerator.hasNext()) {
				DataSet imageSet = trainGenerator.next();

				model.fit(imageSet);

//				INDArray predict = model.output(imageSet.getFeatures())[0];

//				Visualization.visualize(
//						imageSet.getFeatures(),
//						imageSet.getLabels(),
//						predict,
//						frame,
//						panel,
//						batchsize,
//						224,
//						224
//				);
			}

			trainGenerator.reset();
		}
	}


	public void validation_segmentation(RecordReaderDataSetIterator validationGenerator, ComputationGraph model) throws IOException {
		Evaluation eval = new Evaluation(2);

		float IOUTotal = 0;
		int count = 0;
		while (validationGenerator.hasNext()) {
			DataSet imageSetVal = validationGenerator.next();

			INDArray predictVal = model.output(imageSetVal.getFeatures())[0];
			INDArray labels = imageSetVal.getLabels();

			count++;

			eval.eval(labels, predictVal);
			log.info(eval.stats());

			//Intersection over Union:  TP / (TP + FN + FP)
			float IOU = (float) eval.truePositives().get(1) / ((float) eval.truePositives().get(1) + (float) eval.falsePositives().get(1) + (float) eval.falseNegatives().get(1));
			IOUTotal = IOUTotal + IOU;

			System.out.println("IOU Cell " + String.format("%.3f", IOU));

			eval.reset();


		}

		System.out.println("Mean IOU: " + IOUTotal / count);
	}

}
