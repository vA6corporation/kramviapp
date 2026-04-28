package com.example.kramviapp.incidents

import androidx.lifecycle.ViewModel
import com.example.kramviapp.models.CreateIncidentItemModel
import com.example.kramviapp.models.CreateIncidentModel
import com.example.kramviapp.models.CreatePurchaseItemModel
import com.example.kramviapp.models.CreatePurchaseModel
import com.example.kramviapp.requests.InIncidentRequest
import com.example.kramviapp.requests.OutIncidentRequest
import com.example.kramviapp.requests.PurchaseRequest
import com.va6corporation.kramviapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class IncidentsViewModel: ViewModel() {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var incidentsService = retrofit.create(IncidentsService::class.java)

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

        incidentsService = retrofit.create(IncidentsService::class.java)
    }

    fun createPurchase(
        purchase: CreatePurchaseModel,
        purchaseItems: List<CreatePurchaseItemModel>,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val purchaseRequest = PurchaseRequest(purchase, purchaseItems)
        incidentsService.createPurchase(purchaseRequest).enqueue(object: Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onResponse()
                } else {
                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.string())
                        onFailure(jsonObject.getString("message"))
                    }
                }
            }
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun createIn(
        inIncident: CreateIncidentModel,
        inIncidentItems: List<CreateIncidentItemModel>,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val inIncidentRequest = InIncidentRequest(inIncident, inIncidentItems)
        incidentsService.createIn(inIncidentRequest).enqueue(object: Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onResponse()
                } else {
                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.string())
                        onFailure(jsonObject.getString("message"))
                    }
                }
            }
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun createOut(
        outIncident: CreateIncidentModel,
        outIncidentItems: List<CreateIncidentItemModel>,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val outIncidentRequest = OutIncidentRequest(outIncident, outIncidentItems)
        incidentsService.createOut(outIncidentRequest).enqueue(object: Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onResponse()
                } else {
                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.string())
                        onFailure(jsonObject.getString("message"))
                    }
                }
            }
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

}