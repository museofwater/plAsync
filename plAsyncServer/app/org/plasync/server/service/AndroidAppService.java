package org.plasync.server.service;

import org.plasync.server.InvalidAppSpecificationException;
import org.plasync.server.models.App;
import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/5/13
 * Time: 6:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class AndroidAppService {


    public static void addApp(App app) throws InvalidAppSpecificationException {
        if (StringUtils.isEmpty(app.getGcmId())) {
            throw new InvalidAppSpecificationException("GCM ID cannot be null for android app");
        }
    }
}
