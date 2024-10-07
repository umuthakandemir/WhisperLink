package com.pixelcraft.whisperlink.roomdb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ContactEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "userID")
    public String userID;
    @ColumnInfo(name = "userName")
    public String userName;
    @ColumnInfo(name = "userPhoto")
    public String userPhoto;

    public ContactEntity(String userID, String userName, String userPhoto) {
        this.userID = userID;
        this.userName = userName;
        this.userPhoto = userPhoto;
    }
}
