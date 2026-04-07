package com.example.kramviapp.biller

import com.example.kramviapp.models.SaleModel
import com.example.kramviapp.requests.BillerRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface BillerService {

    @POST("sales/bill")
    fun createSale(@Body billerRequest: BillerRequest): Call<SaleModel>

}