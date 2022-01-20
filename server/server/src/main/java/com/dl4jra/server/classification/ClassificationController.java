package com.dl4jra.server.classification;

import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.dl4jra.server.classification.request.Imagedata;
import com.dl4jra.server.classification.request.Modelpath;
import com.dl4jra.server.classification.response.Classificationresult;
import com.dl4jra.server.classification.response.Modelclassnum;
import com.dl4jra.server.globalresponse.Messageresponse;

@Controller
public class ClassificationController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationController.class);
	private Classifier classifier = new Classifier();
	private final ExecutorService classificationexecutor = Executors.newSingleThreadExecutor();
	private Future<Integer> future;

	@PostConstruct
	public void initialization() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			classificationexecutor.shutdown();
	        try 
	        {
	        	classificationexecutor.awaitTermination(1, TimeUnit.SECONDS);
	        } 
	        catch (InterruptedException exception) 
	        {
	        	LOGGER.error(exception.getMessage());
	        }
	    }));
	}
	
	/**
	 * [WEBSOCKET] Reset classifier
	 */
	@MessageMapping("/classification/classifierreset")
	public void resetclassifier() {
		this.classifier.resetclassifier();
	}
	
	
	/**
	 *  [WEBSOCKET] Load modal from path
	 * @param data - Path to classifier
	 * @return Size of output layer (number of classes)
	 */
	@MessageMapping("/classification/modelchanged")
	@SendTo("/response/classification/modelchanged")
	public Modelclassnum loadmodel(Modelpath data) throws Exception {
		int classnum = this.classifier.LoadClassifier(data.getPath());
		return new Modelclassnum(Paths.get(data.getPath()).getFileName().toString(), classnum);
	}
	
	/**
	 * [WEBSOCKET] Classify image/ predict result
	 * @param image - Base64 encoded image data
	 * @return Classificationresult - Classification result (index) of image
	 */
	@MessageMapping("/classification/classify")
	@SendTo("/response/classification/result")
	public Classificationresult classify(Imagedata image) throws Exception {
		this.future = this.classificationexecutor.submit(new ClassifyImage(image.getBase64encodedimage(), 
				this.classifier));
		return new Classificationresult(this.future.get());
	}
	
	/**
	 * [WEBSOCKET] Exception handling
	 * @param exception
	 * @return
	 */
	@MessageExceptionHandler
	@SendTo("/response/classification/error")
	public Messageresponse handleException(Exception exception) {
		LOGGER.error("CLASSIFICATION CONTROLLER MESSAGE EXCEPTION CAUGHT: " + exception.getMessage());
		return new Messageresponse(exception.getMessage());
	}
	
	/**
	 * Thread task
	 */
	private class ClassifyImage implements Callable<Integer> {
		private String encodedimage;
		private Classifier classifier;
		
		public ClassifyImage(String encodedimage, Classifier classifier) {
			this.encodedimage = encodedimage;
			this.classifier = classifier;
		}
		
		public Integer call() throws Exception {
			byte[] imagebytes = Base64.getMimeDecoder().decode(encodedimage);
			Mat mat = Imgcodecs.imdecode(new MatOfByte(imagebytes), Imgcodecs.IMREAD_COLOR);
			return this.classifier.Classify(mat);
		}
	}
}
