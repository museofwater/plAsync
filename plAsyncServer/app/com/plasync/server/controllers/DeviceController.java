package com.plasync.server.controllers;

import com.plasync.server.models.Device;
import com.plasync.server.service.DeviceService;
import com.plasync.server.service.InvalidDeviceSpecificationException;
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
public class DeviceController extends Controller {

    public static Result addDevice() {
        Device newDevice = Json.fromJson(request().body().asJson(), Device.class);
        try {
            DeviceService.addDevice(newDevice);
        }
        catch (InvalidDeviceSpecificationException ex) {
            return badRequest(ex.getMessage());
        }
        return ok();
    }
}
