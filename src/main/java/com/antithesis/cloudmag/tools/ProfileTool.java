package com.antithesis.cloudmag.tools;

import com.google.common.base.Strings;

public final class ProfileTool {

    private static final String DEFAULT_ENV_PROFILE = "PROFILE";
    private static final String ENV_ACTIVE_PROFILE = "ACTIVE_PROFILE";

    private ProfileTool() {
    }

    public static String getProfileValue(){
        String getenv = System.getenv(DEFAULT_ENV_PROFILE);
        if (Strings.isNullOrEmpty(getenv)) {
            return "local";
        }
        return getenv;
    }

    public static void setLoaderProfile() {
        System.setProperty(ENV_ACTIVE_PROFILE, getProfileValue());
    }
}
