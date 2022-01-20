package com.dl4jra.server;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;

public class LibraryLoader {
	// Load opencv library
	public static void loadOpencvLibrary() {
		Loader.load(opencv_java.class);
	}
}
