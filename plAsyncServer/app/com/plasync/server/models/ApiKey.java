package com.plasync.server.models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/6/13
 * Time: 6:17 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class ApiKey extends Model {

    public static Finder<Long,ApiKey> find = new Finder<Long,ApiKey>(
            Long.class, ApiKey.class
    );

    @Id
    private Long id;

    @Column(unique=true)
    private String key;


}
