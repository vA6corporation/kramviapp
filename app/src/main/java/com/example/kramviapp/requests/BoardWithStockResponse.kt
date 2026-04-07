package com.example.kramviapp.requests

import com.example.kramviapp.models.BoardModel
import com.example.kramviapp.models.OutStockModel
import com.example.kramviapp.models.SaleItemModel
import com.example.kramviapp.models.SaleModel

data class BoardWithStockResponse(
    val board: BoardModel?,
    val outStocks: List<OutStockModel>,
)