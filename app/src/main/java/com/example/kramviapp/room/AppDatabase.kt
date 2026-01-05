package com.example.kramviapp.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PrinterModel::class, UserModel::class],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun printerDao(): PrinterDao
    abstract fun userDao(): UserDao
}
