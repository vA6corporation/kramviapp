package com.example.kramviapp.requests

import com.example.kramviapp.models.CreateProformaModel
import com.example.kramviapp.models.ProformaItemModel
import com.google.gson.annotations.SerializedName

data class ProformaRequest(
    @SerializedName("proforma") val proforma: CreateProformaModel,
    @SerializedName("proformaItems") val proformaItems: List<ProformaItemModel>,
)