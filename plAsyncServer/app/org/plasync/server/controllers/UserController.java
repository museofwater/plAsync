package org.plasync.server.controllers;

import org.apache.commons.codec.digest.DigestUtils;
import org.plasync.server.models.User;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
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
    private static final String GRAVATAR_EMAIL_QUERY_PARAMETER = "gravatar-email";
    private static final String USERID_QUERY_PARAMETER = "id";
    private static final String USERNAME_QUERY_PARAMETER = "username";

    private static final String PLASYNC_SERVER_USERNAME_COOKIE = "org.plasync.server.username";
    private static final String PLASYNC_SERVER_USERID_COOKIE = "org.plasync.server.userId";
    private static final String UTF_8 = "UTF-8";


    /* User CRUD operations */

//    @With(AppKeyController.class)
    public static Result getUsers() {
        // Get any query parameters to search by
        String username = request().getQueryString(USERNAME_QUERY_PARAMETER);
        if (username != null) {
            username = username.trim();
            User user = User.findByUsername(username);
            return user != null ? ok(Json.toJson(user)) : notFound();
        }
        return ok(Json.toJson(User.findAllUsers()));
//        return play.mvc.Results.TODO;
    }

//    @With(AppKeyController.class)
    public static Result getUser(String openId) {
        return ok(Json.toJson(User.findById(openId)));
    }

//    @With(AppKeyController.class)
    public static Result createUser() {
        String id = form().bindFromRequest().get(USERID_QUERY_PARAMETER);
        String username = form().bindFromRequest().get(USERNAME_QUERY_PARAMETER).trim();
        String gravatarEmail = form().bindFromRequest().get(GRAVATAR_EMAIL_QUERY_PARAMETER);
        if (!User.exists(username)) {
            User newUser = new User(id);
            newUser.setUsername(username);
            if (gravatarEmail != null && gravatarEmail.trim().length() > 0 ) {
                newUser.setGravatarEmailHash(generateMD5Hash(gravatarEmail));
            }
            else {
                newUser.setGravatarEmailHash("");
            }
            newUser.save();
            return redirect(org.plasync.server.controllers.routes.UserController.welcome(
                    username, id, newUser.getGravatarEmailHash(), true));
        }
        else {
            return redirect(org.plasync.server.controllers.routes.UserController.signup(id));
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
            return redirect(org.plasync.server.controllers.routes.UserController.welcome(
                    user.getUsername(), id, user.getGravatarEmailHash(), false));
        }
    }

//    @With(AppKeyController.class)
    public static Result available(String username) {
        return ok(Json.toJson(!User.exists(username)));
    }

    public static Result welcome(String username, String userId, String gravtarEmailHash, boolean newUser) {
        // Url Encode the cookies in case they have special characters
        String usernameEncoded;
        String userIdEncoded;
        try {
            usernameEncoded = URLEncoder.encode(username, UTF_8);
            userIdEncoded = URLEncoder.encode(userId, UTF_8);
        } catch (UnsupportedEncodingException e) {
            Logger.error("Error encoding cookie values", e);
            // Use unencoded cookies
            usernameEncoded = username;
            userIdEncoded = userId;
        }
        response().setCookie(PLASYNC_SERVER_USERNAME_COOKIE, usernameEncoded);
        response().setCookie(PLASYNC_SERVER_USERID_COOKIE, userIdEncoded);
        return ok(views.html.welcome.render(username, gravtarEmailHash, newUser));
    }

    /* Services to support testing.  -- REMOVE IN PRODUCTION */
    public static Result clearUsers() {
        List<User> users = User.findAllUsers();
        for (User user : users) {
            user.delete();
        }
        return ok();
    }

    private static String generateMD5Hash(String str) {
        return DigestUtils.md5Hex(str);
    }



}
