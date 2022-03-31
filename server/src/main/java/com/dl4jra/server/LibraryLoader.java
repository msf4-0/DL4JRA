package com.dl4jra.server;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;

import java.io.File;

public class LibraryLoader {
	// Load opencv library
	public static void loadOpencvLibrary() {
		final File f = new File(opencv_java.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		System.out.println("\n\n\n Here lies \n\n\n some more \n\n\n");
		System.out.println(f.getAbsolutePath());
		System.out.println("\n\n\n Here lies \n\n\n some more \n\n\n");
		Loader.load(opencv_java.class);
	}
}
