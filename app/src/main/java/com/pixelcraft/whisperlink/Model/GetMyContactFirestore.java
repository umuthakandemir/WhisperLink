package com.pixelcraft.whisperlink.Model;

public class GetMyContactFirestore {
    public String userName;
    public String userStatus;
    //sim,google.sdcard like..
    public String sourceType;
    public String userUID;
    public String userPhoto;
    public GetMyContactFirestore(String userPhoto, String userName, String userStatus, String sourceType, String userUID) {
        this.userName = userName;
        this.userPhoto = userPhoto;
        this.userStatus = userStatus;
        this.sourceType = sourceType;
        this.userUID = userUID;
    }
}
