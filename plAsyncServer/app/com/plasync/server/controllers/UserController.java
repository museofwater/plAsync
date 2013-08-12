package com.plasync.server.controllers;

import com.plasync.server.models.User;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

import static play.data.Form.form;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 6/26/13
 * Time: 8:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserController extends Controller {
    private static final String PLASYNC_SERVER_USERNAME_COOKIE = "com.plasync.server.username";
    private static final String PLASYNC_SERVER_USERID_COOKIE = "com.plasync.server.userId";

    /* User CRUD operations */

//    @With(AppKeyController.class)
    public static Result getAllUsers() {
        return ok(Json.toJson(User.findAllUsers()));
//        return play.mvc.Results.TODO;
    }

//    @With(AppKeyController.class)
    public static Result getUser(String openId) {
        return ok(Json.toJson(User.findById(openId)));
    }

//    @With(AppKeyController.class)
    public static Result createUser() {
        String id = form().bindFromRequest().get("id");
        String username = form().bindFromRequest().get("username").trim();
        if (!User.exists(username)) {
            User newUser = new User(id);
            newUser.username = username;
            newUser.save();
            return redirect(com.plasync.server.controllers.routes.UserController.welcome(username, id, true));
        }
        else {
            return redirect(com.plasync.server.controllers.routes.UserController.signup(id));
        }
    }

    /* Signup/Signin support methods */
//    @With(AppKeyController.class)
    public static Result signup(String id) {
        User user =  User.findById(id);
        if (user == null) {
            Form<User> userForm = form(User.class);
            User newUser = new User(id);
            userForm = userForm.fill(newUser);
            // Prevent caching of page since that would allow a user to submit a username for an already registered ID
            response().setHeader("Cache-Control", "private, no-cache, no-store");
            return ok(views.html.newUser.render(userForm));
        }
        else {
            // If user already is registered, redirect to welcome
            return redirect(com.plasync.server.controllers.routes.UserController.welcome(user.username, id, false));
        }
    }

//    @With(AppKeyController.class)
    public static Result available(String username) {
        return ok(Json.toJson(!User.exists(username)));
    }

    public static Result welcome(String username, String userId, boolean newUser) {
        response().setCookie(PLASYNC_SERVER_USERNAME_COOKIE, username);
        response().setCookie(PLASYNC_SERVER_USERID_COOKIE, userId);
        return ok(views.html.welcome.render(username, newUser));
    }

    /* Services to support testing.  -- REMOVE IN PRODUCTION */
    public static Result clearUsers() {
        List<User> users = User.findAllUsers();
        for (User user : users) {
            user.delete();
        }
        return ok();
    }



}
