package com.nibiru.evil_ap.proxy;

import android.support.v4.util.Pair;
import android.util.Log;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Vector;

import okhttp3.Request;

/**
 * Created by Nibiru on 2016-11-03.
 */

class OkHttpParser {
    private final static String TAG = "OkHttpParser";
    private Vector<Pair<String,String>> requestHeaders;
    private StringBuffer messageBody;
    /*********************************************************************************************/
    OkHttpParser(){
        requestHeaders = new Vector<>();
        messageBody = new StringBuffer();
    }
    Request parse(String request){
        BufferedReader reader = new BufferedReader(new StringReader(request));
        try {
            //read request line
            String requestLine = reader.readLine(); // Request-Line ; Section 5.1
            //read header
            String header = reader.readLine();
            while (header != null && header.length() > 0) {
                //skip over HTTPS upgrade header and HSTS header
                if (!header.startsWith("Upgrade-Insecure-Requests")
                        && !header.startsWith("Strict-Transport-Security")
                        && !header.startsWith("User-Agent")){
                    appendHeaderParameter(header);
                }
                header = reader.readLine();
            }
            //read body
            String bodyLine = reader.readLine();
            while (bodyLine != null) {
                appendMessageBody(bodyLine);
                bodyLine = reader.readLine();
            }
            return buildOkHTTPRequest(requestLine);

        } catch (Exception e) {
            Log.d(TAG, "Unable to parse request");
            e.printStackTrace();
            return null;
        }
    }

    private void appendHeaderParameter(String header) throws Exception {
        int idx = header.indexOf(":");
        if (idx == -1) {
            throw new Exception("Invalid Header Parameter: " + header);
        }
        Pair<String, String> p = new Pair<>(header.substring(0, idx), header.substring(idx + 1, header.length()));
        requestHeaders.add(p);
    }

    private void appendMessageBody(String bodyLine) {
        messageBody.append(bodyLine).append("\r\n");
    }

    private Request buildOkHTTPRequest(String requestLine) {
        String[] requestLineValues = requestLine.split("\\s+");
        String url = "";
        Request.Builder builder = new Request.Builder();
        for(Pair<String, String> header : requestHeaders){
            if (header.first.equals("Host") || header.first.equals("host")){
                url = "http://" + header.second.trim() + requestLineValues[1];
                Log.d(TAG, url);
                builder.url(url);
            }
            else builder.addHeader(header.first, header.second);
        }
        return builder.build();
    }
}