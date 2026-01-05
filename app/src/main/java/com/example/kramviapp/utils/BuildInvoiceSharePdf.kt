package com.example.kramviapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Base64
import androidx.core.content.FileProvider
import com.example.kramviapp.enums.CurrencyCodeType
import com.example.kramviapp.enums.InvoiceType
import com.example.kramviapp.models.BusinessModel
import com.example.kramviapp.models.CustomerModel
import com.example.kramviapp.models.OfficeModel
import com.example.kramviapp.models.SaleItemModel
import com.example.kramviapp.models.SaleModel
import com.example.kramviapp.models.SettingModel
import com.itextpdf.barcodes.BarcodeQRCode
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import java.io.File
import java.util.Locale

class BuildInvoiceSharePdf(
    private val sale: SaleModel,
    private val saleItems: List<SaleItemModel>,
    private val customer: CustomerModel?,
    private val office: OfficeModel,
    private val business: BusinessModel,
    private val setting: SettingModel,
    private val context: Context
) {
    fun sharePdf() {
        val file: File = buildPdf()
        val uri = FileProvider.getUriForFile(
            context,
            "com.va6corporation.kramviapp.fileprovider",  //(use your app signature + ".provider" )
            file
        )
        val share = Intent()
        share.action = Intent.ACTION_SEND
        share.type = "application/pdf"
        share.putExtra(Intent.EXTRA_STREAM, uri)
        val shareIntent = Intent.createChooser(share, null)
        context.startActivity(shareIntent)
    }

    @SuppressLint("DefaultLocale")
    private fun buildPdf(): File {
        val invoiceSerial = "${sale.invoicePrefix}${office.serialPrefix}-${sale.invoiceNumber}"
        val file = File(context.externalCacheDir, "$invoiceSerial.pdf")
        val writer = PdfWriter(file)

        val pdf = PdfDocument(writer)
        val pageSize = Rectangle(225f, 1264f)
        val document = Document(pdf, PageSize(pageSize))
        document.setMargins(0f, 0f, 0f, 0f)

        if (setting.logo.isNotEmpty()) {
            val decodedString: ByteArray = Base64.decode(setting.logo.substring(22), Base64.DEFAULT)
            val data = ImageDataFactory.create(decodedString)
            val img = Image(data)
            document.add(img)
        }

        val invoiceTitle = when (sale.invoiceType) {
            InvoiceType.BOLETA -> "BOLETA ELECTRONICA"
            InvoiceType.FACTURA -> "FACTURA ELECTRONICA"
            InvoiceType.NOTA_DE_VENTA -> "NOTA DE VENTA"
        }

        val paragraphHeader = Paragraph().setFontSize(11f).setTextAlignment(TextAlignment.CENTER)

        val textTradeName = Text(office.tradeName.uppercase(Locale.getDefault()))
        textTradeName.setBold()
        paragraphHeader.add(textTradeName)
        paragraphHeader.add("\n")

        val textBusinessName = Text(business.businessName.uppercase(Locale.getDefault()))
        paragraphHeader.add(textBusinessName)
        paragraphHeader.add("\n")

        val textRuc = Text("RUC: " + business.ruc)
        paragraphHeader.add(textRuc)
        paragraphHeader.add("\n")

        val textAddress = Text(office.address)
        paragraphHeader.add(textAddress)
        paragraphHeader.add("\n")

        if (office.mobileNumber.isNotEmpty()) {
            val textMobileNumber = Text(office.mobileNumber)
            paragraphHeader.add(textMobileNumber)
            paragraphHeader.add("\n")
        }

        val textInvoiceTitle = Text(invoiceTitle)
        textInvoiceTitle.setBold()
        paragraphHeader.add(textInvoiceTitle)
        paragraphHeader.add("\n")

        val textInvoiceSerial = Text(invoiceSerial)
        textInvoiceSerial.setBold()
        paragraphHeader.add(textInvoiceSerial)
        paragraphHeader.add("\n")

        val customerBody = Paragraph().setFontSize(8.5f)
        customerBody.add("Fecha: ${formatDate(sale.createdAt)} ${formatTime(sale.createdAt)}")
        customerBody.add("\n")

        if (customer == null) {
            customerBody.add("Cliente: VARIOS")
            customerBody.add("\n")
        } else {
            customerBody.add("Cliente: ${customer.name}")
            customerBody.add("\n")
            customerBody.add("Direccion: ${customer.address}")
            customerBody.add("\n")
            customerBody.add("${customer.documentType}: ${customer.document}")
            customerBody.add("\n")
        }

        if (sale.isCredit) {
            customerBody.add("F. de pago: CREDITO")
        } else {
            customerBody.add("F. de pago: CONTADO")
        }

        customerBody.add("_______________________________________________")
        customerBody.setMarginBottom(10f)

        val columnWidth = floatArrayOf(75f, 75f, 75f)
        val table = Table(columnWidth).setFontSize(8.5f)

        for (saleItem in saleItems) {
            val cellProduct = Cell(1, 3)
            cellProduct.setBorder(Border.NO_BORDER)
            cellProduct.add(Paragraph(saleItem.fullName.uppercase()))
            table.addCell(cellProduct)
            val cellQuantity = Cell(1, 1)
            cellQuantity.setBorder(Border.NO_BORDER)
            cellQuantity.add(Paragraph(saleItem.quantity.toString()))
            table.addCell(cellQuantity)
            val cellPrice = Cell(1, 1)
            cellPrice.setBorder(Border.NO_BORDER)
            cellPrice.add(
                Paragraph(saleItem.price.toString())
                    .setTextAlignment(TextAlignment.CENTER)
            )
            table.addCell(cellPrice)
            val cellSubTotal = Cell(1, 1)
            cellSubTotal.setBorder(Border.NO_BORDER)
            cellSubTotal.add(
                Paragraph(String.format("%.2f", saleItem.price * saleItem.quantity))
                    .setTextAlignment(TextAlignment.RIGHT)
            )
            table.addCell(cellSubTotal)
        }

        val chargeText = Paragraph().setFontSize(8.5f)
        chargeText.add("_______________________________________________")
        chargeText.add("\n")
        chargeText.add("\n")
        chargeText.add("SON: " + sale.chargeLetters)

        val chargeSummary = Paragraph().setFontSize(8.5f)
            .setTextAlignment(TextAlignment.RIGHT)
            .setMarginRight(50f)

        val currency = if (sale.currencyCode == CurrencyCodeType.SOLES) "S/" else "$"

        if (sale.invoiceType != InvoiceType.NOTA_DE_VENTA) {
            if (sale.gravado > 0) {
                chargeSummary.add(
                    "OP. GRAVADAS $currency\t\t" + String.format("%.2f", sale.gravado)
                )
                chargeSummary.add("\n")
            }
            if (sale.gratuito > 0) {
                chargeSummary.add(
                    "OP. GRATUITAS $currency\t\t" + String.format("%.2f", sale.gratuito)
                )
                chargeSummary.add("\n")
            }
            if (sale.exonerado > 0) {
                chargeSummary.add(
                    "OP. EXONERADAS $currency\t\t" + String.format("%.2f", sale.exonerado)
                )
                chargeSummary.add("\n")
            }
            if (sale.inafecto > 0) {
                chargeSummary.add(
                    "OP. INAFECTAS $currency\t\t" + String.format("%.2f", sale.inafecto)
                )
                chargeSummary.add("\n")
            }
            chargeSummary.add("IGV " + currency + "\t\t" + String.format("%.2f", sale.igv))
            chargeSummary.add("\n")
        }
        chargeSummary.add("TOTAL DCTO " + currency + "\t\t" + String.format("%.2f", sale.discount))
        chargeSummary.add("\n")
        chargeSummary.add("IMPORTE TOTAL " + currency + "\t\t" + String.format("%.2f", sale.charge))
        chargeSummary.add("\n")

        if (sale.isCredit) {
            var totalPayments = 0.0
            for (payment in sale.payments) {
                totalPayments += payment.charge
            }
            val diff = sale.charge - totalPayments
            chargeSummary.add("SALDO " + currency + "\t\t" + String.format("%.2f", diff))
        }

        document.add(paragraphHeader)
        document.add(customerBody)
        document.add(table)
        document.add(chargeText)
        document.add(chargeSummary)

        if (sale.invoiceType != InvoiceType.NOTA_DE_VENTA) {
            val qr = StringBuilder()
            qr.append(business.ruc)
                .append("|")
                .append(sale.invoiceType)
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
            val qrCode = BarcodeQRCode(qr.toString())
            val barcodeObject = qrCode.createFormXObject(ColorConstants.BLACK, pdf)
            val barcodeImage = Image(barcodeObject).setWidth(100f).setHeight(100f)
            barcodeImage.setHorizontalAlignment(HorizontalAlignment.CENTER)
            document.add(barcodeImage)
        }

        val body = StringBuilder()
        val sunatInfo = Paragraph().setFontSize(8.5f)
            .setTextAlignment(TextAlignment.CENTER)
        when (sale.invoiceType) {
            InvoiceType.FACTURA -> {
                body.append("AUTORIZADO MEDIANTE RESOLUCION NRO.").append("\n")
                body.append("0180050001442/SUNAT Representacion impresa").append("\n")
                body.append("de la FACTURA DE VENTA\n")
            }

            InvoiceType.BOLETA -> {
                body.append("AUTORIZADO MEDIANTE RESOLUCION NRO.").append("\n")
                body.append("0180050001442/SUNAT Representacion impresa").append("\n")
                body.append("de la BOLETA DE VENTA").append("\n")
            }

            InvoiceType.NOTA_DE_VENTA -> {}
        }

        if (sale.observations.isNotEmpty()) {
            body.append(sale.observations)
            body.append("\n")
        }

        sunatInfo.add(body.toString())
        document.add(sunatInfo)

        document.close()

        return file
    }
}