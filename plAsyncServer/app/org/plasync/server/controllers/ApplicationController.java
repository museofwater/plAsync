package org.plasync.server.controllers;

import org.plasync.server.models.Credentials;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class ApplicationController extends Controller {

    // Cache sender id
    static String gcmSenderId;

//    @With(AppKeyController.class)
    public static Result getGCMSenderId() {
        if (gcmSenderId == null) {
            gcmSenderId = Credentials.getGcmSenderID();
        }
        return ok(Json.toJson(gcmSenderId));
    }
  
}
