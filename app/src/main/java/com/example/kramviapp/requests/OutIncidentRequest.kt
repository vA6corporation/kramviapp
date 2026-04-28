package com.example.kramviapp.requests

import com.example.kramviapp.models.CreateIncidentItemModel
import com.example.kramviapp.models.CreateIncidentModel
import com.google.gson.annotations.SerializedName

data class OutIncidentRequest(
    @SerializedName("outIncident") val outIncident: CreateIncidentModel,
    @SerializedName("outIncidentItems") val outIncidentItems: List<CreateIncidentItemModel>,
)
