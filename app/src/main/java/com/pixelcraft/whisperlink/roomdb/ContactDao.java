package com.pixelcraft.whisperlink.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contactentity")
    Flowable<List<ContactEntity>> getAll();
    @Query("SELECT * FROM CONTACTENTITY WHERE userName = :userName")
    Flowable<List<ContactEntity>> getContact(String userName);
    @Insert
    Completable Insert(ContactEntity contactEntity);
    @Delete
    Completable Delete(ContactEntity contactEntity);
}
