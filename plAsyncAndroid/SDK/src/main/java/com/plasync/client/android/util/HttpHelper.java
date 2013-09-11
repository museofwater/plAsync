package com.plasync.client.android.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by ericwood on 8/24/13.
 */
public class HttpHelper {

    public static final String TAG = HttpHelper.class.getName();

    public static String getJson(String url, Map<String, String> requestHeaders) throws ServerError, IOException {
        // Prevent connection pooling
        System.setProperty("http.keepAlive", "false");

        URL httpUrl = new URL(url);
        HttpURLConnection httpConnection = (HttpURLConnection) httpUrl.openConnection();

        httpConnection.setRequestMethod("GET");

        setHeaders(httpConnection, requestHeaders);

        httpConnection.connect();

        return getResponse(httpConnection);

    }

    public static String post(String url, Map<String, String> requestHeaders) throws ServerError, IOException {
        return postPutJson(url, "POST", null, requestHeaders);
    }

    public static String postJson(String url, String jsonEntity, Map<String, String> requestHeaders) throws ServerError, IOException {
        return postPutJson(url, "POST", jsonEntity, requestHeaders);
    }

    public static String putJson(String url, String jsonEntity, Map<String, String> requestHeaders) throws ServerError, IOException {
        return postPutJson(url, "PUT", jsonEntity, requestHeaders);
    }

    private static String postPutJson(String url, String requestType, String jsonEntity, Map<String, String> requestHeaders) throws ServerError, IOException {
        // Prevent connection pooling
        System.setProperty("http.keepAlive", "false");

        URL httpUrl = new URL(url);
        HttpURLConnection httpConnection = (HttpURLConnection) httpUrl.openConnection();

        httpConnection.setRequestMethod(requestType);
        if (jsonEntity != null) {
            httpConnection.setRequestProperty("Content-Type","application/json");
        }

        setHeaders(httpConnection, requestHeaders);

        httpConnection.connect();

        if (jsonEntity != null) {
            OutputStreamWriter out = new   OutputStreamWriter(httpConnection.getOutputStream());
            out.write(jsonEntity);
            out.close();
        }

        return getResponse(httpConnection);
    }

    private static void setHeaders(HttpURLConnection httpConnection, Map<String,String> requestHeaders) {
        for (Map.Entry<String,String> headerEntry : requestHeaders.entrySet()) {
            httpConnection.setRequestProperty(headerEntry.getKey(),headerEntry.getValue());
        }
    }

    private static String getResponse(HttpURLConnection httpConnection) throws IOException, ServerError {
        InputStream inputStream;
        StringBuilder sbJson = new StringBuilder();
        StringBuilder sbError = new StringBuilder();
        int responseCode=httpConnection.getResponseCode();
        boolean errorResponse = false;
        if ((responseCode>= 200) &&(responseCode<=202) ) {
            inputStream = (httpConnection).getInputStream();


        }
        else {
            inputStream = httpConnection.getErrorStream();
            errorResponse =  true;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine;
        while ((inputLine = in.readLine()) != null)  {
            if (errorResponse) {
                Log.e(TAG, inputLine);
                sbError.append(inputLine+"\n");
            }
            else {
                sbJson.append(inputLine+"\n");
            }
        }
        inputStream.close();
        httpConnection.disconnect();
        if (errorResponse) {
            throw new ServerError(sbError.toString());
        }
        return sbJson.toString();
    }

    public static class ServerError extends Exception {
        public ServerError(String message) {
            super(message);
        }
    }
}
