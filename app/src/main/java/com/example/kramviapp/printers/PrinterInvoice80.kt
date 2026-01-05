package com.example.kramviapp.printers

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.connection.tcp.TcpConnection
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.exceptions.EscPosEncodingException
import com.dantsu.escposprinter.exceptions.EscPosParserException
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.example.kramviapp.enums.CurrencyCodeType
import com.example.kramviapp.enums.InvoiceType
import com.example.kramviapp.models.BusinessModel
import com.example.kramviapp.models.CustomerModel
import com.example.kramviapp.models.OfficeModel
import com.example.kramviapp.models.SaleItemModel
import com.example.kramviapp.models.SaleModel
import com.example.kramviapp.models.SettingModel
import com.example.kramviapp.utils.formatDate

class PrinterInvoice80(
    private val sale: SaleModel,
    private val saleItems: List<SaleItemModel>,
    private val customer: CustomerModel?,
    private val office: OfficeModel,
    private val setting: SettingModel,
    private val business: BusinessModel,
) {
    companion object {
        private var printTries = 0
        fun printBluetoothTest() {
            Thread {
                try {
                    val printer = EscPosPrinter(
                        BluetoothPrintersConnections.selectFirstPaired(),
                        203,
                        72f,
                        48
                    )
                    val body = StringBuilder()
                    body.append("[C]<font size='tall'>KRAMVI FACTURACION Y VENTA</font>\n")
                    body.append("[L]\n")
                    body.append("[C]<font size='normal'>------------------------------------------------</font>\n")
                    body.append("[L]\n")
                    body.append("[C]<font size='tall'>ESTO ES UNA PRUEBA</font>\n")
                    body.append("[L]\n")
                    body.append("[C]<font size='tall'>GRACIAS POR SU PREFERENCIA</font>\n")
                    body.append("[L]\n")
                    body.append("[L]\n")

                    printer.printFormattedTextAndCut(body.toString())
                    printer.disconnectPrinter()
                } catch (e: EscPosConnectionException) {
                    e.printStackTrace()
                    if (printTries < 3000) {
                        this.printBluetoothTest()
                        printTries++
                    } else {
                        printTries = 0
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

        fun printEthernetTest(ipAddress: String) {
            Log.e("printerTries", printTries.toString())
            Thread {
                try {
                    val printer = EscPosPrinter(TcpConnection(ipAddress, 9100), 203, 72f, 48)
                    val body = StringBuilder()
                    body.append("[C]<font size='tall'>KRAMVI FACTURACION Y VENTA</font>\n")
                    body.append("[L]\n")
                    body.append("[C]<font size='normal'>------------------------------------------------</font>\n")
                    body.append("[L]\n")
                    body.append("[C]<font size='tall'>ESTO ES UNA PRUEBA</font>\n")
                    body.append("[L]\n")
                    body.append("[C]<font size='tall'>GRACIAS POR SU PREFERENCIA</font>\n")
                    body.append("[L]\n")
                    body.append("[L]\n")

                    printer.printFormattedTextAndCut(body.toString())
                    printer.disconnectPrinter()
                } catch (e: EscPosConnectionException) {
                    e.printStackTrace()
                    if (printTries < 3000) {
                        this.printEthernetTest(ipAddress)
                        printTries++
                    } else {
                        printTries = 0
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

    private var printTries = 0

    @SuppressLint("DefaultLocale")
    private fun buildBody(printer: EscPosPrinter): StringBuilder {
        var invoiceTitle = ""
        var invoiceSerial = ""
        val body = StringBuilder()

        when (sale.invoiceType) {
            InvoiceType.FACTURA -> {
                invoiceTitle = "FACTURA ELECTRONICA"
                invoiceSerial = "F" + office.serialPrefix + "-" + sale.invoiceNumber
            }

            InvoiceType.BOLETA -> {
                invoiceTitle = "BOLETA ELECTRONICA"
                invoiceSerial = "B" + office.serialPrefix + "-" + sale.invoiceNumber
            }

            InvoiceType.NOTA_DE_VENTA -> {
                invoiceTitle = "NOTA DE VENTA"
                invoiceSerial = "N" + office.serialPrefix + "-" + sale.invoiceNumber
            }
        }

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

        if (sale.invoiceType != InvoiceType.NOTA_DE_VENTA || setting.showAddressOnTicket) {
            body.append("[C]${office.address}\n")
        }

        if (office.mobileNumber.isNotEmpty()) {
            body.append("[C]${office.mobileNumber}\n")
        }

        body.append("[C]$invoiceTitle\n")
        body.append("[C]$invoiceSerial\n")
        body.append("[C]<font size='normal'>------------------------------------------------</font>\n")
        body.append("[L]Fecha: ").append(formatDate(sale.createdAt)).append("\n")
        body.append("[L]Cliente: ").append(customer?.name ?: "VARIOS").append("\n")

        customer?.let {
            if (customer.address.isNotEmpty()) {
                body.append("[L]Direccion: ").append(customer.address).append("\n")
            }
            if (customer.document.isNotEmpty()) {
                body.append("[L]RUC/DNI: ").append(customer.document).append("\n")
            }
        }

        body.append("[C]<font size='normal'>------------------------------------------------</font>\n")
        body.append("[L]Descripcion[C]Precio U.[R]Importe").append("\n")
        for (saleItem in saleItems) {
            body.append("[L]").append(saleItem.fullName.uppercase()).append("\n")
            body.append("[L]").append(saleItem.quantity).append("[C]")
                .append(String.format("%.2f", saleItem.price))
                .append("[R]").append(String.format("%.2f", saleItem.price * saleItem.quantity))
                .append("\n")
        }

        body.append("[C]SON: ").append(sale.chargeLetters).append("\n")

        val currency = if (sale.currencyCode == CurrencyCodeType.SOLES) "S/" else "$"

        if (sale.invoiceType != InvoiceType.NOTA_DE_VENTA) {
            if (sale.gravado > 0) {
                body.append("[L][R]OP. GRAVADAS ").append(currency).append("[R]")
                    .append(String.format("%.2f", sale.gravado))
                    .append("\n")
            }
            if (sale.gratuito > 0) {
                body.append("[L][R]OP. GRATUITAS ").append(currency).append("[R]")
                    .append(String.format("%.2f", sale.gratuito))
                    .append("\n")
            }
            if (sale.exonerado > 0) {
                body.append("[L][R]OP. EXONERADAS ").append(currency).append("[R]")
                    .append(String.format("%.2f", sale.exonerado))
                    .append("\n")
            }
            if (sale.inafecto > 0) {
                body.append("[L][R]OP. INAFECTAS ").append(currency).append("[R]")
                    .append(String.format("%.2f", sale.inafecto))
                    .append("\n")
            }
            body.append("[L][R]IGV ").append(currency).append("[R]")
                .append(String.format("%.2f", sale.igv)).append("\n")
        }

        body.append("[L][R]TOTAL DCTO ").append(currency).append("[R]")
            .append(sale.discount).append("\n")
        body.append("[L][R]IMPORTE TOTAL ").append(currency).append("[R]")
            .append(String.format("%.2f", sale.charge)).append("\n")

        if (sale.cash != null && sale.cash > 0) {
            body.append("[L][R]BILLETE ").append(currency).append("[R]")
                .append(String.format("%.2f", sale.cash))
                .append("\n")
            body.append("[L][R]VUELTO ").append(currency).append("[R]").append(String.format("%.2f", sale.cash - sale.charge))
                .append("\n")
        }

        if (sale.isCredit) {
            var totalPayments = 0.0
            for (payment in sale.payments) {
                totalPayments += payment.charge
            }
            val diff = sale.charge - totalPayments
            body.append("[L]SALDO ").append(currency).append("[R]")
                .append(String.format("%.2f", diff)).append("\n")
        }

        if (sale.invoiceType != InvoiceType.NOTA_DE_VENTA && setting.showQrCode) {
            val qr = StringBuilder()
            qr.append(business.ruc)
                .append("|")
                .append(sale.invoiceType.toString())
                .append("|")
                .append(sale.invoicePrefix)
                .append(office.serialPrefix)
                .append("|")
                .append(sale.invoiceNumber)
                .append("|")
                .append(String.format("%.2f", sale.igv))
                .append("|")
                .append(String.format("%.2f", sale.charge))
                .append("|")
                .append(formatDate(sale.createdAt))
            if (customer != null) {
                qr.append("|")
                    .append(customer.documentType)
                    .append("|")
                    .append(customer.document)
            }
            body.append("[C]<qrcode size='20'>").append(qr).append("</qrcode>").append("\n")
        }

        when (sale.invoiceType) {
            InvoiceType.FACTURA -> {
                body.append("[C]").append("AUTORIZADO MEDIANTE RESOLUCION NRO.")
                    .append("\n")
                body.append("[C]")
                    .append("0180050001442/SUNAT Representacion impresa")
                    .append("\n")
                body.append("[C]").append("de la FACTURA DE VENTA\n")
                body.append("[L]\n")
            }

            InvoiceType.BOLETA -> {
                body.append("[C]").append("AUTORIZADO MEDIANTE RESOLUCION NRO.")
                    .append("\n")
                body.append("[C]")
                    .append("0180050001442/SUNAT Representacion impresa")
                    .append("\n")
                body.append("[C]").append("de la BOLETA DE VENTA").append("\n")
                body.append("[L]\n")
            }

            InvoiceType.NOTA_DE_VENTA -> {}
        }

        if (sale.observations.isNotEmpty()) {
            body.append("[C]").append(sale.observations).append("\n")
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

    fun printEthernet(
        ipAddress: String,
    ) {
        Thread {
            try {
                val printer = EscPosPrinter(TcpConnection(ipAddress, 9100), 203, 72f, 48)
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
                val printer = EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 72f, 48)
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