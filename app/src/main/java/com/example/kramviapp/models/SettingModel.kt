package com.example.kramviapp.models

import com.example.kramviapp.enums.CurrencyCodeType
import com.example.kramviapp.enums.IgvCodeType
import com.example.kramviapp.enums.InvoiceCode
import com.example.kramviapp.enums.PriceType
import com.example.kramviapp.enums.SearchCustomerType

data class SettingModel(
    val logo: String = "",
    val password: String = "",
    val defaultPriceListId: Int? = null,
    val textBottom: String = "",

    val defaultInvoice: InvoiceCode = InvoiceCode.BOLETA,
    val defaultPrice: PriceType = PriceType.GLOBAL,
    val defaultIgvCode: IgvCodeType = IgvCodeType.GRAVADO,
    val defaultCurrencyCode: CurrencyCodeType = CurrencyCodeType.SOLES,
    val defaultSearchCustomer: SearchCustomerType = SearchCustomerType.RUC,

    val isShowCurrency: Boolean = false,
    val isShowChange: Boolean = false,
    val isShowCost: Boolean = false,
    val isShowTotalDiscount: Boolean = false,
    val isShowTotalDiscountPercent: Boolean = false,
    val isShowCredit: Boolean = false,
    val isShowEditPrice: Boolean = false,
    val isAvailableStock: Boolean = false,

    val defaultIgvPercent: Double = 18.0,
    val defaultRcPercent: Double = 0.0
)
