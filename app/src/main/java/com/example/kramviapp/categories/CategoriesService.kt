package com.example.kramviapp.categories

import com.example.kramviapp.models.CategoryModel
import com.example.kramviapp.models.CreateCategoryModel
import com.example.kramviapp.models.CreateProductModel
import com.example.kramviapp.models.FavoriteModel
import com.example.kramviapp.models.PriceListModel
import com.example.kramviapp.models.ProductModel
import com.example.kramviapp.requests.CategoryResquest
import com.example.kramviapp.requests.ProductRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CategoriesService {

    @GET("categories")
    fun getCategories(): Call<List<CategoryModel>>

    @POST("categories")
    fun create(@Body category: CategoryResquest): Call<CategoryModel>

}