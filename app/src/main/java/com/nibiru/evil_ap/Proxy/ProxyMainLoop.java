package com.nibiru.evil_ap.proxy;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nibiru on 2016-10-14.
 */

public class ProxyMainLoop implements Runnable{
    final static String TAG = "ProxyMainLoop";
    private ServerSocket serverSocket;
    public static final int SERVERPORT = 1337;
    protected Boolean work = true;
    /*********************************************************************************************/
    @Override
    public void run() {
        //http://codetheory.in/android-java-executor-framework/
        //int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                140,
                140,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()
        );

        try {
            serverSocket = new ServerSocket(SERVERPORT);
            while (work) {
                // listen for incoming clients
                Log.d(TAG, "Listening on port: " + SERVERPORT);
                executor.execute(new ProxyRunnable(serverSocket.accept()));
            }
        } catch (IOException e) {
            Log.d(TAG, "Error!");
            e.printStackTrace();
        }
    }
}