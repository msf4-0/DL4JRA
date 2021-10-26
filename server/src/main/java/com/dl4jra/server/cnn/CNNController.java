package com.dl4jra.server.cnn;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer.PoolingType;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.dl4jra.server.cnn.request.CNNException;
import com.dl4jra.server.cnn.request.Convolayernode;
import com.dl4jra.server.cnn.request.Denselayernode;
import com.dl4jra.server.cnn.request.Flipdatasetnode;
import com.dl4jra.server.cnn.request.Inputnode;
import com.dl4jra.server.cnn.request.Loaddatasetnode;
import com.dl4jra.server.cnn.request.Mlconfigurationnode;
import com.dl4jra.server.cnn.request.Modalsavingnode;
import com.dl4jra.server.cnn.request.Nodeclass;
import com.dl4jra.server.cnn.request.Outputlayernode;
import com.dl4jra.server.cnn.request.Resizedatasetnode;
import com.dl4jra.server.cnn.request.Rotatedatasetnode;
import com.dl4jra.server.cnn.request.Subsamplinglayernode;
import com.dl4jra.server.cnn.request.Trainnetworknode;
import com.dl4jra.server.cnn.response.ErrorResponse;
import com.dl4jra.server.cnn.response.RBProcessCompleted;
import com.dl4jra.server.cnn.response.UpdateResponse;

