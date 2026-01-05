package com.example.kramviapp.proformas

import androidx.lifecycle.ViewModel
import com.example.kramviapp.invoices.InvoicesService
import com.example.kramviapp.models.CdrModel
import com.example.kramviapp.models.CreateProformaModel
import com.example.kramviapp.models.ProformaItemModel
import com.example.kramviapp.models.ProformaModel
import com.example.kramviapp.models.SaleModel
import com.example.kramviapp.models.TicketModel
import com.example.kramviapp.requests.ProformaRequest
import com.va6corporation.kramviapp.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProformasViewModel: ViewModel() {

    private val _proformasOfTheDay: MutableStateFlow<List<ProformaModel>> = MutableStateFlow(listOf())
    val proformasOfTheDay = _proformasOfTheDay.asStateFlow()
    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var proformasService = retrofit.create(ProformasService::class.java)

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

        proformasService = retrofit.create(ProformasService::class.java)
    }

    fun setIsRefreshing(isRefreshing: Boolean) {
        _isRefreshing.value = isRefreshing
    }

    fun loadProformaById(
        proformaId: String,
        onResponse: (ProformaModel) -> Unit,
        onFailure: (String) -> Unit
    ) {
        proformasService.getProformaById(proformaId).enqueue(object: Callback<ProformaModel> {
            override fun onResponse(call: Call<ProformaModel>, response: Response<ProformaModel>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onResponse(it)
                    }
                } else {
                    onFailure("Sin resultados")
                }
            }
            override fun onFailure(call: Call<ProformaModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun loadProformasOfTheDay(
        onReponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        proformasService.getProformasOfTheDay().enqueue(object: Callback<List<ProformaModel>> {
            override fun onResponse(call: Call<List<ProformaModel>>, response: Response<List<ProformaModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _proformasOfTheDay.value = it
                        onReponse()
                    }
                }
            }
            override fun onFailure(call: Call<List<ProformaModel>>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun createProforma(
        proforma: CreateProformaModel,
        proformaItems: List<ProformaItemModel>,
        onResponse: (ProformaModel) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val proformaRequest = ProformaRequest(proforma, proformaItems)
        proformasService.createProforma(proformaRequest).enqueue(object: Callback<ProformaModel> {
            override fun onResponse(call: Call<ProformaModel>, response: Response<ProformaModel>) {
                if (response.isSuccessful) {
                    response.body()?.let(onResponse)
                } else {
                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.string())
                        onFailure(jsonObject.getString("message"))
                    }
                }
            }
            override fun onFailure(call: Call<ProformaModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

}