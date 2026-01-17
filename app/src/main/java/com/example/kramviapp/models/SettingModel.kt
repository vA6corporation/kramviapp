package com.example.kramviapp.models

import com.example.kramviapp.enums.CurrencyCodeType
import com.example.kramviapp.enums.IgvCodeType
import com.example.kramviapp.enums.InvoiceType
import com.example.kramviapp.enums.PriceType
import com.example.kramviapp.enums.SearchCustomerType

data class SettingModel(
    val logo: String = "",
    val password: String = "",
    val defaultPriceListId: String? = null,
    val textSale: String = "",

    val defaultInvoice: InvoiceType = InvoiceType.BOLETA,
    val defaultPrice: PriceType = PriceType.GLOBAL,
    val defaultIgvCode: IgvCodeType = IgvCodeType.GRAVADO,
    val defaultCurrencyCode: CurrencyCodeType = CurrencyCodeType.SOLES,
    val defaultSearchCustomer: SearchCustomerType = SearchCustomerType.RUC,

    val showCurrencyCode: Boolean = false,
    val showChange: Boolean = false,
    val showCost: Boolean = false,
    val showTotalDiscount: Boolean = false,
    val showTotalDiscountPercent: Boolean = false,
    val showQrCode: Boolean = false,
    val showAddressOnTicket: Boolean = false,
    val allowCredit: Boolean = false,
    val showChargeCommand: Boolean = false,
    val showSubPrice: Boolean = false,
    val showWorker: Boolean = false,
    val allowFreePrice: Boolean = false,
    val allowFreeStock: Boolean = false,
    val isOfficeTurn: Boolean = false,
    val isBlockChangeStock: Boolean = false,

    val defaultIgvPercent: Int = 18,
)
