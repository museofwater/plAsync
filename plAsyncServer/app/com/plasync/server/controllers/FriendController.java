package com.plasync.server.controllers;

import com.plasync.server.models.FriendAssociation;
import com.plasync.server.models.FriendRequest;
import com.plasync.server.models.FriendRequestStatus;
import com.plasync.server.service.DuplicateFriendRequestException;
import com.plasync.server.service.FriendService;
import com.plasync.server.service.NotFoundException;
import com.plasync.server.service.NotificationService;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 8/30/13
 * Time: 5:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class FriendController extends Controller {

    public static Result getFriends(String appId, String userId) {
        return ok(Json.toJson(FriendService.getFriends(appId, userId)));
    }

    public static Result getUnacceptedFriendRequests(String appId, String userId) {
        return ok(Json.toJson(FriendService.getUnacceptedFriendRequests(appId, userId)));
    }

    public static Result createFriendRequest() {
        FriendRequest newFriendRequest = Json.fromJson(request().body().asJson(),FriendRequest.class);
        try {
            FriendService.createRequest(newFriendRequest);
        }
        catch (DuplicateFriendRequestException e) {
            return badRequest("Duplicate friend request");
        }
        return ok();
    }

    public static Result acceptFriendRequest(long requestId) {
        try {
            FriendService.accept(requestId);
        }
        catch (NotFoundException e) {
            return notFound(e.getMessage());
        }
        return ok();
    }

    public static Result declineFriendRequest(long requestId) {
        try {
            FriendService.decline(requestId);
        }
        catch (NotFoundException e) {
            return notFound(e.getMessage());
        }
        return ok();
    }
}
