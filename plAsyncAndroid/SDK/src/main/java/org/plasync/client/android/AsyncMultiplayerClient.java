package org.plasync.client.android;

import android.util.Log;

import com.google.gson.Gson;
import org.plasync.client.android.util.HttpHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ericwood on 9/5/13.
 */
public class AsyncMultiplayerClient {
    static final String TAG = AsyncMultiplayerClient.class.getName();

    public final static String API_KEY_HEADER = "PLASYNC-APP-KEY";

    private static final String GCM_SENDER_ID_URL = "cgmSenderId";

    /**
     * The base URL of the plAsyncServer that this session is connected to
     */
    private final String plAsyncServerUrl;

    /**
     * The plAsync API Key for using the service.
     *
     * This may be required by some servers.  Can be null, but that may cause the server to reject the request
     */
    private final String plAsyncApiKey;

    private Map<String,String> requestHeaders = new HashMap<String,String>();

    public AsyncMultiplayerClient(String plAsyncApiKey, String plAsyncServerUrl) {
        this.plAsyncApiKey = plAsyncApiKey;
        this.plAsyncServerUrl = plAsyncServerUrl;
        if (plAsyncApiKey != null) {
            requestHeaders.put(API_KEY_HEADER, plAsyncApiKey);
        }
    }

    public String getSenderId() throws AsyncMultiplayerSessionError {
        String gcmSenderJson = null;
        try {
            gcmSenderJson = HttpHelper.getJson(getUrl(GCM_SENDER_ID_URL), requestHeaders);
            return new Gson().fromJson(gcmSenderJson,String.class);
        }
        catch (HttpHelper.ServerError ex) {
            throw new AsyncMultiplayerSessionError(ex.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, "Connection error", e);
            throw new AsyncMultiplayerSessionError("Connection error");
        }
    }

    private String getUrl(String subUrl) {
        return this.plAsyncServerUrl + subUrl;
    }

//    private static enum RequestType {
//        GET, POST,
//    }
//
//    private static class AsyncMultiplayerApiRequest {
//        private String url;
//        private RequestType type;
//        private String jsonEntity;
//        private Map<String,String> requestHeaders = new HashMap<String,String>();
//
//        private AsyncMultiplayerApiRequest(String url, RequestType type, String jsonEntity, Map<String, String> requestHeaders) {
//            this.url = url;
//            this.type = type;
//            this.jsonEntity = jsonEntity;
//            this.requestHeaders = requestHeaders;
//        }
//
//        public String getUrl() {
//            return url;
//        }
//
//        public RequestType getType() {
//            return type;
//        }
//
//        public String getJsonEntity() {
//            return jsonEntity;
//        }
//
//        public Map<String, String> getRequestHeaders() {
//            return requestHeaders;
//        }
//    }
//
//    private static interface AsyncMultiplayerApiRequestCompleteListener {
//        void onRequestComplete(String jsonResult);
//        void onRequestError(AsyncMultiplayerSessionError error);
//    }
//
//    private static class AsyncMultiplayerApiRequestAsyncTask extends AsyncTask<Void, Void, Void> {
//
//        private AsyncMultiplayerApiRequest request;
//        private AsyncMultiplayerApiRequestCompleteListener callback;
//
//        String jsonResult;
//        AsyncMultiplayerSessionError error;
//
//        public AsyncMultiplayerApiRequestAsyncTask(AsyncMultiplayerApiRequest request, AsyncMultiplayerApiRequestCompleteListener callback) {
//            this.request = request;
//            this.callback = callback;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                if (request.getType() == RequestType.GET) {
//                    jsonResult = HttpHelper.getJson(request.getUrl(),request.getRequestHeaders());
//                }
//                else {
//                    String jsonEntity = request.getJsonEntity();
//                    if (request.getType() == RequestType.POST) {
//                        if (jsonEntity == null) {
//                            jsonResult = HttpHelper.post(request.getUrl(),request.getRequestHeaders());
//                        }
//                        else {
//                            jsonResult = HttpHelper.postJson(request.getUrl(), jsonEntity, request.getRequestHeaders());
//                        }
//                    }
//                    else {
//                        jsonResult = HttpHelper.putJson(request.getUrl(), jsonEntity, request.getRequestHeaders());
//                    }
//                }
//            }
//            catch (HttpHelper.ServerError ex) {
//                this.error = new AsyncMultiplayerSessionError(ex.getMessage());
//            }
//            catch (IOException e) {
//                Log.e(AsyncMultiplayerClient.TAG, "Connection error", e);
//                this.error = new AsyncMultiplayerSessionError("Connection error");
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void param) {
//            if (error == null) {
//                callback.onRequestComplete(jsonResult);
//            }
//            else {
//                callback.onRequestError(error);
//            }
//        }
//    }
}