@Controller
public class CNNController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CNNController.class);
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private CNN cnn = new CNN();
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@PostConstruct
	public void initialization() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	        executor.shutdown();
	        try 
	        {
	            executor.awaitTermination(1, TimeUnit.SECONDS);
	        } 
	        catch (InterruptedException e) 
	        {
	        	LOGGER.error(e.toString());
	        }
	    }));
	}

	@MessageExceptionHandler
	@SendTo("/response/cnn/error")
	public ErrorResponse handleException(CNNException exception) {
		LOGGER.error("ODCONTROLLER MESSAGE EXCEPTION CAUGHT: " + exception.getMessage());
		return new ErrorResponse(exception.getNodeId(), exception.getMessage());
	}
	
	/**
	 * [WEBSOCKET] Reset cnn
	 * @return ProcessCompleted message
	 */
	@MessageMapping("/cnn/startnewsequence")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted StartNewSequence() {
		this.cnn = new CNN();
		return new RBProcessCompleted("New sequence created");
	}

	/**
	 * [WEBSOCKET] Load training dataset
	 * @param data - Load training dataset data 
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/loadtrainingdataset")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted LoadTrainingDataset(Loaddatasetnode data) throws Exception {
		try 
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.LoadTrainingDataset(data.getPath(), data.getImagewidth(), data.getImageheight(), data.getChannels(), data.getNumLabels(), data.getBatchsize());
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Training dataset loaded successfully");
		} 
		catch (Exception exception) 
		{
			throw new CNNException(exception.getMessage(), data.getNodeId());
		}
	}

	/**
	 * [WEBSOCKET] Flip training dataset
	 * @param data - Flip training dataset data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/fliptrainingdataset")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted FlipTrainingDataset(Flipdatasetnode data) throws Exception {
		try 
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.FlipTrainingDataset(data.getFlipmode());
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Training dataset flipped (FLIPMODE: " + data.getFlipmode() + ")");
		} 
		catch (Exception exception) 
		{
			throw new CNNException(exception.getMessage(), data.getNodeId());
		}
	}
	
	/**
	 * [WEBSOCKET] Rotate training dataset
	 * @param data - Rotate training dataset data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/rotatetrainingdataset")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted RotateTrainingImage(Rotatedatasetnode data) throws Exception {
		try 
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.RotateTrainingDataset(data.getAngle());
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Training dataset rotated (ANGLE: " + data.getAngle() + ")");
		} 
		catch (Exception exception) 
		{
			throw new CNNException(exception.getMessage(), data.getNodeId());
		}
	}

	/**
	 * [WEBSOCKET] Resize training dataset
	 * @param data - Resize training dataset data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/resizetrainingdataset")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted ResizeTrainingImage(Resizedatasetnode data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.ResizeTrainingDataset(data.getImagewidth(), data.getImageheight());
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted(String.format("Training dataset resized (%d x %d)", data.getImagewidth(), data.getImageheight()));
		}
		catch (Exception exception)
		{
			throw new CNNException(exception.getMessage(), data.getNodeId());
		}
	}
	
	/**
	 * Generate training dataset iterator
	 * @param data Generate training dataset iterator data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/generatetrainingdatasetiterator")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted GenerateTrainingDatasetIterator(Nodeclass data) throws Exception {
		try 
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.GenerateTrainingDatasetIterator();
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Training dataset iterator has been generated");
		}
		catch (Exception exception) 
		{
			throw new CNNException("Failed to initialize training dataset iterator", data.getNodeId());
		}
	}
	
	/**
	 * Load validation dataset
	 * @param data - Load validation dataset data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/loadvalidationdataset")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted LoadValidationDataset(Loaddatasetnode data) throws Exception {
		try 
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.LoadValidationDataset(data.getPath(), data.getImagewidth(), data.getImageheight(), 
					data.getChannels(), data.getNumLabels(), data.getBatchsize());
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Validation dataset loaded successfully");
		}
		catch (Exception exception)
		{
			throw new CNNException("Failed to load validation dataset", data.getNodeId());
		}
	}

	/**
	 * Flip validation dataset
	 * @param data Flip validation dataset data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/flipvalidationdataset")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted FlipValidationDataset(Flipdatasetnode data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.FlipValidationDataset(data.getFlipmode());
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Validation dataset flipped (FLIPMODE: " + data.getFlipmode() + ")");
		}
		catch (Exception exception)
		{
			throw new CNNException(exception.getMessage(), data.getNodeId());
		}
	}
	
	/**
	 * Rotate validation dataset 
	 * @param data - Rotate validation dataset data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/rotatevalidationdataset")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted RotateValidationImage(Rotatedatasetnode data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.RotateValidationDataset(data.getAngle());
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Validation dataset rotated (ANGLE: " + data.getAngle() + ")");
		}
		catch (Exception exception)
		{
			throw new CNNException(exception.getMessage(), data.getNodeId());
		}
	}
	
	/**
	 * Resize validation dataset
	 * @param data - Resize validation dataset data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/resizevalidationdataset")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted ResizeValidationImage(Resizedatasetnode data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.ResizeValidationDataset(data.getImagewidth(), data.getImageheight());
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted(String.format("Validation dataset resized (%d x %d)", data.getImagewidth(), data.getImageheight()));
		}
		catch (Exception exception)
		{
			throw new CNNException(exception.getMessage(), data.getNodeId());
		}
	}
	
	/**
	 * Generate validation dataset iterator
	 * @param data - Generate validation dataset iterator data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/generatevalidationdatasetiterator")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted GenerateValidationDatasetIterator(Nodeclass data) throws Exception {
		try 
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.GenerateValidationDatasetIterator();
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Validation dataset iterator has been generated");
		}
		catch (Exception exception) 
		{
			throw new CNNException("Failed to initialize validation dataset iterator", data.getNodeId());
		}
	}
	
	/**
	 *  Initialize configuration
	 * @param data - CNN configuration data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/initializeconfiguration")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted InitializeConfigurations(Mlconfigurationnode data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.InitializeConfigurations(data.getSeed(), data.getLearningrate(), OptimizationAlgorithm.valueOf(data.getOptimizationalgorithm()));
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("CNN has been initialized");
		}
		catch (Exception exception) 
		{
			throw new CNNException("Failed to initialize CNN configurations", data.getNodeId());
		}
	}
	
	/**
	 * Append convolution layer
	 * @param data - Convolution layer configuration data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/appendconvolutionlayer")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted AppendConvolutionLayer(Convolayernode data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.AppendConvolutionLayer(data.getOrdering(), data.getnIn(), data.getnOut(), 
					data.getKernalx(), data.getKernaly(), data.getStridex(), data.getStridey(), Activation.fromString(data.getActivationfunction()));
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Convolution layer has been appended to CNN (ordering: " + data.getOrdering() + ")");
		}
		catch (Exception exception)
		{
			throw new CNNException("Failed to append convolutional layer", data.getNodeId());
		}
	}
	
	/**
	 * Append subsampling layer
	 * @param data - Subsampling layer configuration data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/appendsubsamplinglayer")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted AppendSubsamplingLayer(Subsamplinglayernode data) throws Exception {
		try 
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.AppendSubsamplingLayer(data.getOrdering(), data.getKernalx(), data.getKernaly(), data.getStridex(), 
					data.getStridey(), PoolingType.valueOf(data.getPoolingtype()));
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Subsampling layer has been appended to CNN (ordering: " + data.getOrdering() + ")");
		} 
		catch (Exception exception) 
		{
			throw new CNNException("Failed to append subsampling layer", data.getNodeId());
		}
	}
	
	/**
	 * Append dense layer
	 * @param data - Dense layer configuration data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/appenddenselayer")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted AppendDenseLayer(Denselayernode data) throws Exception {
		try 
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.AppendDenseLayer(data.getOrdering(), data.getnOut(), Activation.fromString(data.getActivationfunction()));
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Dense layer has been appended to CNN (ordering: " + data.getOrdering() + ")");
		} 
		catch (Exception exception) 
		{
			System.out.println(exception.getMessage());
			throw new CNNException("Failed to append dense layer", data.getNodeId());
		}
	}
	
	/**
	 * Append output layer
	 * @param data - Output layer configuration data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/appendoutputlayer")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted AppendOutputLayer(Outputlayernode data) throws Exception {
		try 
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.AppendOutputLayer(data.getOrdering(), data.getnOut(), 
					Activation.valueOf(data.getActivationfunction()), LossFunction.valueOf(data.getLossfunction()));
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Output layer has been appended to CNN (ordering: " + data.getOrdering() + ")");
		} 
		catch (Exception exception) 
		{
			throw new CNNException("Failed to append output layer", data.getNodeId());
		}
	}

	/**
	 * Set input type
	 * @param data - Input type data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/setinputtype")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted SetInputType(Inputnode data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.SetInputType(data.getImagewidth(), data.getImageheight(), data.getChannels());
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("CNN input type has been set");
		}
		catch (Exception exception)
		{
			throw new CNNException("Failed to set input type", data.getNodeId());
		}
	}
	
	/**
	 * Construct network
	 * @param data - Construct node data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/constructnetwork")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted ConstructNetwork(Nodeclass data) throws Exception {
		try 
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.ConstructNetwork();
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Convolutional network has been initialized and is ready to be trained");
		} 
		catch (Exception exception) 
		{
			throw new CNNException("Failed to construct CNN", data.getNodeId());
		}
	}

	/**
	 * Train network
	 * @param data - Network training data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/trainnetwork")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted TrainNetwork(Trainnetworknode data) throws Exception {
		try 
		{
			this.cnn.TrainNetwork(data.getEpochs(), data.getScoreListener(), template);
			return new RBProcessCompleted("Network training completed");
		}
		catch (Exception exception) 
		{
			throw new CNNException(exception.getMessage(), data.getNodeId());
		}
	}

	/**
	 * Validate network
	 * @param data - Validate network node data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/validatenetwork")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted ValidateNetwork(Nodeclass data) throws Exception {
		try 
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.ValidateNetwork(this.template);
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Network validation completed");
		}
		catch (Exception exception)
		{
			throw new CNNException(exception.getMessage(), data.getNodeId());
		}
	}

	/**
	 * Export network (model)
	 * @param data - Model exportation data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/exportnetwork")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted SaveModal(Modalsavingnode data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.SaveModal(data.getPath(), data.getFilename());
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Convolution Neural Network Modal has been saved");
		}
		catch (Exception exception)
		{
			throw new CNNException(exception.getMessage(), data.getNodeId());
		}
	}
	
	/**
	 * Abort cnn training process
	 * @throws Exception
	 */
	@MessageMapping("/cnn/abort")
	public void Abort() throws Exception {
		this.executor.shutdownNow();
		this.executor = Executors.newSingleThreadExecutor();
	}
}




















