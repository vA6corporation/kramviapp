package com.example.kramviapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.kramviapp.enums.PrinterType

@Entity(tableName = "printers")
class PrinterModel {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "name")
    var name: String = ""

    @ColumnInfo(name = "printerType")
    var printerType: PrinterType = PrinterType.BLUETOOTH58

    @ColumnInfo(name = "printInvoice")
    var printInvoice: Boolean = false

    @ColumnInfo(name = "printProforma")
    var printProforma: Boolean = false

    @ColumnInfo(name = "printAccount")
    var printAccount: Boolean = false

    @ColumnInfo(name = "printKitchen")
    var printKitchen: Boolean = false

    @ColumnInfo(name = "printBar")
    var printBar: Boolean = false

    @ColumnInfo(name = "printOven")
    var printOven: Boolean = false

    @ColumnInfo(name = "printBox")
    var printBox: Boolean = false

    @ColumnInfo(name = "ipAddress")
    var ipAddress: String = ""

}
