package com.example.kramviapp.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<UserModel>

    @Insert
    suspend fun insert(user: UserModel)

    @Delete
    suspend fun delete(user: UserModel)
}