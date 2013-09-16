package com.hack3d.truckit;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericwood on 8/24/13.
 */
public class TruckItClient {

    private static final String BASE_URL = "http://192.168.8.72:9000";
    private static final String CREATE_USER_URL = "/user";
    private static final String CREATE_LOAD_URL = "/load";
    private static final String CREATE_BID_URL = "/bid";
    private static final String CREATE_JOB_URL = "/job";
    private static final String UPDATE_JOB_URL = "/job";
    private static final String GET_LOADS_URL = "/load";
    private static final String GET_BIDS_URL = "/bid";
    private static final String GET_JOBS_URL = "/job";



//    public static void createUser(User user) {
//        String jsonEntity = new Gson().toJson(user,User.class);
//        HttpHelper.postJson(getUrl(CREATE_USER_URL),jsonEntity);
//    }
//
//    public static void createLoad(Load load) {
//        String jsonEntity = new Gson().toJson(load,Load.class);
//        HttpHelper.postJson(getUrl(CREATE_LOAD_URL),jsonEntity);
//    }
//
//    public static void createBid(Bid bid) {
//        String jsonEntity = new Gson().toJson(bid,Bid.class);
//        HttpHelper.postJson(getUrl(CREATE_BID_URL),jsonEntity);
//    }
//
//    public static void createJob(Job job) {
//        String jsonEntity = new Gson().toJson(job,Job.class);
//        HttpHelper.postJson(getUrl(CREATE_JOB_URL),jsonEntity);
//    }
//
//    public static void updateJob(Job job) {
//        String jsonEntity = new Gson().toJson(job,Job.class);
//        HttpHelper.postJson(getUrl(UPDATE_JOB_URL),jsonEntity);
//    }
//
//    public static List<Load> getLoads(String city, String state) {
//        StringBuilder sbUrl = new StringBuilder(getUrl(GET_LOADS_URL));
//        sbUrl.append("?city=");
//        sbUrl.append(city);
//        sbUrl.append("&state=");
//        sbUrl.append(state);
//        String loadsJson = HttpHelper.getJson(sbUrl.toString());
//        Type listType = new TypeToken<ArrayList<Load>>() {
//        }.getType();
//        return new Gson().fromJson(loadsJson, listType);
//    }
//
//    public static List<Bid> getBids(String userId) {
//        StringBuilder sbUrl = new StringBuilder(getUrl(GET_BIDS_URL));
//        sbUrl.append("/");
//        sbUrl.append(userId);
//        String bidsJson = HttpHelper.getJson(sbUrl.toString());
//        Type listType = new TypeToken<ArrayList<Bid>>() {
//        }.getType();
//        return new Gson().fromJson(bidsJson, listType);
//    }
//
//    public static List<Job> getJobs(String userId) {
//        StringBuilder sbUrl = new StringBuilder(getUrl(GET_JOBS_URL));
//        sbUrl.append("/");
//        sbUrl.append(userId);
//        String bidsJson = HttpHelper.getJson(sbUrl.toString());
//        Type listType = new TypeToken<ArrayList<Job>>() {
//        }.getType();
//        return new Gson().fromJson(bidsJson, listType);
//    }

    private static String getUrl(String subUrl) {
        return BASE_URL + subUrl;
    }
}
