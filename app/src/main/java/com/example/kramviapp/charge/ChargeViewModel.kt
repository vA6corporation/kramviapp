package com.example.kramviapp.charge

import androidx.lifecycle.ViewModel
import com.example.kramviapp.models.CreateDueModel
import com.example.kramviapp.models.CreatePaymentModel
import com.example.kramviapp.models.CreateSaleModel
import com.example.kramviapp.models.PaymentMethodModel
import com.example.kramviapp.models.SaleItemModel
import com.example.kramviapp.models.SaleModel
import com.example.kramviapp.models.WorkerModel
import com.example.kramviapp.requests.CreditRequest
import com.example.kramviapp.requests.SaleRequest
import com.example.kramviapp.requests.SaleWithStockResponse
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

class ChargeViewModel: ViewModel() {

    private val _dues: MutableStateFlow<List<CreateDueModel>?> = MutableStateFlow(null)
    val dues = _dues.asStateFlow()

    private val _paymentMethods: MutableStateFlow<List<PaymentMethodModel>?> = MutableStateFlow(null)
    val paymentMethods = _paymentMethods.asStateFlow()

    private val _workers: MutableStateFlow<List<WorkerModel>?> = MutableStateFlow(null)
    val workers = _workers.asStateFlow()

    private val _payments: MutableStateFlow<List<CreatePaymentModel>> = MutableStateFlow(listOf())
    val payments = _payments.asStateFlow()
    fun setPayments(payments: List<CreatePaymentModel>) { _payments.value = payments }

  //fun setDues(dues: List<CreateDueModel>) { _dues.value = dues }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var posStandardService = retrofit.create(ChargeService::class.java)

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

        posStandardService = retrofit.create(ChargeService::class.java)
    }

    fun loadSaleById(
        saleId: String,
        onResponse: (SaleModel) -> Unit,
        onFailure: (String) -> Unit
    ) {
        posStandardService.getSaleById(saleId).enqueue(object: Callback<SaleModel> {
            override fun onResponse(call: Call<SaleModel>, response: Response<SaleModel>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onResponse(it)
                    }
                } else {
                    onFailure("Sin resultados")
                }
            }
            override fun onFailure(call: Call<SaleModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun loadPaymentMethods(onResponse: () -> Unit) {
        posStandardService.getPaymentMethods().enqueue(object: Callback<List<PaymentMethodModel>> {
            override fun onResponse(call: Call<List<PaymentMethodModel>>, response: Response<List<PaymentMethodModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _paymentMethods.value = it
                        onResponse()
                    }
                }
            }
            override fun onFailure(call: Call<List<PaymentMethodModel>>, t: Throwable) {

            }
        })
    }

    fun loadWorkers() {
        posStandardService.getWorkers().enqueue(object: Callback<List<WorkerModel>> {
            override fun onResponse(call: Call<List<WorkerModel>>, response: Response<List<WorkerModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _workers.value = it
                    }
                }
            }
            override fun onFailure(call: Call<List<WorkerModel>>, t: Throwable) {

            }
        })
    }

    fun createCredit(
        sale: CreateSaleModel,
        saleItems: List<SaleItemModel>,
        payments: List<CreatePaymentModel>,
        dues: List<CreateDueModel>,
        boardId: String?,
        onResponse: (SaleModel) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val creditRequest = CreditRequest(sale, saleItems, payments, dues)
        posStandardService.createCredit(creditRequest, boardId).enqueue(object: Callback<SaleModel> {
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

    fun createCreditWithStock(
        sale: CreateSaleModel,
        saleItems: List<SaleItemModel>,
        payments: List<CreatePaymentModel>,
        dues: List<CreateDueModel>,
        boardId: String?,
        onResponse: (SaleWithStockResponse) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val saleRequest = CreditRequest(sale, saleItems, payments, dues)
        posStandardService.createCreditWithStock(saleRequest, boardId).enqueue(object: Callback<SaleWithStockResponse> {
            override fun onResponse(call: Call<SaleWithStockResponse>, response: Response<SaleWithStockResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let(onResponse)
                } else {
                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.string())
                        onFailure(jsonObject.getString("message"))
                    }
                }
            }
            override fun onFailure(call: Call<SaleWithStockResponse>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun createSale(
        sale: CreateSaleModel,
        saleItems: List<SaleItemModel>,
        payments: List<CreatePaymentModel>,
        boardId: String?,
        onResponse: (SaleModel) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val saleRequest = SaleRequest(sale, saleItems, payments)
        posStandardService.createSale(saleRequest, boardId).enqueue(object: Callback<SaleModel> {
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

    fun createSaleWithStock(
        sale: CreateSaleModel,
        saleItems: List<SaleItemModel>,
        payments: List<CreatePaymentModel>,
        boardId: String?,
        onResponse: (SaleWithStockResponse) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val saleRequest = SaleRequest(sale, saleItems, payments)
        posStandardService.createSaleWithStock(saleRequest, boardId).enqueue(object: Callback<SaleWithStockResponse> {
            override fun onResponse(call: Call<SaleWithStockResponse>, response: Response<SaleWithStockResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let(onResponse)
                } else {
                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.string())
                        onFailure(jsonObject.getString("message"))
                    }
                }
            }
            override fun onFailure(call: Call<SaleWithStockResponse>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

}