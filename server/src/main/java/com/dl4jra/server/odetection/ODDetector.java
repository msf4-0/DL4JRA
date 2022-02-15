package com.dl4jra.server.odetection;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.OutputLayer;
import org.deeplearning4j.nn.layers.objdetect.DetectedObject;
import org.deeplearning4j.nn.layers.objdetect.Yolo2OutputLayer;
import org.deeplearning4j.util.ModelSerializer;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.Darknet19;
import org.deeplearning4j.zoo.model.ResNet50;
import org.deeplearning4j.zoo.model.TinyYOLO;
import org.deeplearning4j.zoo.model.YOLO2;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.dl4jra.server.LibraryLoader;

public class ODDetector {
	/* Detector class */

	static { LibraryLoader.loadOpencvLibrary(); }
	private int imagewidth, imageheight, gridwidth, gridheight;
	private String currentModelName;
	private ComputationGraph DetectionModel;
	private NativeImageLoader loader;
	private ImagePreProcessingScaler scaler = new ImagePreProcessingScaler(0, 1);
	private ArrayList<String> classes;
	private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
    
	/* Reset detector */
	public void ResetDetector() {
		this.imagewidth = 0;
		this.imageheight = 0;
		this.gridwidth = 0;
		this.gridheight = 0;
		this.classes = null;
		this.loader = null;
		this.DetectionModel = null;
	}
	
	/**
	 * Detector's configuration
	 * @param imagewidth - Image's width (input)
	 * @param imageheight - Image's height (input)
	 * @param channels - Image's color channel
	 * @param gridwidth - Detector's grid width
	 * @param gridheight - Detector's grid height
	 */
	public void SetDetectorInputType(int imagewidth, int imageheight, int channels, int gridwidth, int gridheight) {
		this.imagewidth = imagewidth;
		this.imageheight = imageheight;
		this.gridwidth = gridwidth;
		this.gridheight = gridheight;
		loader = new NativeImageLoader(imagewidth, imageheight, channels);
	}
	
	/**
	 * Check if detector's input type is configured
	 * @return True if detector's input type is set
	 */
	public boolean DetectorInputTypeIsSet() {
		return this.imagewidth != 0 && this.imageheight != 0 && this.gridwidth != 0 && this.gridheight != 0;
	}

	/**
	 * Load object detection model
	 * @param path - Path to model
	 * @throws Exception
	 */
	public void LoadModel(String path) throws Exception {
		File location = new File(path);
    	boolean modalexists = location.exists() && ! location.isDirectory();
    	if (! modalexists)
    		throw new Exception("Invalid path (modal not found)");
    	this.DetectionModel = ModelSerializer.restoreComputationGraph(location);
	}

	public void LoadModelZoo(String modelName) throws Exception {
		ZooModel zooModel;
		currentModelName = modelName;
		if(modelName.equals("yolo2")){
			zooModel = YOLO2.builder().numClasses(0).build();
			this.DetectionModel = (ComputationGraph) zooModel.initPretrained();
		}
	}

	
	/**
	 * Check if od model is loaded
	 * @return True if model is loaded, else false
	 */
	public boolean ModelIsLoaded() {
		return this.DetectionModel != null;
	}
	
	/**
	 * Set the label of classes
	 * @param classes - classes' label
	 */
	public void SetPredictionClasses(ArrayList<String> classes) {
		this.classes = classes;
	}
	
	/**
	 * Check if classes' label are set
	 * @return True if label(s) are set, else false
	 */
	public boolean PredictionClassesIsSet() {
		return this.classes != null;
	}
	
	/**
	 * Detect object and save image (ONLY if something is detected)
	 * @param image - Image data for detection
	 * @param threshold - Detection threshold
	 * @param directory - Directory to save image
	 * @param filename - Filename of image
	 * @throws Exception
	 */
	public void DetectandSave(Mat image, double threshold, String directory, String filename) throws Exception {
		if (! this.ModelIsReady())
			throw new Exception("Model is Loaded: " + this.ModelIsLoaded() + "\nInputType is set: " + this.DetectorInputTypeIsSet() + 
					"\nClasses is set: " + this.PredictionClassesIsSet());
		Imgproc.resize(image, image, new Size(imagewidth, imageheight));
		List<DetectedObject> objects = GetObjects(image, threshold);
		if (objects.size() > 0) {
			image = DrawBoxes(image, objects);
			SaveImage(image, directory, filename);
		}
	}
	
	/**
	 * Detect image, draw bounding box(es) and return
	 * @param image - Image data for detection
	 * @param threshold - Detection threshold
	 * @return Processed image data
	 * @throws Exception
	 */
	public Mat DetectandReturn(Mat image, double threshold) throws Exception {
		if (! this.ModelIsReady())
			throw new Exception("Model is Loaded: " + this.ModelIsLoaded() + "\nInputType is set: " + this.DetectorInputTypeIsSet() + 
					"\nClasses is set: " + this.PredictionClassesIsSet());
		Imgproc.resize(image, image, new Size(imagewidth, imageheight));
		List<DetectedObject> objects = GetObjects(image, threshold);
		if (objects.size() > 0)
			image = DrawBoxes(image, objects);
		return image;
	}
	
