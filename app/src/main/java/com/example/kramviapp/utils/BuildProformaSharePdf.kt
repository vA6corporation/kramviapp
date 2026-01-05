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
import com.example.kramviapp.models.ProformaItemModel
import com.example.kramviapp.models.ProformaModel
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

class BuildProformaSharePdf(
    private val proforma: ProformaModel,
    private val proformaItems: List<ProformaItemModel>,
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
        val proformaSerial = "P${office.serialPrefix}-${proforma.proformaNumber}"
        val file = File(context.externalCacheDir, "$proformaSerial.pdf")
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

        val textInvoiceTitle = Text("PROFORMA")
        textInvoiceTitle.setBold()
        paragraphHeader.add(textInvoiceTitle)
        paragraphHeader.add("\n")

        val textInvoiceSerial = Text(proformaSerial)
        textInvoiceSerial.setBold()
        paragraphHeader.add(textInvoiceSerial)
        paragraphHeader.add("\n")

        val customerBody = Paragraph().setFontSize(8.5f)
        customerBody.add("Fecha: ${formatDate(proforma.createdAt)} ${formatTime(proforma.createdAt)}")
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

        customerBody.add("_______________________________________________")
        customerBody.setMarginBottom(10f)

        val columnWidth = floatArrayOf(75f, 75f, 75f)
        val table = Table(columnWidth).setFontSize(8.5f)

        for (proformaItem in proformaItems) {
            val cellProduct = Cell(1, 3)
            cellProduct.setBorder(Border.NO_BORDER)
            cellProduct.add(Paragraph(proformaItem.fullName.uppercase()))
            table.addCell(cellProduct)
            val cellQuantity = Cell(1, 1)
            cellQuantity.setBorder(Border.NO_BORDER)
            cellQuantity.add(Paragraph(proformaItem.quantity.toString()))
            table.addCell(cellQuantity)
            val cellPrice = Cell(1, 1)
            cellPrice.setBorder(Border.NO_BORDER)
            cellPrice.add(
                Paragraph(proformaItem.price.toString())
                    .setTextAlignment(TextAlignment.CENTER)
            )
            table.addCell(cellPrice)
            val cellSubTotal = Cell(1, 1)
            cellSubTotal.setBorder(Border.NO_BORDER)
            cellSubTotal.add(
                Paragraph(String.format("%.2f", proformaItem.price * proformaItem.quantity))
                    .setTextAlignment(TextAlignment.RIGHT)
            )
            table.addCell(cellSubTotal)
        }

        val chargeText = Paragraph().setFontSize(8.5f)
        chargeText.add("_______________________________________________")
        chargeText.add("\n")
        chargeText.add("\n")
        chargeText.add("SON: " + proforma.chargeLetters)

        val chargeSummary = Paragraph().setFontSize(8.5f)
            .setTextAlignment(TextAlignment.RIGHT)
            .setMarginRight(50f)

        val currency = if (proforma.currencyCode == CurrencyCodeType.SOLES) "S/" else "$"

        if (proforma.gravado > 0) {
            chargeSummary.add(
                "OP. GRAVADAS $currency\t\t" + String.format("%.2f", proforma.gravado)
            )
            chargeSummary.add("\n")
        }
        if (proforma.gratuito > 0) {
            chargeSummary.add(
                "OP. GRATUITAS $currency\t\t" + String.format("%.2f", proforma.gratuito)
            )
            chargeSummary.add("\n")
        }
        if (proforma.exonerado > 0) {
            chargeSummary.add(
                "OP. EXONERADAS $currency\t\t" + String.format("%.2f", proforma.exonerado)
            )
            chargeSummary.add("\n")
        }
        if (proforma.inafecto > 0) {
            chargeSummary.add(
                "OP. INAFECTAS $currency\t\t" + String.format("%.2f", proforma.inafecto)
            )
            chargeSummary.add("\n")
        }
        chargeSummary.add("IGV " + currency + "\t\t" + String.format("%.2f", proforma.igv))
        chargeSummary.add("\n")
        chargeSummary.add("TOTAL DCTO " + currency + "\t\t" + String.format("%.2f", proforma.discount))
        chargeSummary.add("\n")
        chargeSummary.add("IMPORTE TOTAL " + currency + "\t\t" + String.format("%.2f", proforma.charge))
        chargeSummary.add("\n")

        val observationSummary = Paragraph().setFontSize(8.5f)
            .setTextAlignment(TextAlignment.CENTER)

        if (proforma.observations.isNotEmpty()) {
            observationSummary.add(proforma.observations)
            observationSummary.add("\n")
        }

        document.add(paragraphHeader)
        document.add(customerBody)
        document.add(table)
        document.add(chargeText)
        document.add(chargeSummary)
        document.add(observationSummary)
        document.close()

        return file
    }
}