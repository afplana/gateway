package com.moap.gateway.utils;

import com.moap.gateway.entity.Gateway;

import java.util.regex.Pattern;

public class Ipv4Utils {

    private Ipv4Utils() {
        //prevents instantiation
    }

    public static boolean validIPv4(String ipv4) {
        String regex = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        Pattern valid = Pattern.compile(regex);
        return valid.pattern().matches(ipv4);
    }
}
