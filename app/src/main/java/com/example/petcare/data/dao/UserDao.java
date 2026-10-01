package com.example.petcare.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.petcare.data.entity.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE firebaseUid = :firebaseUid LIMIT 1")
    User getUserByFirebaseUid(String firebaseUid);

    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :password LIMIT 1")
    User login(String email, String password);
}