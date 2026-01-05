package com.example.kramviapp.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PrinterDao {

    @Query("SELECT * FROM printers")
    suspend fun getAll(): List<PrinterModel>

    @Insert
    suspend fun insert(printers: PrinterModel)

    @Delete
    suspend fun delete(printer: PrinterModel)
}

