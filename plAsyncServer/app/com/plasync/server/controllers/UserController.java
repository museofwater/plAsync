package com.plasync.server.controllers;

import com.plasync.server.model.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 6/26/13
 * Time: 8:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserController extends Controller {

    @With(AppKeyController.class)
    public static Result getAllUsers() {
        return play.mvc.Results.TODO;
    }

    public static Result getUser(Long id) {
        return play.mvc.Results.TODO;
    }

//    public static Result createUser(String openId, String username) {
//        User newUser = new User();
//        newUser.openId = openId;
//        newUser.username = username;
//        try
//
//    }
}
