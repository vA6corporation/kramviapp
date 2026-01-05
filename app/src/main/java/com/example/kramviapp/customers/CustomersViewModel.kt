package com.example.kramviapp.customers

import androidx.lifecycle.ViewModel
import com.example.kramviapp.models.CreateCustomerModel
import com.example.kramviapp.models.CustomerModel
import com.example.kramviapp.requests.CustomerResquest
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


class CustomersViewModel: ViewModel() {

    private val _customers: MutableStateFlow<List<CustomerModel>> = MutableStateFlow(listOf())
    val customers = _customers.asStateFlow()

    private val _customer: MutableStateFlow<CustomerModel?> = MutableStateFlow(null)
    val customer = _customer.asStateFlow()
    fun setCustomer(customer: CustomerModel?) { _customer.value = customer }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var customersService = retrofit.create(CustomersService::class.java)

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

        customersService = retrofit.create(CustomersService::class.java)
    }

    fun getCustomersByRuc(
        ruc: String,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        customersService.getCustomersByRuc(ruc).enqueue(object: Callback<List<CustomerModel>> {
            override fun onResponse(call: Call<List<CustomerModel>>, response: Response<List<CustomerModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _customers.value = it
                        onResponse()
                    }
                } else {
                    onFailure("Sin resultados")
                }
            }
            override fun onFailure(call: Call<List<CustomerModel>>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun getCustomersByDni(
        dni: String,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        customersService.getCustomersByDni(dni).enqueue(object: Callback<List<CustomerModel>> {
            override fun onResponse(call: Call<List<CustomerModel>>, response: Response<List<CustomerModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _customers.value = it
                        onResponse()
                    }
                } else {
                    onFailure("Sin resultados")
                }
            }
            override fun onFailure(call: Call<List<CustomerModel>>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun getCustomersByCe(
        ce: String,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        customersService.getCustomersByCe(ce).enqueue(object: Callback<List<CustomerModel>> {
            override fun onResponse(call: Call<List<CustomerModel>>, response: Response<List<CustomerModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _customers.value = it
                        onResponse()
                    }
                } else {
                    onFailure("Sin resultados")
                }
            }
            override fun onFailure(call: Call<List<CustomerModel>>, t: Throwable) {

            }
        })
    }

    fun getCustomersByKey(
        key: String,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        customersService.getCustomersByKey(key).enqueue(object: Callback<List<CustomerModel>> {
            override fun onResponse(call: Call<List<CustomerModel>>, response: Response<List<CustomerModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _customers.value = it
                        onResponse()
                    }
                } else {
                    onFailure("Sin resultados")
                }
            }
            override fun onFailure(call: Call<List<CustomerModel>>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun getCustomersByMobileNumber(
        mobileNumber: String,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        customersService.getCustomersByMobileNumber(mobileNumber).enqueue(object: Callback<List<CustomerModel>> {
            override fun onResponse(call: Call<List<CustomerModel>>, response: Response<List<CustomerModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _customers.value = it
                        onResponse()
                    }
                } else {
                    onFailure("Sin resultados")
                }
            }
            override fun onFailure(call: Call<List<CustomerModel>>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun createCustomer(
        customer: CreateCustomerModel,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val customerRequest = CustomerResquest(customer)
        customersService.createCustomer(customerRequest).enqueue(object: Callback<CustomerModel> {
            override fun onResponse(call: Call<CustomerModel>, response: Response<CustomerModel>) {
                if (response.isSuccessful) {
                    _customer.value = response.body()
                    onResponse()
                } else {
                    onFailure("Existe otro cliente con este mismo RUC")
                }
            }
            override fun onFailure(call: Call<CustomerModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun updateCustomer(
        customer: CreateCustomerModel,
        customerId: String,
        onResponse: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val customerRequest = CustomerResquest(customer)
        customersService.updateCustomer(customerRequest, customerId).enqueue(object: Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onResponse("Se han guardado los cambios")
                } else {
                    onFailure("Existe otro cliente con este mismo N° de documento")
                }
            }
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

}