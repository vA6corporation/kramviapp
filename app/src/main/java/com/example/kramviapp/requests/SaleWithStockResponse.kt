package com.example.kramviapp.requests

import com.example.kramviapp.models.OutStockModel
import com.example.kramviapp.models.SaleItemModel
import com.example.kramviapp.models.SaleModel

data class SaleWithStockResponse(
    val sale: SaleModel?,
    val outStocks: List<OutStockModel>,
)