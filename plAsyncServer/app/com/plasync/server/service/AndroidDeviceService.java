package com.plasync.server.service;

import com.plasync.server.models.Device;
import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/5/13
 * Time: 6:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class AndroidDeviceService {


    public static void addDevice(Device device) throws InvalidDeviceSpecificationException {
        if (StringUtils.isEmpty(device.getGcmId())) {
            throw new InvalidDeviceSpecificationException("GCM ID cannot be null for android device");
        }
    }
}
