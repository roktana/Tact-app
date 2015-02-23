package com.tactile.tact.utils.oauth2;

import android.net.Uri;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sebafonseca on 10/21/14.
 */
public class UriFragmentParser {
    /**
     * look for # error fragments and standard url param errors, like the
     * user clicked deny on the auth page
     *
     * @param uri
     * @return
     */
    public static Map<String, String> parse(Uri uri) {
        Map<String, String> retval = parse(uri.getEncodedFragment());
        if (retval.size() == 0) {
            retval = parse(uri.getEncodedQuery());
        }
        return retval;
    }

    public static Map<String, String> parse(String fragmentString) {
        Map<String, String> res = new HashMap<String, String>();
        if (fragmentString == null)
            return res;
        fragmentString = fragmentString.trim();
        if (fragmentString.length() == 0)
            return res;
        String[] params = fragmentString.split("&");
        for (String param : params) {
            String[] parts = param.split("=");
            res.put(URLDecoder.decode(parts[0]),
                    parts.length > 1 ? URLDecoder.decode(parts[1]) : "");
        }
        return res;
    }

    private UriFragmentParser() {
        assert false : "don't construct me!";
    }
}
