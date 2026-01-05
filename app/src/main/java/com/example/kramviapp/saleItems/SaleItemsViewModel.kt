package com.example.kramviapp.saleItems

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.kramviapp.models.ProductModel
import com.example.kramviapp.models.SaleItemModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SaleItemsViewModel: ViewModel() {

    private val _saleItems: MutableStateFlow<MutableList<SaleItemModel>> = MutableStateFlow(mutableListOf())
    val saleItems = _saleItems.asStateFlow()

    fun setSaleItems(saleItems: MutableList<SaleItemModel>) {
        _saleItems.value = saleItems.toMutableList()
    }

    fun addSaleItem(product: ProductModel) {
        val saleItems = _saleItems.value.toMutableList()
        var foundIndex = -1
        val saleItem = SaleItemModel(
            product.fullName,
            product.price,
            product.onModel,
            1.0,
            product.igvCode,
            product.igvCode,
            product.unitCode,
            product._id,
            product.prices,
            product.isTrackStock,
        )
        for (element in saleItems) {
            if (saleItem.productId == element.productId &&
                saleItem.igvCode == element.igvCode &&
                saleItem.observations == element.observations) {
                foundIndex = saleItems.indexOf(element)
            }
        }
        if (foundIndex >= 0) {
            val quantity = saleItems[foundIndex].quantity
            saleItems[foundIndex] = saleItems[foundIndex].copy(quantity = quantity + 1)
        } else {
            saleItems.add(saleItem)
        }
        _saleItems.value = saleItems
    }

    fun updateSaleItem(index: Int, saleItem: SaleItemModel) {
        _saleItems.value[index] = _saleItems.value[index].copy(
            quantity = saleItem.quantity,
            price = saleItem.price,
            igvCode = saleItem.igvCode
        )
    }

    fun removeSaleItem(index: Int) {
        val saleItems = _saleItems.value.toMutableList()
        saleItems.removeAt(index)
        _saleItems.value = saleItems
    }

    fun removeAllSaleItems() {
        _saleItems.value = mutableStateListOf()
    }

}