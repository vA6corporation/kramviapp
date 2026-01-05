package com.example.kramviapp.requests

import com.example.kramviapp.models.CreateIncidentItemModel
import com.example.kramviapp.models.CreateIncidentModel
import com.example.kramviapp.models.CreateProductModel
import com.example.kramviapp.models.PriceModel
import com.google.gson.annotations.SerializedName

data class IncidentInRequest(
    @SerializedName("incident") val incident: CreateIncidentModel,
    @SerializedName("incidentInItems") val incidentInItems: List<CreateIncidentItemModel>,
)
