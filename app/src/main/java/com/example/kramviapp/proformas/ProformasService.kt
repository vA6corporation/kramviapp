package com.example.kramviapp.proformas

import com.example.kramviapp.models.ProformaModel
import com.example.kramviapp.requests.ProformaRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ProformasService {

    @GET("proformas/proformasOfTheDay")
    fun getProformasOfTheDay(): Call<List<ProformaModel>>

    @GET("proformas/byId/{proformaId}")
    fun getProformaById(
        @Path("proformaId") proformaId: String
    ): Call<ProformaModel>

    @POST("proformas")
    fun createProforma(@Body proformaRequest: ProformaRequest): Call<ProformaModel>

}