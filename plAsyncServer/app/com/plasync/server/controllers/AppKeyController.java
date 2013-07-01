package com.plasync.server.controllers;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 6/26/13
 * Time: 7:52 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Responsible for confirming the presence and validity of an app key for each request
 */
public class AppKeyController extends Action.Simple {

    public final static String APP_KEY_HEADER = "PLASYNC-APP-KEY";

    public Result call(Http.Context ctx) throws Throwable {
        String[] appKeyHeaderValues = ctx.request().headers().get(APP_KEY_HEADER);
        if ((appKeyHeaderValues != null) && (appKeyHeaderValues.length == 1) && (appKeyHeaderValues[0] != null)) {
            return delegate.call(ctx);
        }

        return unauthorized("unauthorized");
    }

}