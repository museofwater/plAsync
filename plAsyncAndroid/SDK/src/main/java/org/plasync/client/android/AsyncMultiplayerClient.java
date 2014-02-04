package org.plasync.client.android;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.plasync.client.android.model.App;
import org.plasync.client.android.model.FriendRequest;
import org.plasync.client.android.model.User;
import org.plasync.client.android.util.HttpHelper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ericwood on 9/5/13.
 */
public class AsyncMultiplayerClient {
    static final String TAG = AsyncMultiplayerClient.class.getName();

    public final static String API_KEY_HEADER = "PLASYNC-APP-KEY";

    private static final String GCM_SENDER_ID_URL = "gcmSenderId";
    private static final String APP_URL = "app";
    private static final String USERS_URL = "users";
    private static final String FRIEND_REQUESTS_URL = "friend";
    private static final String ACCEPT_FRIEND_REQUEST_URL = "accept";
    private static final String DECLINE_FRIEND_REQUEST_URL = "decline";
    private static final String SEARCH_PARAM = "search";
    public static final String URL_SEPARATOR = "/";


    /**
     * The base URL of the plAsyncServer that this session is connected to.
     *
     * Should not include "/"
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

    public void addApp(App app) throws AsyncMultiplayerSessionError {
        try {
            HttpHelper.postJson(getUrl(APP_URL), new Gson().toJson(app), requestHeaders);
        }
        catch (HttpHelper.ServerError ex) {
            throw new AsyncMultiplayerSessionError(ex.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, "Connection error", e);
            throw new AsyncMultiplayerSessionError("Connection error");
        }
    }

    public List<FriendRequest> getFriendRequests(String appId, User user) throws AsyncMultiplayerSessionError {
        String friendRequestsJson = null;
        try {
            friendRequestsJson = HttpHelper.getJson(getUrl(FRIEND_REQUESTS_URL + "/" + appId + "/" +
                                                           user.getId()),
                    requestHeaders);
            Type listType = new TypeToken<ArrayList<FriendRequest>>() {}.getType();
            return new Gson().fromJson(friendRequestsJson, listType);
        }
        catch (HttpHelper.ServerError ex) {
            throw new AsyncMultiplayerSessionError(ex.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, "Connection error", e);
            throw new AsyncMultiplayerSessionError("Connection error");
        }
    }

    public List<User> searchUsers(String appId, String query) throws AsyncMultiplayerSessionError {
        String searchResultsJson = null;
        try {
            searchResultsJson = HttpHelper.getJson(getUrl(APP_URL + "/" + appId + "/" + USERS_URL +
                                                          "?" + SEARCH_PARAM + "=" + query),
                                                   requestHeaders);
            Type listType = new TypeToken<ArrayList<User>>() {}.getType();
            return new Gson().fromJson(searchResultsJson, listType);
        }
        catch (HttpHelper.ServerError ex) {
            throw new AsyncMultiplayerSessionError(ex.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, "Connection error", e);
            throw new AsyncMultiplayerSessionError("Connection error");
        }
    }

    public void respondToFriendRequest(FriendRequest request, boolean accepted) throws AsyncMultiplayerSessionError {
        try {

            HttpHelper.postJson(getUrl(FRIEND_REQUESTS_URL,String.valueOf(request.getRequestId()),
                                       accepted ? ACCEPT_FRIEND_REQUEST_URL :
                                                  DECLINE_FRIEND_REQUEST_URL),null,requestHeaders);
        }
        catch (HttpHelper.ServerError ex) {
            throw new AsyncMultiplayerSessionError(ex.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, "Connection error", e);
            throw new AsyncMultiplayerSessionError("Connection error");
        }
    }

    public void createFriendRequest(FriendRequest request) throws AsyncMultiplayerSessionError {
        try {
            HttpHelper.postJson(getUrl(FRIEND_REQUESTS_URL),new Gson().toJson(request),
                               requestHeaders);
        }
        catch (HttpHelper.ServerError ex) {
            throw new AsyncMultiplayerSessionError(ex.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, "Connection error", e);
            throw new AsyncMultiplayerSessionError("Connection error");
        }
    }

    private String getUrl(String... subUrls) {
        StringBuffer sbUrl = new StringBuffer(this.plAsyncServerUrl);
        for (int i = 0; i < subUrls.length; ++i) {
            sbUrl.append(URL_SEPARATOR);
            sbUrl.append(subUrls[i]);
        }
        return sbUrl.toString();
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
