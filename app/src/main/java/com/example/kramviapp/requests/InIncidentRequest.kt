package com.example.kramviapp.requests

import com.example.kramviapp.models.CreateIncidentItemModel
import com.example.kramviapp.models.CreateIncidentModel
import com.google.gson.annotations.SerializedName

data class InIncidentRequest(
    @SerializedName("inIncident") val inIncident: CreateIncidentModel,
    @SerializedName("inIncidentItems") val inIncidentItems: List<CreateIncidentItemModel>,
)
