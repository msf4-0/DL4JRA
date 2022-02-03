package com.dl4jra.server.odetection;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.dl4jra.server.LibraryLoader;
import com.dl4jra.server.globalresponse.Messageresponse;
import com.dl4jra.server.odetection.request.Loggingdata;
import com.dl4jra.server.odetection.request.Preprocessedimage;
import com.dl4jra.server.odetection.request.Pretrainedmodelname;
import com.dl4jra.server.odetection.response.Modelconfigurationdata;
import com.dl4jra.server.odetection.response.Processedimage;

@Controller
public class ODController {
	static { LibraryLoader.loadOpencvLibrary(); } 
	private static final Logger LOGGER = LoggerFactory.getLogger(ODController.class);
	private static final String loggingdirectory = "C://DL4JRA/ODLogging";
	private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
	// Object detection executor service (thread)
	private final ExecutorService odexecutor = Executors.newSingleThreadExecutor();
	private Future<Processedimage> odfuture;
	private ODDetector detector = new ODDetector();
	private FileWriter fwritter;
	private boolean logging = false;

	@PostConstruct
	public void initialization() {
		File directorylocation = new File(loggingdirectory);
		if (! directorylocation.exists()) {
			directorylocation.mkdirs();
		}		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			odexecutor.shutdown();
	        try 
	        {
	        	odexecutor.awaitTermination(1, TimeUnit.SECONDS);
	        } 
	        catch (InterruptedException exception) 
	        {
	        	LOGGER.error(exception.getMessage());
	        }
	    }));
	}
	
	/* [WEBSOCKET] Reset model */
	@MessageMapping("/objectdetection/modelreset")
	public void modelreset() throws Exception {
		System.out.println("[ODCONTROLLER] MODEL RESET");
		this.detector.ResetDetector();
	}
	
	/* [WEBSOCKET] Set detector (model) to pretrained model */
	@MessageMapping("/objectdetection/modelonchanged")
	@SendTo("/response/objectdetection/modelchanged")
	public Modelconfigurationdata modelconfiguration(Pretrainedmodelname data) throws Exception {
		System.out.println("===================================");
		System.out.println("===================================");
		System.out.println("===================================");
		System.out.println(data.getModelname());
		System.out.println("===================================");
		System.out.println("===================================");
		System.out.println("===================================");
		this.detector.ResetDetector();
		ODModelConfigurationData modeldata = PMRepository.GetPretrainedModelData(data.getModelname());
		int imagewidth = modeldata.getModelinputwidth();
		int imageheight = modeldata.getModelinputheight();
		int channels = modeldata.getModelinputchannel();
		int gridwidth = modeldata.getGridwidth();
		int gridheight = modeldata.getGridheight();
		this.detector.SetDetectorInputType(imagewidth, imageheight, channels, gridwidth, gridheight);

		// load with zoo model
		if(modeldata.getModelpath() == null){
			this.detector.LoadModelZoo(data.getModelname());
		}
		// load with path
		else{
			this.detector.LoadModel(modeldata.getModelpath());
		}
		this.detector.SetPredictionClasses(modeldata.getClasses());
		System.out.println("[ODCONTROLLER] MODEL CHANGED");
		System.out.println(data.getModelname());
		return new Modelconfigurationdata(data.getModelname(), modeldata.getModelinputwidth());
	}
	
	/* [WEBSOCKET] Signal indicates streaming is started */
	@MessageMapping("/objectdetection/streamingstart")
	public void startdetection(Loggingdata data) throws Exception {
		System.out.println("[ODCONTROLLER] START STREAMING");
		System.out.println(1);
		this.logging = data.isLogging();
		System.out.println(2);
		if (data.isLogging()) {
			System.out.println(3);
			System.out.println("[ODCONTROLLER] STARTING FILESTREAM");
			System.out.println(4);
			this.fwritter = new FileWriter(loggingdirectory + "/" + formatter.format(new Date()).toString() + ".txt");
		}
		
	}
	
	/* [WEBSOCKET] Signal indicates streaming is stopped */
	@MessageMapping("/objectdetection/streamingend")
	public void enddetection() throws Exception {
		System.out.println("[ODCONTROLLER] STOP STREAMING");
		System.out.println(5);
		if (this.fwritter != null) {
			System.out.println(6);
			System.out.println("[ODCONTROLLER] CLOSING FILESTREAM");
			this.fwritter.close();
			this.fwritter = null;
		}
	}
	
	/* [WEBSOCKET] Detect object in image, draw bounding boxes (if any) and return */
	@MessageMapping("/objectdetection/detect")
	@SendTo("/response/objectdetection/processedimage")
	public Processedimage odetection(Preprocessedimage image) throws Exception {
		this.odfuture = this.odexecutor.submit(new detectandreturn(image, this.detector, this.logging, this.fwritter));
		return this.odfuture.get();
	}
	
	/* [WEBSOCKET] Exception handling */
	@MessageExceptionHandler
	@SendTo("/response/objectdetection/error")
	public Messageresponse handleException(Exception exception) {
		System.out.println("[ODCONTROLLER] EXCEPTION CAUGHT: " + exception.getMessage().toUpperCase());
		return new Messageresponse(exception.getMessage());
	}
	
	// Thread task
	private class detectandreturn implements Callable<Processedimage> {
		
		private Preprocessedimage image;
		private ODDetector detector;
		private boolean logging = false;
		private FileWriter fwritter;
		
		public detectandreturn(Preprocessedimage image, ODDetector detector, boolean logging, FileWriter fwritter) {
			this.image = image;
			this.detector = detector;
			this.logging = logging;
			this.fwritter = fwritter;
		}
		
		@Override
		public Processedimage call() throws Exception {
			String encodedstring = image.getBase64encodedstring();
			byte[] imagebytes = Base64.getMimeDecoder().decode(encodedstring);
			Mat mat = Imgcodecs.imdecode(new MatOfByte(imagebytes), Imgcodecs.IMREAD_COLOR);
			Mat outputmat;
			if (! this.logging)
				outputmat = this.detector.DetectandReturn(mat, 0.5);
			else
				outputmat = this.detector.DetectandReturn(mat, 0.5, fwritter);
			
			Imgproc.resize(outputmat, outputmat, new Size(this.image.getOutputwidth(), this.image.getOutputheight()));
			MatOfByte mob = new MatOfByte();
			Imgcodecs.imencode(".jpg", outputmat, mob);
			byte[] outputbytes = mob.toArray();
			String outputstring = "data:image/jpeg;base64," + Base64.getMimeEncoder().encodeToString(outputbytes);
			return new Processedimage(outputstring, outputstring.length());
		}
	}
	
}
