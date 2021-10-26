package com.dl4jra.server.datasetgenerator;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.dl4jra.server.LibraryLoader;
import com.dl4jra.server.datasetgenerator.request.Imagedetails;
import com.dl4jra.server.globalresponse.Messageresponse;

@Controller
public class DGController {
	/* Dataset Generator Controller */
	
	static { LibraryLoader.loadOpencvLibrary(); } 
	private static final Logger LOGGER = LoggerFactory.getLogger(DGController.class);
	// Screenshot executor service (thread)
	private final ExecutorService ssexecutor = Executors.newSingleThreadExecutor();
	private Future<Void> ssfuture;
	
	@PostConstruct
	public void initialization() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			ssexecutor.shutdown();
	        try 
	        {
	        	ssexecutor.awaitTermination(1, TimeUnit.SECONDS);
	        } 
	        catch (InterruptedException exception) 
	        {
	        	LOGGER.error(exception.getMessage());
	        }
	    }));
	}
	
	/* [WEBSOCKET] Save image data to directory */
	@MessageMapping("/screenshot/save")
	@SendTo("/response/screenshot/success")
	public Messageresponse screenshotandsave(Imagedetails image) throws InterruptedException, ExecutionException {
		this.ssfuture =  ssexecutor.submit(new screenshotandsave(image.getDirectory(), image.getFilename(), image.getBase64encodedstring()));
		this.ssfuture.get();
		return new Messageresponse("Screenshot saved successfully");
	}

	/* [WEBSOCKET] Exception handling */
	@MessageExceptionHandler
	@SendTo("/response/screenshot/failed")
	public Messageresponse handleException(Exception exception) {
		LOGGER.error("WS MESSAGE EXCEPTION CAUGHT: " + exception.getMessage());
		return new Messageresponse(exception.getMessage());
	}
	
	// Thread task 
	private class screenshotandsave implements Callable<Void> {
		String directory, filename, base64encodedstring;
		
		public screenshotandsave(String directory, String filename, String base64encodedstring) {
			this.directory = directory;
			this.filename = filename;
			this.base64encodedstring = base64encodedstring;
		}

		@Override
		public Void call() throws Exception {
			Path directorylocation = Paths.get(directory);
			Files.createDirectories(directorylocation);
			byte[] imagebytes = Base64.getMimeDecoder().decode(base64encodedstring);
			ByteArrayInputStream inputstream = new ByteArrayInputStream(imagebytes);
			BufferedImage bufferedImage = ImageIO.read(inputstream);
			ImageIO.write(bufferedImage, "jpg", new File(directory + "/" + filename + ".jpg"));
			return null;
		}
	}
}
