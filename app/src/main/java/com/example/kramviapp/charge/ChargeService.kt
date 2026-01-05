package com.example.kramviapp.charge

import com.example.kramviapp.models.PaymentMethodModel
import com.example.kramviapp.models.SaleModel
import com.example.kramviapp.models.WorkerModel
import com.example.kramviapp.requests.CreditRequest
import com.example.kramviapp.requests.SaleRequest
import com.example.kramviapp.requests.SaleWithStockResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChargeService {

    @GET("paymentMethods")
    fun getPaymentMethods(): Call<List<PaymentMethodModel>>

    @GET("workers")
    fun getWorkers(): Call<List<WorkerModel>>

    @GET("sales/byId/{saleId}")
    fun getSaleById(@Path("saleId") saleId: String): Call<SaleModel>

    @POST("sales")
    fun createSale(@Body saleRequest: SaleRequest, @Query("boardId") boardId: String?): Call<SaleModel>

    @POST("sales/withStock")
    fun createSaleWithStock(@Body saleRequest: SaleRequest, @Query("boardId") boardId: String?): Call<SaleWithStockResponse>

    @POST("credits")
    fun createCredit(@Body creditRequest: CreditRequest, @Query("boardId") boardId: String?): Call<SaleModel>

    @POST("credits/withStock")
    fun createCreditWithStock(@Body creditRequest: CreditRequest, @Query("boardId") boardId: String?): Call<SaleWithStockResponse>

}