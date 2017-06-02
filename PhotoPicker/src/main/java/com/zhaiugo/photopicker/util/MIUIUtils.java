package com.zhaiugo.photopicker.util;

import java.io.IOException;

public final class MIUIUtils {

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private static final String KEY_MIUI_V6_NAME = "V6";
    private static final String KEY_MIUI_V7_NAME = "V7";
    private static final String KEY_MIUI_V8_NAME = "V8";

    public static boolean isMIUI() {
        try {
            BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isMIUIV6() {
        try {
            BuildProperties prop = BuildProperties.newInstance();
            if (KEY_MIUI_V6_NAME.equals(prop.getProperty(KEY_MIUI_VERSION_NAME, null))
                    || KEY_MIUI_V7_NAME.equals(prop.getProperty(KEY_MIUI_VERSION_NAME, null))
                    || KEY_MIUI_V8_NAME.equals(prop.getProperty(KEY_MIUI_VERSION_NAME, null))) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
