package com.example.kramviapp.boards

import com.example.kramviapp.models.TableModel
import retrofit2.Call
import retrofit2.http.GET

interface TablesService {

    @GET("tables")
    fun getTables(): Call<List<TableModel>>

}