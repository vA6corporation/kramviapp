package com.example.kramviapp.incidents

import com.example.kramviapp.requests.IncidentInRequest
import com.example.kramviapp.requests.IncidentOutRequest
import com.example.kramviapp.requests.PurchaseRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface IncidentsService {

    @POST("purchases")
    fun createPurchase(
        @Body purchaseRequest: PurchaseRequest
    ): Call<Unit>

    @POST("incidents/in")
    fun createIn(
        @Body incidentInRequest: IncidentInRequest
    ): Call<Unit>

    @POST("incidents/out")
    fun createOut(
        @Body incidentOutRequest: IncidentOutRequest
    ): Call<Unit>

}