package com.example.kramviapp.incidents

import com.example.kramviapp.requests.InIncidentRequest
import com.example.kramviapp.requests.OutIncidentRequest
import com.example.kramviapp.requests.PurchaseRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface IncidentsService {

    @POST("purchases")
    fun createPurchase(
        @Body purchaseRequest: PurchaseRequest
    ): Call<Unit>

    @POST("inIncidents")
    fun createIn(
        @Body incidentInRequest: InIncidentRequest
    ): Call<Unit>

    @POST("outIncidents")
    fun createOut(
        @Body incidentOutRequest: OutIncidentRequest
    ): Call<Unit>

}