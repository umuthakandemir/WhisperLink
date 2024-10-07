package com.pixelcraft.whisperlink.Model;

import java.security.PublicKey;

public class MyContactsModel {
    public String userName;
    public String userPhone;
    public String userStatus;
    //sim,google.sdcard like..
    public String sourceType;

    public MyContactsModel(String userName, String userPhone, String userStatus, String sourceType) {
        this.userName = userName;
        this.userPhone = userPhone;
        this.sourceType = sourceType;
        this.userStatus = userStatus;
    }
}
