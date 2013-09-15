package org.plasync.server.controllers;

import org.plasync.server.models.App;
import org.plasync.server.service.AppService;
import org.plasync.server.service.InvalidAppSpecificationException;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/5/13
 * Time: 6:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class AppController extends Controller {

    public static Result addApp() {
        App newApp = Json.fromJson(request().body().asJson(), App.class);
        try {
            AppService.addApp(newApp);
        }
        catch (InvalidAppSpecificationException ex) {
            return badRequest(ex.getMessage());
        }
        return ok();
    }
}
