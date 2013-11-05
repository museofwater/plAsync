package org.plasync.server.service;

import org.apache.commons.lang3.StringUtils;
import org.plasync.server.InvalidAppSpecificationException;
import org.plasync.server.models.App;
import org.plasync.server.models.DeviceType;
import org.plasync.server.models.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/4/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppService {

    public static void addApp(App app) throws InvalidAppSpecificationException {
        // Do device specific app handling
        if (app.getDeviceType() == DeviceType.ANDROID) {
            AndroidAppService.addApp(app);
        }
        else if (app.getDeviceType() == DeviceType.IOS) {
            // do ios specific registration, i.e. notification key
        }
        else if (app.getDeviceType() == DeviceType.FACEBOOK) {
            // do facebook specific registration
        }

        // Add the app to the database
        app.save();
    }

    public static List<User> searchUsers(String appId, String search) {
        Set<User> users = new HashSet<User>();
        List<App> apps = App.findByAppId(appId, search);
        for (App app : apps) {
            users.add(app.getUser());
        }
        return new ArrayList<User>(users);
    }
}
