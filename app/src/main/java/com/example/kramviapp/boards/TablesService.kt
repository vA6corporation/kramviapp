package com.example.kramviapp.boards

import com.example.kramviapp.models.BoardModel
import com.example.kramviapp.models.FavoriteModel
import com.example.kramviapp.models.ProductModel
import com.example.kramviapp.models.TableModel
import com.example.kramviapp.requests.BoardRequest
import com.example.kramviapp.requests.BoardWithStockResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TablesService {

    @GET("tables")
    fun getTables(): Call<List<TableModel>>

}