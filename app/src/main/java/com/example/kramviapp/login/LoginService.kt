package com.example.kramviapp.login

import com.example.kramviapp.models.BusinessModel
import com.example.kramviapp.models.LoginResultModel
import com.example.kramviapp.models.OfficeModel
import com.example.kramviapp.models.ProfileModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class LoginObject(val email: String, val password: String)

interface LoginService {

    @POST("auth/login")
    fun login(@Body login: LoginObject): Call<LoginResultModel>

    @GET("auth/profile")
    fun loadProfile(): Call<ProfileModel>

    @GET("offices/byActivity")
    fun loadOfficesByActivity(): Call<List<OfficeModel>>

    @GET("auth/setBusinessOffice/{businessId}/{officeId}/{activityId}")
    fun setBusinessOffice(
        @Path("businessId") businessId: String,
        @Path("officeId") officeId: String,
        @Path("activityId") activityId: String,
    ): Call<LoginResultModel>

    @GET("businesses")
    fun loadBusinesses(): Call<List<BusinessModel>>

}