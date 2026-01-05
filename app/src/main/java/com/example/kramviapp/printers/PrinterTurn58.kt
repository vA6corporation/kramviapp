package com.example.kramviapp.printers

import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.connection.tcp.TcpConnection
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.exceptions.EscPosEncodingException
import com.dantsu.escposprinter.exceptions.EscPosParserException
import com.example.kramviapp.models.ExpenseModel
import com.example.kramviapp.models.SummaryPaymentModel
import com.example.kramviapp.models.SummarySaleItemModel
import com.example.kramviapp.models.TurnModel
import com.example.kramviapp.models.UserModel
import com.example.kramviapp.utils.formatDate

class PrinterTurn58(
    private val turn: TurnModel,
    private val expenses: List<ExpenseModel>,
    private val summaryPayments: List<SummaryPaymentModel>,
    private val summarySaleItems: List<SummarySaleItemModel>,
    private val user: UserModel,
) {
    private var printTries = 0
    private fun buildBody(): StringBuilder {
        val body = java.lang.StringBuilder()
        val totalCollected = 0.0
        var totalCash = 0.0
        var totalExpenses = 0.0

        for (expense in expenses) {
            totalExpenses += expense.charge
        }

        body.append("[C]").append("ESTADO DE CAJA").append("\n")
        body.append("[C]").append(formatDate(turn.createdAt)).append("\n")
        body.append("[C]").append(user.name).append("\n")

        for (expense in expenses) {
            body.append("[L]").append(expense.concept.uppercase()).append("\n")
            body.append("[L]").append(String.format("%.2f", expense.charge))
                .append("\n")
        }

        body.append("[L]\n")

        for (summaryPayment in summaryPayments) {
            if (summaryPayment.paymentMethod.equals("EFECTIVO")) {
                totalCash = summaryPayment.totalCharge
            }
            body.append("[L]").append(summaryPayment.paymentMethod.name).append("(")
                .append(summaryPayment.totalQuantity).append(")").append("[R]")
                .append(String.format("%.2f", summaryPayment.totalCharge))
                .append("\n")
        }

        body.append("[L]\n")

        body.append("[L]T. RECAUDADO:").append("[R]")
            .append(String.format("%.2f", totalCollected)).append("\n")
        body.append("[L]T. GASTOS:").append("[R]").append(String.format("%.2f", totalExpenses))
            .append("\n")
        body.append("[L]M. DE APERTURA:").append("[R]")
            .append(String.format("%.2f", turn.openCash)).append("\n")
        body.append("[L]E. RESTANTE:").append("[R]").append(
            String.format("%.2f", totalCash + turn.openCash - totalExpenses)
        ).append("\n")

        if (turn.observations.isNotEmpty()) {
            body.append("[L]\n")
            body.append("[C]").append(turn.observations).append("\n")
        }

        body.append("[L]\n")

        for (summarySaleItem in summarySaleItems) {
            body.append("[L]").append(summarySaleItem.fullName.uppercase()).append("\n")
            body.append("[L]").append(summarySaleItem.totalQuantity).append("[R]")
                .append(String.format("%.2f", summarySaleItem.totalSale))
                .append("\n")
        }

        body.append("[C]\n")
        body.append("[C]\n")
        return body
    }

    fun printBluetooth() {
        Thread {
            try {
                val printer = EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32)
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
                val printer = EscPosPrinter(TcpConnection(ipAddress, 9100), 203, 48f, 32)
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