package com.plasync.server.service;

import com.plasync.server.models.App;
import com.plasync.server.models.DeviceType;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/4/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppService {

    public static void addApp(App app) throws InvalidAppSpecificationException {
        if (app.getDeviceType() == DeviceType.ANDROID) {
            AndroidAppService.addApp(app);
        }
        else if (app.getDeviceType() == DeviceType.IOS) {
            // do ios specific registration, i.e. notification key
        }
        else if (app.getDeviceType() == DeviceType.FACEBOOK) {
            // do facebook specific registration
        }
    }
}
