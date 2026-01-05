package com.example.kramviapp.customers

import com.example.kramviapp.models.CustomerModel
import com.example.kramviapp.requests.CustomerResquest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CustomersService {

    @GET("customers/byRuc/{ruc}")
    fun getCustomersByRuc(@Path("ruc") ruc: String): Call<List<CustomerModel>>

    @GET("customers/byDni/{dni}")
    fun getCustomersByDni(@Path("dni") dni: String): Call<List<CustomerModel>>

    @GET("customers/byCe/{ce}")
    fun getCustomersByCe(@Path("ce") dni: String): Call<List<CustomerModel>>

    @GET("customers/byMobileNumber/{mobileNumber}")
    fun getCustomersByMobileNumber(@Path("mobile") dni: String): Call<List<CustomerModel>>

    @GET("customers/byKey/{key}")
    fun getCustomersByKey(@Path("key") dni: String): Call<List<CustomerModel>>

    @POST("customers")
    fun createCustomer(@Body customerRequest: CustomerResquest): Call<CustomerModel>

    @PUT("customers/{customerId}")
    fun updateCustomer(
        @Body customerRequest: CustomerResquest,
        @Path("customerId") customerId: String,
    ): Call<Unit>

}