package org.plasync.server.controllers;

import org.plasync.server.models.User;
import play.libs.F;
import play.libs.OpenID;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.HashMap;
import java.util.Map;

import static play.data.Form.form;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 7/3/13
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class SigninController extends Controller {
    public static Result signin() {
        return ok(views.html.signin.render());
    }

    public static Result authenticate() {
        // Set up some attributes to return from the provider
        Map<String, String> attributes = new HashMap<String, String>();
        String provider = form().bindFromRequest().get("openIdProvider");
        if (provider.equalsIgnoreCase("google")) {
            // Need email for google, because google openids are domain specific.
            // See http://blog.stackoverflow.com/2010/04/openid-one-year-later/
            attributes.put("Email", "http://schema.openid.net/contact/email");
        }

        // Get the open id url from the request
        String openIdUrl = form().bindFromRequest().get("openid_identifier");

//        String returnToUrl = "http://localhost:9000/signin/verify";
        String returnToUrl = routes.SigninController.verify(provider).absoluteURL(request());
        F.Promise<String> redirectUrl = OpenID.redirectURL(openIdUrl, returnToUrl, attributes);
        return redirect(redirectUrl.get());

    }

    public static Result verify(String provider) {
        F.Promise<OpenID.UserInfo> userInfoPromise = OpenID.verifiedId();
        OpenID.UserInfo userInfo = userInfoPromise.get();
        String userId;
        if (provider.equalsIgnoreCase("google")) {
            // Use email for google instead of open id, because google openids are domain specific.
            // See http://blog.stackoverflow.com/2010/04/openid-one-year-later/
            userId = userInfo.attributes.get("Email");
        }
        else {
            userId = userInfo.id;
        }
        // Get the user
        User user = User.findById(userId);
        if (user == null) {
            return redirect(org.plasync.server.controllers.routes.UserController.signup(userId));
        }
        return redirect(org.plasync.server.controllers.routes.UserController.welcome(
                user.getUsername(), userId, user.getGravatarEmailHash(), false));
    }
}
