package com.example.kramviapp.invoices

import com.example.kramviapp.models.CdrModel
import com.example.kramviapp.models.SaleModel
import com.example.kramviapp.models.TicketModel
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface InvoicesService {

    @GET("sales/salesOfTheDay")
    fun getSalesOfTheDay(): Call<List<SaleModel>>

    @GET("invoices/send/{saleId}")
    fun getSendInvoice(
        @Path("saleId") saleId: String,
    ): Call<CdrModel>

    @DELETE("sales/{saleId}/{deletedReason}")
    fun deleteSale(
        @Path("saleId") saleId: String,
        @Path("deletedReason") deletedReason: String
    ): Call<Unit>

    @DELETE("tickets/invoice/{saleId}/{deletedReason}")
    fun deleteInvoice(
        @Path("saleId") saleId: String,
        @Path("deletedReason") deletedReason: String
    ): Call<TicketModel>

}