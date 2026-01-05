package com.example.kramviapp.printers

import android.util.Log
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.connection.tcp.TcpConnection
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.exceptions.EscPosEncodingException
import com.dantsu.escposprinter.exceptions.EscPosParserException
import com.example.kramviapp.enums.IgvCodeType
import com.example.kramviapp.models.BoardItemModel
import com.example.kramviapp.models.BoardModel
import com.example.kramviapp.models.SettingModel
import com.example.kramviapp.models.TableModel
import com.example.kramviapp.models.UserModel
import com.example.kramviapp.utils.formatDate
import com.example.kramviapp.utils.formatTime
import org.threeten.bp.Instant

class PrinterCommand80(
    private val table: TableModel,
    private val board: BoardModel,
    private val boardItems: List<BoardItemModel>,
    private val setting: SettingModel,
    private val user: UserModel,
) {
    private var printTries = 0
    private fun buildBody(): StringBuilder {
        val body = StringBuilder()
        var totalCharge = 0.0
        val instantString = Instant.now().toString()

        body.append("[C]<font size='tall'>TICKET ${board.ticketNumber}</font>\n")
        body.append("[C]<font size='tall'>MESA ${table.name}</font>\n")
        body.append("[L]\n")
        for (boardItem in boardItems) {
            body.append("[L]<font size='tall'>").append(String.format("%.0f", boardItem.quantity))
                .append("   ").append(boardItem.fullName.uppercase()).append("</font>\n")
            if (boardItem.observations.isNotEmpty()) {
                body.append("[L]<font size='tall'>- ").append(boardItem.observations).append("</font>\n")
            }
            if (boardItem.igvCode != IgvCodeType.BONIFICACION) {
                totalCharge += boardItem.price * boardItem.quantity
            }
        }
        body.append("[L]").append("\n")
        if (setting.showChargeCommand) {
            body.append("[L]").append("IMPORTE TOTAL: ")
                .append(String.format("%.2f", totalCharge)).append("\n")
            body.append("[L]").append("\n")
        }
        body.append("[C]").append("${formatDate(instantString)} ${formatTime(instantString)}").append("\n")
        body.append("[C]<font size='tall'>").append(user.name).append("</font>\n")
        body.append("[C]\n")
        body.append("[C]\n")

        return body
    }

    fun printBluetooth() {
        Thread {
            try {
                val printer = EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 72f, 48)
                val body = buildBody()
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

    fun printEthernet(ipAddress: String) {
        Thread {
            try {
                val printer = EscPosPrinter(TcpConnection(ipAddress, 9100), 203, 72f, 48)
                val body = buildBody()
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
}