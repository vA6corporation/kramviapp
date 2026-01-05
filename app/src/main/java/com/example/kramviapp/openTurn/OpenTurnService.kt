package com.example.kramviapp.openTurn

import com.example.kramviapp.models.CreateExpenseModel
import com.example.kramviapp.models.CreateTurnModel
import com.example.kramviapp.models.ExpenseModel
import com.example.kramviapp.models.SummaryPaymentModel
import com.example.kramviapp.models.SummarySaleItemModel
import com.example.kramviapp.models.TurnModel
import com.example.kramviapp.requests.ExpenseResquest
import com.example.kramviapp.requests.TurnRequest
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface OpenTurnService {

    @GET("turns/openTurnOffice")
    fun getTurnOffice(): Call<TurnModel>

    @GET("turns/openTurnUser")
    fun getTurnUser(): Call<TurnModel>

    @PUT("turns/{turnId}")
    fun updateTurn(@Path("turnId") turnId: String, @Body turnRequest: TurnRequest): Call<Unit>

    @GET("payments/summaryByTurn/{turnId}")
    fun getSummaryPaymentsByTurn(@Path("turnId") turnId: String): Call<List<SummaryPaymentModel>>

    @GET("sales/summarySaleItemsByTurn/{turnId}")
    fun getSummarySaleItemsByTurn(@Path("turnId") turnId: String): Call<List<SummarySaleItemModel>>

    @GET("expenses/byTurn/{turnId}")
    fun getExpensesByTurn(@Path("turnId") turnId: String): Call<MutableList<ExpenseModel>>

    @GET("turns/closeTurn/{turnId}")
    fun getCloseTurn(@Path("turnId") turnId: String): Call<Unit>

    @POST("turns/openTurnOffice")
    fun createTurnOffice(@Body turn: CreateTurnModel): Call<TurnModel>

    @POST("turns/openTurnUser")
    fun createTurnUser(@Body turn: CreateTurnModel): Call<TurnModel>

    @POST("expenses")
    fun createExpense(@Body expenseRequest: ExpenseResquest): Call<ExpenseModel>

    @PUT("expenses/{expenseId}")
    fun updateExpense(@Path("expenseId") expenseId: String, @Body expenseRequest: ExpenseResquest): Call<Unit>

    @DELETE("expenses/{expenseId}")
    fun deleteExpense(@Path("expenseId") expenseId: String): Call<Unit>

}