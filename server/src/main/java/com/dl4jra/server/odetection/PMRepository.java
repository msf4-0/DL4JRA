package com.dl4jra.server.odetection;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PMRepository {
	/**
	 * Provide full details for pretrained model
	 * 1. Path to pretrained model
	 * 2. Pretrained model classes (labels)
	 * 3. Pretrained model image input dimension (width, height, channels)
	 * 4. Pretrained model gridsize
	 */
	private static String rootpath = Paths.get("").toAbsolutePath().normalize().toString();
	
	@SuppressWarnings("serial")
	private static final HashMap<String, ArrayList<String>> classes = new HashMap<String, ArrayList<String>>() {{
		put("tinyyolo", new ArrayList<String>(Arrays.asList("Aeroplane", "Bicycle", "Bird", "Boat", "Bottle", "Bus", "Car", "Cat", "Chair", "Cow", "DiningTable", "Dog", "Horse", "Motorbike", "Person", "Pottedplant", "Sheep", "Sofa", "Train", "Tvmonitor")));
		put("yolo2", new ArrayList<String>(Arrays.asList("Aeroplane", "Bicycle", "Bird", "Boat", "Bottle", "Bus", "Car", "Cat", "Chair", "Cow", "DiningTable", "Dog", "Horse", "Motorbike", "Person", "Pottedplant", "Sheep", "Sofa", "Train", "Tvmonitor")));
		put("resnet50", new ArrayList<String>(Arrays.asList("Aeroplane", "Bicycle", "Bird", "Boat", "Bottle", "Bus", "Car", "Cat", "Chair", "Cow", "DiningTable", "Dog", "Horse", "Motorbike", "Person", "Pottedplant", "Sheep", "Sofa", "Train", "Tvmonitor")));
	}};
	
	public static ODModelConfigurationData GetPretrainedModelData(String modelname) throws Exception{
		System.out.println(rootpath);
//		E:\SHRDC\DL4JRA2\DL4JRA\server/odmodels/tinyyolo.data
		ODModelConfigurationData odModelConfigurationData;
		if (modelname.equals("tinyyolo"))
			odModelConfigurationData = new ODModelConfigurationData(classes.get("tinyyolo"), 416, 416,
					3, 13, 13, rootpath + "/odmodels/tinyyolo.data");
		else if(modelname.equals("yolo2"))
			odModelConfigurationData =  new ODModelConfigurationData(classes.get("yolo2"), 608, 608, 3, 13, 13);
		else if(modelname.equals("resnet50"))
			odModelConfigurationData =  new ODModelConfigurationData(classes.get("resnet50"), 224, 224, 3, 13, 13);
		else
			throw new Exception("Pretrained model not found");
		return  odModelConfigurationData;
	}
}