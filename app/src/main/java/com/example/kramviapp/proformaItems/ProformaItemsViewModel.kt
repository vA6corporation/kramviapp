package com.example.kramviapp.proformaItems

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.kramviapp.models.ProductModel
import com.example.kramviapp.models.ProformaItemModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProformaItemsViewModel: ViewModel() {

    private val _proformaItems: MutableStateFlow<MutableList<ProformaItemModel>> = MutableStateFlow(mutableStateListOf())
    val proformaItems = _proformaItems.asStateFlow()

    fun setProformaItems(proformaItems: MutableList<ProformaItemModel>) {
        _proformaItems.value = proformaItems.toMutableList()
    }

    fun addProformaItem(product: ProductModel) {
        val proformaItems = _proformaItems.value.toMutableList()
        var foundIndex = -1
        val proformaItem = ProformaItemModel(
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
        for (element in proformaItems) {
            if (proformaItem.productId == element.productId &&
                proformaItem.igvCode == element.igvCode &&
                proformaItem.observations == element.observations) {
                foundIndex = proformaItems.indexOf(element)
            }
        }
        if (foundIndex >= 0) {
            val quantity = proformaItems[foundIndex].quantity
            proformaItems[foundIndex] = proformaItems[foundIndex].copy(quantity = quantity + 1)
        } else {
            proformaItems.add(proformaItem)
        }
        _proformaItems.value = proformaItems
    }

    fun updateProformaItem(index: Int, proformaItem: ProformaItemModel) {
        _proformaItems.value[index] = _proformaItems.value[index].copy(
            quantity = proformaItem.quantity,
            price = proformaItem.price,
            igvCode = proformaItem.igvCode
        )
    }

    fun removeProformaItem(index: Int) {
        val proformaItems = _proformaItems.value.toMutableList()
        proformaItems.removeAt(index)
        _proformaItems.value = proformaItems
    }

    fun removeAllProformaItems() {
        _proformaItems.value = mutableStateListOf()
    }

}