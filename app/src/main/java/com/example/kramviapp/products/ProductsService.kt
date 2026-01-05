package com.example.kramviapp.products

import com.example.kramviapp.models.CategoryModel
import com.example.kramviapp.models.CreateProductModel
import com.example.kramviapp.models.FavoriteModel
import com.example.kramviapp.models.PriceListModel
import com.example.kramviapp.models.ProductModel
import com.example.kramviapp.requests.ProductRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductsService {

    @GET("priceLists")
    fun getPriceLists(): Call<List<PriceListModel>>

    @GET("products/byKey/{key}")
    fun getProductsByKey(@Path("key") key: String): Call<List<ProductModel>>

    @GET("favorites/withProducts")
    fun getFavoritesWitProducts(): Call<List<FavoriteModel>>

    @GET("products/byUpcGlobal/{upc}")
    fun getProductByUpcGlobal(@Path("upc") upc: String): Call<ProductModel>

    @GET("products/byPage/{pageIndex}/{pageSize}")
    fun getProductsByPage(
        @Path("pageIndex") pageIndex: Int,
        @Path("pageSize") pageSize: Int
    ): Call<List<ProductModel>>

    @GET("categories")
    fun getCategories(): Call<List<CategoryModel>>

    @GET("products/stock/{productId}")
    fun getStock(@Path("productId") productId: String): Call<Double>

    @POST("favorites/{productId}")
    fun createFavorite(@Path("productId") productId: String): Call<Unit>

    @POST("products")
    fun createProduct(@Body product: ProductRequest): Call<Unit>

    @PUT("products/trackstock/{productId}")
    fun trackStock(@Path("productId") productId: String): Call<Unit>

    @DELETE("favorites/{productId}")
    fun deleteFavorite(@Path("productId") productId: String): Call<Unit>

    @GET("products/byCategoryPage/{categoryId}/1/500")
    fun getProductsByCategory(@Path("categoryId") categoryId: String): Call<List<ProductModel>>

}