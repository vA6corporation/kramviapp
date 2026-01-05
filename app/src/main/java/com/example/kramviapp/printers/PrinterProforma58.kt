package com.example.kramviapp.printers

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.connection.tcp.TcpConnection
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.exceptions.EscPosEncodingException
import com.dantsu.escposprinter.exceptions.EscPosParserException
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.example.kramviapp.enums.CurrencyCodeType
import com.example.kramviapp.models.BusinessModel
import com.example.kramviapp.models.CustomerModel
import com.example.kramviapp.models.OfficeModel
import com.example.kramviapp.models.ProformaItemModel
import com.example.kramviapp.models.ProformaModel
import com.example.kramviapp.models.SettingModel
import com.example.kramviapp.utils.formatDate

class PrinterProforma58(
    private val proforma: ProformaModel,
    private val proformaItems: List<ProformaItemModel>,
    private val customer: CustomerModel?,
    private val office: OfficeModel,
    private val setting: SettingModel,
    private val business: BusinessModel,
) {
    private var printTries = 0

    @SuppressLint("DefaultLocale")
    private fun buildBody(printer: EscPosPrinter): StringBuilder {
        var proformaSerial = "P${office.serialPrefix}-${proforma.proformaNumber}"
        val body = StringBuilder()

        if (setting.logo.isNotEmpty()) {
            val decodedString: ByteArray = Base64.decode(setting.logo.substring(22), Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            body.append("[C]<img>")
                .append(PrinterTextParserImg.bitmapToHexadecimalString(printer, decodedByte))
                .append("</img>\n")
            body.append("[L]\n")
        }

        body.append("[C]<font size='tall'>${office.tradeName.uppercase()}</font>\n")
        body.append("[C]${business.businessName.uppercase()}\n")
        body.append("[C]RUC:${business.ruc}\n")

        body.append("[C]${office.address}\n")

        if (office.mobileNumber.isNotEmpty()) {
            body.append("[C]${office.mobileNumber}\n")
        }

        body.append("[C]PROFORMA\n")
        body.append("[C]$proformaSerial\n")
        body.append("[C]<font size='normal'>--------------------------</font>\n")
        body.append("[L]Fecha: ").append(formatDate(proforma.createdAt)).append("\n")
        body.append("[L]Cliente: ").append(customer?.name ?: "VARIOS").append("\n")

        customer?.let {
            if (customer.address.isNotEmpty()) {
                body.append("[L]Direccion: ").append(customer.address).append("\n")
            }
            if (customer.document.isNotEmpty()) {
                body.append("[L]RUC/DNI: ").append(customer.document).append("\n")
            }
        }

        body.append("[C]<font size='normal'>--------------------------</font>\n")
        body.append("[L]Descripcion[R]Importe").append("\n")
        for (proformaItem in proformaItems) {
            body.append("[L]").append(proformaItem.fullName.uppercase()).append("\n")
            body.append("[L]").append("x").append(proformaItem.quantity).append("[R]")
                .append(String.format("%.2f", proformaItem.price * proformaItem.quantity))
                .append("\n")
        }

        body.append("[C]SON: ").append(proforma.chargeLetters).append("\n")

        val currency = if (proforma.currencyCode == CurrencyCodeType.SOLES) "S/" else "$"

        if (proforma.gravado > 0) {
            body.append("[L]OP. GRAVADAS ").append(currency).append("[R]")
                .append(String.format("%.2f", proforma.gravado)).append("\n")
        }
        if (proforma.gratuito > 0) {
            body.append("[L]OP. GRATUITAS ").append(currency).append("[R]")
                .append(String.format("%.2f", proforma.gratuito)).append("\n")
        }
        if (proforma.exonerado > 0) {
            body.append("[L]OP. EXONERADAS ").append(currency).append("[R]")
                .append(String.format("%.2f", proforma.exonerado)).append("\n")
        }
        if (proforma.inafecto > 0) {
            body.append("[L]OP. INAFECTAS ").append(currency).append("[R]")
                .append(String.format("%.2f", proforma.inafecto)).append("\n")
        }
        body.append("[L]IGV ").append(currency).append("[R]")
            .append(String.format("%.2f", proforma.igv)).append("\n")

        body.append("[L]TOTAL DCTO ").append(currency).append("[R]").append(proforma.discount)
            .append("\n")
        body.append("[L]IMPORTE TOTAL ").append(currency).append("[R]")
            .append(String.format("%.2f", proforma.charge)).append("\n")

        if (proforma.observations.isNotEmpty()) {
            body.append("[C]").append(proforma.observations).append("\n")
            body.append("[L]\n")
        }

        if (setting.textSale.isNotEmpty()) {
            body.append("[C]").append(setting.textSale).append("\n")
            body.append("[L]\n")
        }

        body.append("[C]\n")
        body.append("[C]\n")

        return body
    }

    fun printEthernet(ipAddress: String) {
        Thread {
            try {
                val printer = EscPosPrinter(TcpConnection(ipAddress, 9100), 203, 48f, 32)
                val body = buildBody(printer)
                printer.printFormattedTextAndOpenCashBox(body.toString(), 50f)
                printer.disconnectPrinter()
            } catch (e: EscPosConnectionException) {
                e.printStackTrace()
                if (printTries < 3000) {
                    this.printEthernet(ipAddress)
                    printTries++
                }
            } catch (e: EscPosBarcodeException) {
                e.printStackTrace()
            } catch (e: EscPosEncodingException) {
                e.printStackTrace()
            } catch (e: EscPosParserException) {
                e.printStackTrace()
            }
        }.start()
    }

    fun printBluetooth() {
        Thread {
            try {
                val printer = EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32)
                val body = buildBody(printer)
                printer.printFormattedTextAndOpenCashBox(body.toString(), 50f)
                printer.disconnectPrinter()
            } catch (e: EscPosConnectionException) {
                e.printStackTrace()
                if (printTries < 3000) {
                    this.printBluetooth()
                    printTries++
                }
            } catch (e: EscPosBarcodeException) {
                e.printStackTrace()
            } catch (e: EscPosEncodingException) {
                e.printStackTrace()
            } catch (e: EscPosParserException) {
                e.printStackTrace()
            }
        }.start()
    }

}