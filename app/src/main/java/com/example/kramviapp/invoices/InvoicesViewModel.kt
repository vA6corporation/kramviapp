package com.example.kramviapp.invoices

import androidx.lifecycle.ViewModel
import com.example.kramviapp.models.CdrModel
import com.example.kramviapp.models.SaleModel
import com.example.kramviapp.models.TicketModel
import com.va6corporation.kramviapp.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InvoicesViewModel: ViewModel() {

    private val _salesOfTheDay: MutableStateFlow<List<SaleModel>> = MutableStateFlow(listOf())
    val salesOfTheDay = _salesOfTheDay.asStateFlow()
    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var invoicesService = retrofit.create(InvoicesService::class.java)

    fun setAccessToken(
        accessToken: String,
        onUnauthorized: () -> Unit
    ) {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val request: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            chain.proceed(request)
        }.addInterceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            if (response.code() == 401) {
                onUnauthorized()
            }
            response
        }.build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        invoicesService = retrofit.create(InvoicesService::class.java)
    }

    fun setIsRefreshing(isRefreshing: Boolean) {
        _isRefreshing.value = isRefreshing
    }

    fun loadSalesOfTheDay(
        onReponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        invoicesService.getSalesOfTheDay().enqueue(object: Callback<List<SaleModel>> {
            override fun onResponse(call: Call<List<SaleModel>>, response: Response<List<SaleModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _salesOfTheDay.value = it
                        onReponse()
                    }
                }
            }
            override fun onFailure(call: Call<List<SaleModel>>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun sendInvoice(
        saleId: String,
        onResponse: (CdrModel) -> Unit,
        onFailure: (String) -> Unit
    ) {
        invoicesService.getSendInvoice(saleId).enqueue(object: Callback<CdrModel> {
            override fun onResponse(call: Call<CdrModel>, response: Response<CdrModel>) {
                if (response.isSuccessful) {
                    response.body()?.let { onResponse(it) }
                }
            }
            override fun onFailure(call: Call<CdrModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun deleteSale(
        saleId: String,
        deletedReason: String,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        invoicesService.deleteSale(saleId, deletedReason).enqueue(object: Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onResponse()
                }
            }
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun deleteInvoice(
        saleId: String,
        deletedReason: String,
        onResponse: (TicketModel) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        invoicesService.deleteInvoice(saleId, deletedReason).enqueue(object: Callback<TicketModel> {
            override fun onResponse(call: Call<TicketModel>, response: Response<TicketModel>) {
                if (response.isSuccessful) {
                    response.body()?.let { onResponse(it) }
                }
            }
            override fun onFailure(call: Call<TicketModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

}