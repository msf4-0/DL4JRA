package com.dl4jra.server.cnn;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;

import com.dl4jra.server.cnn.request.*;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.RNNFormat;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer.PoolingType;
import org.deeplearning4j.nn.weights.WeightInit;
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
	 * [WEBSOCKET] Load training dataset CSV
	 * @param data - Load training dataset CSV data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/loadtrainingdataset_csv")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted LoadTrainingDatasetCSV(Loaddatasetnode data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.LoadTrainingDatasetCSV(data.getPath(), data.getNumSkipLines(), data.getNumClassLabels(),
					data.getBatchsize(), data.getDelimeter());
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Training dataset CSV loaded successfully");
		}
		catch (Exception exception)
		{
			throw new CNNException(exception.getMessage(), data.getNodeId());
		}
	}

	/**
	 * [WEBSOCKET] Load data set then auto split it into training and testing
	 * @param data - Load dataset data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/loaddatasetautosplit")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted LoadDatasetAutoSplit (Loaddatasetnode data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.LoadDatasetAutoSplit(data.getPath(), data.getImagewidth(), data.getImageheight(), data.getChannels(), data.getNumLabels(), data.getBatchsize());
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Dataset (Auto-split) loaded successfully");
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
	 * Generate training dataset CSV iterator
	 * @param data Generate training dataset iterator data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/generatetrainingdatasetiterator_csv")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted GenerateTrainingDatasetIteratorCSV(Nodeclass data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.GenerateTrainingDatasetIteratorCSV();
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Training dataset CSV iterator has been generated");
		}
		catch (Exception exception)
		{
			throw new CNNException("Failed to initialize training dataset CSV iterator", data.getNodeId());
		}
	}

	/**
	 * Generate dataset (auto-split) iterator
	 * @param data Generate ataset (auto-split) iterator data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/generatedatasetautosplititerator")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted GenerateDatasetAutoSplitIterator(Nodeclass data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.GenerateDatasetAutoSplitIterator();
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Training and validating iterator has been generated");
		}
		catch (Exception exception)
		{
			throw new CNNException("Failed to initialize dataset (auto-split) iterator", data.getNodeId());
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
	 * Load validation dataset CSV
	 * @param data - Load validation dataset data CSV
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/loadvalidationdataset_csv")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted LoadValidationDatasetCSV(Loaddatasetnode data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.LoadTestingDatasetCSV(data.getPath(), data.getNumSkipLines(), data.getNumClassLabels(),
					data.getBatchsize(), data.getDelimeter());
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Validation dataset CSV loaded successfully");
		}
		catch (Exception exception)
		{
			throw new CNNException("Failed to load validation dataset CSV", data.getNodeId());
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
	 * Generate validation dataset CSV iterator
	 * @param data - Generate validation dataset iterator data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/generatevalidationdatasetiterator_csv")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted GenerateValidationDatasetIteratorCSV(Nodeclass data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.GenerateValidatingDatasetIteratorCSV();
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Validation dataset CSV iterator has been generated");
		}
		catch (Exception exception)
		{
			throw new CNNException("Failed to initialize validation dataset CSV iterator", data.getNodeId());
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
            ConvolutionMode convolutionMode;
            if (data.getConvolutionMode().equals("null")){
                convolutionMode = null;
            }
            else{
                convolutionMode = ConvolutionMode.valueOf(data.getConvolutionMode());
            }

			Activation activation;
			if (data.getActivationfunction().equals("null")){
				activation = null;
			}
			else{
				activation = Activation.fromString(data.getActivationfunction());
			}

			WeightInit weightInit;
			if (data.getWeightInit().equals("null")){
				weightInit = null;
			}
			else{
				weightInit = WeightInit.valueOf(data.getWeightInit());
			}

			OptimizationAlgorithm optimizationAlgorithm;
			if (data.getOptimizationalgorithm().equals("null")){
				optimizationAlgorithm = null;
			}
			else{
				optimizationAlgorithm = OptimizationAlgorithm.valueOf(data.getOptimizationalgorithm());
			}

			GradientNormalization gradientNormalization;
			if(data.getGradientNormalization().equals("null")){
				gradientNormalization = null;
			}
			else{
				gradientNormalization = GradientNormalization.valueOf(data.getGradientNormalization());
			}

			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.InitializeConfigurations(data.getSeed(), data.getLearningrate(), optimizationAlgorithm,
                    convolutionMode, activation, weightInit, gradientNormalization);
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

			ConvolutionMode convolutionMode;
			if (data.getConvolutionMode().equals("null")){
				convolutionMode = null;
			}
			else{
				convolutionMode = ConvolutionMode.valueOf(data.getConvolutionMode());
			}

			Activation activation;
			if (data.getActivationfunction().equals("null")){
				activation = null;
			}
			else{
				activation = Activation.fromString(data.getActivationfunction());
			}

			this.cnn.AppendConvolutionLayer(data.getOrdering(), data.getnIn(), data.getnOut(), data.getKernalx(), data.getKernaly(),
					data.getStridex(), data.getStridey(), data.getPaddingx(), data.getPaddingy(), activation,
					data.getDropOut(), data.getBiasInit(), convolutionMode);
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
			ConvolutionMode convolutionMode;
			if (data.getConvolutionMode().equals("null")){
				convolutionMode = null;
			}
			else{
				convolutionMode = ConvolutionMode.valueOf(data.getConvolutionMode());
			}
			this.cnn.AppendSubsamplingLayer(data.getOrdering(), data.getKernalx(), data.getKernaly(), data.getStridex(), 
					data.getStridey(), data.getPaddingx(), data.getPaddingy(), PoolingType.valueOf(data.getPoolingtype()),
					convolutionMode);
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
			Activation activation;
			if (data.getActivationfunction().equals("null")){
				activation = null;
			}
			else{
				activation = Activation.fromString(data.getActivationfunction());
			}

			WeightInit weightInit;
			if (data.getWeightInit().equals("null")){
				weightInit = null;
			}
			else{
				weightInit = WeightInit.valueOf(data.getWeightInit());
			}

			this.cnn.AppendDenseLayer(data.getOrdering(),data.getnOut(), activation,
					data.getDropOut(), data.getBiasInit(), weightInit);
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

			Activation activation;
			if (data.getActivationfunction().equals("null")){
				activation = null;
			}
			else{
				activation = Activation.fromString(data.getActivationfunction());
			}

			WeightInit weightInit;
			if (data.getWeightInit().equals("null")){
				weightInit = null;
			}
			else{
				weightInit = WeightInit.valueOf(data.getWeightInit());
			}

			this.cnn.AppendOutputLayer(data.getOrdering(), data.getnOut(), 
					activation, LossFunction.valueOf(data.getLossfunction()), weightInit);
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Output layer has been appended to CNN (ordering: " + data.getOrdering() + ")");
		} 
		catch (Exception exception) 
		{
			throw new CNNException("Failed to append output layer", data.getNodeId());
		}
	}

//	===============================================================================================================================
	/**
	 * Append Local Response Normalization layer
	 * @param data - Local Response Normalization layer configuration data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/appendlocalresponsenormalizationlayer")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted AppendLocalResponseNormalizationLayer(LocalResponseNormalizaionNode data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.AppendLocalResponseNormalizationLayer(data.getOrdering());
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Local Response Normalization layer has been appended to CNN (ordering: " + data.getOrdering() + ")");
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
			System.out.println(new RBProcessCompleted("START TRAINING"));
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


//	FOR RNN
	/**
	 *  Initialize configuration
	 * @param data - CNN configuration data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/initializeconfiguration_rnn")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted InitializeConfigurationsRNN(Mlconfigurationnode data) throws Exception {
		try
		{
			WeightInit weightInit;
			if (data.getWeightInit().equals("null")){
				weightInit = null;
			}
			else{
				weightInit = WeightInit.valueOf(data.getWeightInit());
			}

			OptimizationAlgorithm optimizationAlgorithm;
			if (data.getOptimizationalgorithm().equals("null")){
				optimizationAlgorithm = null;
			}
			else{
				optimizationAlgorithm = OptimizationAlgorithm.valueOf(data.getOptimizationalgorithm());
			}


			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.InitializeConfigurationsGraphBuilder(data.getSeed(), data.getLearningrate(), optimizationAlgorithm,
					weightInit);
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("RNN has been initialized");
		}
		catch (Exception exception)
		{
			throw new CNNException("Failed to initialize RNN configurations", data.getNodeId());
		}
	}

	/**
	 * Add input for RNN
	 * @param data - AddInputNode node data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/addinput")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted AddInput(AddInputNode data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.AddInput(data.getInputName());
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Added input name: " + data.getInputName());
		}
		catch (Exception exception)
		{
			throw new CNNException(exception.getMessage(), data.getNodeId());
		}
	}

	/**
	 * Add input for RNN
	 * @param data - AddInputNode node data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/setoutput")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted SetOutput(SetOutputNode data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.SetOutput(data.getOutputName());
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("Set output name: " + data.getOutputName());
		}
		catch (Exception exception)
		{
			throw new CNNException(exception.getMessage(), data.getNodeId());
		}
	}

	/**
	 * Append LSTM layer
	 * @param data - LSTM layer configuration data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/appendlstm")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted AppendLSTM(LSTMlayerNode data) throws Exception {
		int v = 0;
		try
		{
			v = 1;
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			v = 2;
			Activation activation;
			if (data.getActivationfunction().equals("null")){
				activation = null;
			}
			else{
				activation = Activation.fromString(data.getActivationfunction());
			}

			v = 3;
//			this.cnn.AppendLSTMLayer(data.getLayerName(), data.getnOut(), activation, data.getLayerInput());
			cnn.AppendLSTMLayer("layer0", 100, Activation.TANH, "trainFeatures");

			v = 4;
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			v = 5;
			return new RBProcessCompleted("LSTM has been appended to RNN (Layer name: " + data.getLayerName() + ")");
		}
		catch (Exception exception)
		{
			return new RBProcessCompleted("EXCEPTION : " + exception.getMessage());
		}

	}


	/**
	 * Append RNN output layer
	 * @param data - RNN Output layer configuration data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/appendrnnoutputlayer")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted AppendRnnOutputLayer(RnnOutputLayerNode data) throws Exception {
		int v = 0;
		try
		{
			v = 1;
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			v =2;
//			Activation activation;
//			if (data.getActivationfunction().equals("null")){
//				activation = null;
//			}
//			else{
//				activation = Activation.fromString(data.getActivationfunction());
//			}
			v=3;
//			this.cnn.AppendRnnOutputLayer(data.getLayerName(), RNNFormat.valueOf(data.getRnnFormat()), data.getnIn(), data.getnOut(),
//					LossFunction.valueOf(data.getLossfunction()), Activation.fromString(data.getActivationfunction()), data.getLayerInput());
			cnn.AppendRnnOutputLayer("predictActivity", RNNFormat.NCW, 100, 6, LossFunction.MCXENT,
					Activation.SOFTMAX, "layer0");
			v=4;
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("RNN Output layer has been appended to RNN (Layer name: " + data.getLayerName() + ")");
		}
		catch (Exception exception)
		{
			return new RBProcessCompleted("EXCEPTION : " + exception.getMessage());
		}
	}


	/**
	 * Construct network RNN
	 * @param data - Construct node data
	 * @return ProcessCompleted message
	 * @throws Exception
	 */
	@MessageMapping("/cnn/constructnetwork_rnn")
	@SendTo("/response/cnn/currentprocessdone")
	public RBProcessCompleted ConstructNetworkRNN(Nodeclass data) throws Exception {
		try
		{
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(0, 1));
			this.cnn.ConstructNetworkRNN();
			this.template.convertAndSend("/response/cnn/progressupdate", new UpdateResponse(1, 1));
			return new RBProcessCompleted("RNN network has been initialized and is ready to be trained");
		}
		catch (Exception exception)
		{
			throw new CNNException("Failed to construct RNN", data.getNodeId());
		}
	}

}




















