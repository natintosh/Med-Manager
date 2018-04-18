package com.example.oluwatobiloba.medmanager.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.oluwatobiloba.medmanager.models.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE userId IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE name LIKE :name LIMIT 1")
    User findByName(String name);

    @Query("SELECT * FROM user WHERE userId LIKE :id")
    User findById(String id);

    @Insert
    void insertUser(User user);

    @Insert
    void insertMultipleUsers(User... users);

    @Delete
    void delete(User user);
}
