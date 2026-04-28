package com.example.kramviapp.charge

import com.example.kramviapp.models.PaymentMethodModel
import com.example.kramviapp.models.SaleModel
import com.example.kramviapp.requests.CreditRequest
import com.example.kramviapp.requests.SaleRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChargeService {

    @GET("paymentMethods")
    fun getPaymentMethods(): Call<List<PaymentMethodModel>>

    @GET("sales/byId/{saleId}")
    fun getSaleById(@Path("saleId") saleId: Int): Call<SaleModel>

    @POST("sales")
    fun createSale(
        @Body saleRequest: SaleRequest,
        @Query("boardId") boardId: Int?,
        @Query("isAvailableStock") isAvailableStock: Boolean
    ): Call<SaleModel>

    @POST("credits")
    fun createCredit(
        @Body creditRequest: CreditRequest,
        @Query("boardId") boardId: Int?,
        @Query("isAvailableStock") isAvailableStock: Boolean
    ): Call<SaleModel>

}