package com.plasync.server.models;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/4/13
 * Time: 7:29 PM
 * To change this template use File | Settings | File Templates.
 */

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Stores secret keys and credentials for the app to prevent unauthorized use
 */
@Entity
public class Credentials extends Model {

    public static Finder<Integer,Credentials> find = new Finder<Integer,Credentials>(
            Integer.class, Credentials.class
    );

    @Id
    private int id;

    private String gcmSenderId;

    private String gcmApiKey;

    /**
     * API key to use when calling services internally
     */
    private String apiMasterKey;

    public static String getGCMSenderID() {
        return find.all().get(0).gcmSenderId;
    }
}
