package com.ccms.dinamica.verify;


public class VerifyUser {
    private String userID;

    private long userCode;

    private long signTime;

    VerifyUser(String[] strs) {
        super();
        this.userID = strs[0];
        this.userCode = parselong(strs[1]);

        this.signTime = parselong(strs[2]);
    }

    private static long parselong(String strlong) {
        if (strlong == null) {
            return 0;
        }

        try {
            return Long.parseLong(strlong);
        } catch (Exception e) {
            return 0;
        }
    }

    public String getUserID() {
        return userID;
    }

    public long getUserCode() {
        return userCode;
    }

    public long getSignTime() {
        return signTime;
    }

}
