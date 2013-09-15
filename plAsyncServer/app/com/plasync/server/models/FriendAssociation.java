package com.plasync.server.models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Junction;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 8/30/13
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Represents a friendship association between two users.  Each friendship is represented by two associations.
 * A friendship association exists when both associations had a status of accepted.  Pending friendship associations
 * will have one association with a status of accepted (the requestor) and one with a status of pending.  Previously
 * declined requests will also be returned as pending so that the receiving user may choose to accept them at a later
 * date.
 */
@Entity
public class FriendAssociation extends Model {

    public static Finder<Long,FriendAssociation> find = new Finder<Long,FriendAssociation>(
            Long.class, FriendAssociation.class
    );

    @Id
    private long id;

    @OneToOne
    private User user;

    @OneToOne
    private User friend;

    /**
     * Identifies the app for which the friend association is relevant
     */
    private String appId;

    private FriendRequestStatus requestStatus = FriendRequestStatus.PENDING;

    public static FriendAssociation findById(long id)
    {
        return find.byId(id);
    }

    /**
     * Returns all associations for which this user is the user, filtered by the specified statuses
     * @param userId
     * @param filterStatuses
     * @return
     */
    public static List<FriendAssociation> findFromAssociationsByUser(String appId, String userId,
                                                                     FriendRequestStatus... filterStatuses) {
        ExpressionList<FriendAssociation> el = find
                .where()
                .eq("user.id", userId)
                .eq("appId", appId);
        // If there is more then one filter status then create an "or" junction
        Junction<FriendAssociation> junction = null;
        if (filterStatuses.length > 1) {
            junction = el.disjunction();
        }
        for (FriendRequestStatus filterStatus : filterStatuses) {
            if (junction != null) {
                junction.eq("requestStatus", filterStatus.ordinal());
            }
            else {
                el.eq("requestStatus", filterStatus.ordinal());
            }
        }
        if (filterStatuses.length > 1) {
            junction.endJunction();
        }
        return el.findList();
    }

    /**
     * Returns all associations for which this user is the friend, filtered by the specified statuses
     * @param userId
     * @param filterStatuses
     * @return
     */
    public static List<FriendAssociation> findToAssociationsByUser(String appId, String userId,
                                                                   FriendRequestStatus... filterStatuses) {
        ExpressionList<FriendAssociation> el = find
                .where()
                .eq("friend.id", userId)
                .eq("appId", appId);
        // If there is more then one filter status then create an "or" junction
        Junction<FriendAssociation> junction = null;
        if (filterStatuses.length > 1) {
            junction = el.disjunction();
        }
        for (FriendRequestStatus filterStatus : filterStatuses) {
            if (junction != null) {
                junction.eq("requestStatus", filterStatus.ordinal());
            }
            else {
                el.eq("requestStatus", filterStatus.ordinal());
            }
        }
        if (filterStatuses.length > 1) {
            junction.endJunction();
        }
        return el.findList();
    }

    public static boolean exists(FriendAssociation friendAssociation) {
        return find
                .where()
                .eq("appId", friendAssociation.getAppId())
                .eq("user", friendAssociation.getUser())
                .eq("friend", friendAssociation.getFriend())
                .findRowCount() > 0;
    }

    public FriendAssociation(String appId, User user, User friend, FriendRequestStatus status) {
        this.appId = appId;
        this.user = user;
        this.friend = friend;
        this.requestStatus = status;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public FriendRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(FriendRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }


}
