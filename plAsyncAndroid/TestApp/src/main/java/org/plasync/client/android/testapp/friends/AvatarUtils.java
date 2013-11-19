package org.plasync.client.android.testapp.friends;

/**
 * Created by ericwood on 10/24/13.
 */
public class AvatarUtils {

    private static final String GRAVATAR_BASE_URL = "http://www.gravatar.com/avatar/";
    private static final String GRAVATAR_DEFAULT_PARAM = "d=identicon";

    public static String getUrl(String gravatarEmailHash) {
        return GRAVATAR_BASE_URL + gravatarEmailHash + "?" + GRAVATAR_DEFAULT_PARAM;
    }
}
