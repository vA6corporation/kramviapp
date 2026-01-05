package com.example.kramviapp.biller

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.kramviapp.models.BillerItemModel
import com.example.kramviapp.models.CreatePaymentModel
import com.example.kramviapp.models.CreateSaleModel
import com.example.kramviapp.models.SaleModel
import com.example.kramviapp.requests.BillerRequest
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

class BillerViewModel: ViewModel() {

    private val _billerItems: MutableStateFlow<MutableList<BillerItemModel>> = MutableStateFlow(
        mutableStateListOf()
    )
    val billerItems = _billerItems.asStateFlow()

    fun addBillerItem(billerItem: BillerItemModel) {
        val billerItems = _billerItems.value.toMutableList()
        billerItems.add(billerItem)
        _billerItems.value = billerItems
    }

    fun updateBillerItem(index: Int, billerItem: BillerItemModel) {
        _billerItems.value[index] = _billerItems.value[index].copy(
            fullName = billerItem.fullName,
            quantity = billerItem.quantity,
            price = billerItem.price,
            igvCode = billerItem.igvCode
        )
    }

    fun removeBillerItem(index: Int) {
        val billerItems = _billerItems.value.toMutableList()
        billerItems.removeAt(index)
        _billerItems.value = billerItems
    }

    fun removeAllBillerItems() {
        _billerItems.value = mutableStateListOf()
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var billerService = retrofit.create(BillerService::class.java)

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

        billerService = retrofit.create(BillerService::class.java)
    }

    fun createSale(
        sale: CreateSaleModel,
        saleItems: List<BillerItemModel>,
        payments: List<CreatePaymentModel>,
        onResponse: (SaleModel) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val billerRequest = BillerRequest(sale, saleItems, payments, listOf())
        billerService.createSale(billerRequest).enqueue(object: Callback<SaleModel> {
            override fun onResponse(call: Call<SaleModel>, response: Response<SaleModel>) {
                if (response.isSuccessful) {
                    response.body()?.let(onResponse)
                } else {
                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.string())
                        onFailure(jsonObject.getString("message"))
                    }
                }
            }
            override fun onFailure(call: Call<SaleModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

}