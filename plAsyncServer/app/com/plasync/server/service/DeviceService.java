package com.plasync.server.service;

import com.plasync.server.models.Device;
import com.plasync.server.models.DeviceType;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/4/13
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeviceService {

    public static void addDevice(Device device) throws InvalidDeviceSpecificationException {
        if (device.getType() == DeviceType.ANDROID) {
            AndroidDeviceService.addDevice(device);
        }
        else if (device.getType() == DeviceType.IOS) {
            // do ios specific registration, i.e. notification key
        }
        else if (device.getType() == DeviceType.FACEBOOK) {
            // do facebook specific registration
        }
    }
}
