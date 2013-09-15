package org.plasync.server.service;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.TxRunnable;
import org.plasync.server.models.FriendAssociation;
import org.plasync.server.models.FriendRequest;
import org.plasync.server.models.FriendRequestStatus;
import org.plasync.server.models.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/2/13
 * Time: 2:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class FriendService {

    /**
     * Gets all current friends of the specified user and app
     *
     * In order for a user to be a friend of another user, that user must have an association where the user is a
     * friend of the requesting user and an association where the requesting user is a friend of the user, both with
     * status of accepted
     * @param userId
     * @return
     */
    public static List<User> getFriends(String appId, String userId) {
        List<User> friends = new ArrayList<User>();
        // Get a list of users requested by this user
        List<FriendAssociation> requests = FriendAssociation.findFromAssociationsByUser(appId, userId,
                                                                                        FriendRequestStatus.ACCEPTED);
        // Get the users from the associations
        Set<User> requestedUsers = getFriendsFromAssociations(requests);
        // Now get a list of accepted requests from this user
        List<FriendAssociation> acceptedRequests = FriendAssociation.findToAssociationsByUser(appId, userId,
                                                                                              FriendRequestStatus.ACCEPTED);
        // Add all of the users from the acceptedRequests that were requested by the user
        for (FriendAssociation friendAssociation : acceptedRequests) {
            User user = friendAssociation.getUser();
            if (requestedUsers.contains(user)) {
                friends.add(user) ;
            }
        }
        return friends;
    }


    /**
     * Gets all unaccepted requests to the user in the specified app.
     *
     * This includes pending and previously declined requests.  The declined requests are included because accepting
     * a previously declined request is the only way those users can establish a friendship relationship, since the
     * original requestor is prohibited from sending a second request.
     * @param userId
     * @return
     */
    public static List<FriendRequest> getUnacceptedFriendRequests(String appId, String userId) {
        List<FriendRequest> unacceptedRequests = new ArrayList<FriendRequest>();
        FriendRequestStatus[] filterStatuses = {FriendRequestStatus.PENDING, FriendRequestStatus.DECLINED};
        List <FriendAssociation> friendAssociations = FriendAssociation.findFromAssociationsByUser(appId, userId,
                                                                                                   filterStatuses);
        for (FriendAssociation friendAssociation : friendAssociations) {
            // The original request actually came from the friend in the user association, so we should return requests
            // that have the friend as the original requestor and the user as the requested
            unacceptedRequests.add(new FriendRequest(friendAssociation.getId(), friendAssociation.getAppId(),
                                                     friendAssociation.getFriend(), friendAssociation.getUser(),
                                                     friendAssociation.getRequestStatus()));
        }
        return unacceptedRequests;
    }

    public static void createRequest(FriendRequest request) throws DuplicateFriendRequestException {
        // create two associations, one from requestor to requested with an accepted status and one from requested to
        // requestor with a pending status
        final FriendAssociation requestorToRequested =
                new FriendAssociation(request.getAppId(), request.getRequestor(), request.getRequested(),
                                      FriendRequestStatus.ACCEPTED);
        // Verify that the request has not already been made
        if (FriendAssociation.exists(requestorToRequested)) {
            throw new DuplicateFriendRequestException();
        }
        final FriendAssociation requestedToRequestor =
                new FriendAssociation(request.getAppId(), request.getRequested(), request.getRequestor(),
                                      FriendRequestStatus.PENDING);
        // Now save both
        Ebean.execute(new TxRunnable() {
            public void run() {
                requestorToRequested.save();
                requestedToRequestor.save();
            }
        });

        // Notify the requested user
        NotificationService.sendFriendRequest(requestorToRequested);
    }

    public static void accept(long requestId) throws NotFoundException {
        FriendAssociation friendRequest = FriendAssociation.findById(requestId);
        if (friendRequest == null) {
            throw new NotFoundException("No friend request found for id " + requestId);
        }
        friendRequest.setRequestStatus(FriendRequestStatus.ACCEPTED);
        friendRequest.save();
        NotificationService.sendFriendRequestAccepted(friendRequest);
    }

    public static void decline(long requestId) throws NotFoundException {
        FriendAssociation friendRequest = FriendAssociation.findById(requestId);
        if (friendRequest == null) {
            throw new NotFoundException("No friend request found for id " + requestId);
        }
        friendRequest.setRequestStatus(FriendRequestStatus.DECLINED);
        friendRequest.save();
    }

    private static Set<User> getFriendsFromAssociations(List<FriendAssociation> friendAssociations) {
        Set<User> users = new HashSet<User>();
        for (FriendAssociation friendAssociation : friendAssociations) {
            users.add(friendAssociation.getFriend());
        }
        return users;
    }
}
