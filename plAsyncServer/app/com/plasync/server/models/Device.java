package com.plasync.server.models;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/4/13
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.List;

/**
 * Represents a device (Android, iOS, or Facebook)
 */
@Entity
public class Device extends Model {



    @Id
    private long id;

    @OneToOne
    private User user;








}
