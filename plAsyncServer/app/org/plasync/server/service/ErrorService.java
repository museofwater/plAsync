package org.plasync.server.service;

import org.codehaus.jackson.JsonNode;
import org.plasync.server.AppNotFoundException;
import org.plasync.server.DuplicateFriendRequestException;
import org.plasync.server.models.ApiError;import play.libs.Json;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/16/13
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class ErrorService {

    public static JsonNode getErrorResponse(DuplicateFriendRequestException e) {
        return Json.toJson(new ApiError(e.getClass().getName(),"Duplicate friend request"));
    }


    public static JsonNode getErrorResponse(AppNotFoundException e) {
        return Json.toJson(new ApiError(e.getClass().getName(),"Application not found"));
    }

    public static JsonNode getErrorResponse(NotificationException e) {
        return Json.toJson(new ApiError(e.getClass().getName(),"Notification failed"));
    }
}
