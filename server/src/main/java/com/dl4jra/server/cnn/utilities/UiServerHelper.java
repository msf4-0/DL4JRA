package com.dl4jra.server.cnn.utilities;

import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.model.storage.FileStatsStorage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class UiServerHelper {
    UIServer uiServer = null;
    StatsStorage statsStorage = null;
    public UiServerHelper(){
    }

    public StatsStorage startUiServer() throws InterruptedException, IOException, URISyntaxException {
        // start ui server
        System.out.println("Starting UI server");
        if (uiServer == null) {
            uiServer = UIServer.getInstance();
        } else {

            System.out.println("stopping ui server");
            uiServer.stop();
            System.out.println("restarting ui server");
            uiServer = UIServer.getInstance();
        }
        if (statsStorage == null) {
            statsStorage = new FileStatsStorage(new File(System.getProperty("java.io.tmpdir"), "ui-stats.dl4j"));
        } else {
            statsStorage.close();
            statsStorage = new FileStatsStorage(new File(System.getProperty("java.io.tmpdir"), "ui-stats.dl4j"));
        }

        uiServer.attach(statsStorage);                // Check if the current thread is interrupted, if so, break the loop.
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop(
            ).browse(new URI("http://localhost:9000"));
        }

        return statsStorage;
    }
    public void stopUiServer() throws IOException, InterruptedException {
        uiServer.detach(statsStorage);
        statsStorage.close();
        System.out.println("stopping ui server");
        uiServer.stop();
    }
}