	/**
	 * Detect image, draw bounding box(es), write detection result to textfile and return
	 * @param image - Image data for detection
	 * @param threshold - Detection threshold
	 * @param fwritter - FileWriter 
	 * @return Processed image data
	 * @throws Exception
	 */
	public Mat DetectandReturn(Mat image, double threshold, FileWriter fwritter) throws Exception {
		if (! this.ModelIsReady())
			throw new Exception("Model is Loaded: " + this.ModelIsLoaded() + "\nInputType is set: " + this.DetectorInputTypeIsSet() + 
					"\nClasses is set: " + this.PredictionClassesIsSet());
		Imgproc.resize(image, image, new Size(imagewidth, imageheight));
		List<DetectedObject> objects = GetObjects(image, threshold);
		if (objects.size() > 0) {
			image = DrawBoxes(image, objects, fwritter);
		}
		return image;
	}
	
	/**
	 * Get objects in image
	 * @param threshold - Detection threshold
	 * @return DetectedObject in image
	 * @throws Exception
	 */
	public List<DetectedObject> GetObjects(Mat inputimage, double threshold) throws Exception {
		if (! this.ModelIsReady())
			throw new Exception("Detection model is not configured");
		Yolo2OutputLayer yout = (Yolo2OutputLayer) this.DetectionModel.getOutputLayer(0);
		INDArray ds = this.loader.asMatrix(inputimage);
		this.scaler.transform(ds);
		INDArray results = this.DetectionModel.outputSingle(ds);
		List<DetectedObject> objects = NonMaxSuppression.getObjects(yout.getPredictedObjects(results, threshold));
		return objects;
	}
	
	/**
	 * Draw bounding box around detected object
	 * @param image - Output image
	 * @param objects - Detected objects
	 * @return Processed image
	 * @throws Exception
	 */
	public Mat DrawBoxes(Mat image, List<DetectedObject> objects) throws Exception{
		for (DetectedObject obj : objects) {
            double[] xy1 = obj.getTopLeftXY();
            double[] xy2 = obj.getBottomRightXY();
            int predictedclass = GetLargestProbabilityIndex(obj.getClassPredictions());
            int x1 = (int) Math.max(0, Math.round(imagewidth * xy1[0] / gridwidth));
            int y1 = (int) Math.max(0, Math.round(imageheight * xy1[1] / gridheight));
            int x2 = (int) Math.min(imagewidth, Math.round(imagewidth * xy2[0] / gridwidth));
            int y2 = (int) Math.min(imageheight, Math.round(imageheight * xy2[1] / gridheight));
            Imgproc.rectangle(image, new Point(x1, y1), new Point(x2, y2), new Scalar(0, 0, 255), 2);
            if (this.classes != null) {
            	if (predictedclass < this.classes.size()) {
            		Imgproc.putText(image, this.classes.get(predictedclass), new Point(x1 + 2, y2 - 2), 1, .8, new Scalar(0, 0, 255));
            	}
            }
    	}
		return image;
	}
	
	/**
	 * Draw bounding box around detected object and log to file
	 * @param image - Output image
	 * @param objects - Detected objects
	 * @param fwritter - FileWriter
	 * @return Processed image
	 * @throws Exception
	 */
	public Mat DrawBoxes(Mat image, List<DetectedObject> objects, FileWriter fwritter) throws Exception{
		String outputstring = formatter.format(new Date()).toString() + "\n";
		for (DetectedObject obj : objects) {
            double[] xy1 = obj.getTopLeftXY();
            double[] xy2 = obj.getBottomRightXY();
            int predictedclass = GetLargestProbabilityIndex(obj.getClassPredictions());
            int x1 = (int) Math.max(0, Math.round(imagewidth * xy1[0] / gridwidth));
            int y1 = (int) Math.max(0, Math.round(imageheight * xy1[1] / gridheight));
            int x2 = (int) Math.min(imagewidth, Math.round(imagewidth * xy2[0] / gridwidth));
            int y2 = (int) Math.min(imageheight, Math.round(imageheight * xy2[1] / gridheight));
            Imgproc.rectangle(image, new Point(x1, y1), new Point(x2, y2), new Scalar(0, 0, 255), 2);
            if (this.classes != null) {
            	if (predictedclass < this.classes.size()) {
            		Imgproc.putText(image, this.classes.get(predictedclass), new Point(x1 + 2, y2 - 2), 1, .8, new Scalar(0, 0, 255));
            		outputstring += String.format("%s DETECTED (%.3f)\n", this.classes.get(predictedclass), obj.getClassPredictions().getFloat(predictedclass));
            	}
            }
    	}
		outputstring += "------------------------------------------------\n\n";
		fwritter.write(outputstring);
		return image;
	}
	
	/**
	 * Get the index of the largest value (highest probability)
	 * @param probabilities - Array of probabilities
	 * @return Index of the largest value
	 * @throws Exception
	 */
	private int GetLargestProbabilityIndex(INDArray probabilities) throws Exception {
		float largestval = 0;
		int largestvalindex = 0;
		for(int index = 0; index < probabilities.size(0); index++) {
			if (probabilities.getFloat(index) > largestval) {
				largestvalindex = index;
				largestval = probabilities.getFloat(index);
			}
		}
		return largestvalindex;
	}
	
	/* Save image to directory */
	private void SaveImage(Mat image, String directory, String filename) throws Exception{
		File location = new File(directory);
		if (! location.exists() || ! location.isDirectory())
			throw new Exception("Directory does not exist");
		Imgcodecs.imwrite(directory + "/" + filename + ".jpg", image);
	}
	
	/**
	 * Check detection model is ready to use. Condition:
	 * 1. Model is loaded
	 * 2. Detector's input is set
	 * 3. Prediction classes' label(s) is set
	 * @return True if all three conditions are met, else false
	 */
	private boolean ModelIsReady() {
		return this.ModelIsLoaded() && this.DetectorInputTypeIsSet() && this.PredictionClassesIsSet();
	}
}

