package com.example.kramviapp.categories

import androidx.lifecycle.ViewModel
import com.example.kramviapp.models.CategoryModel
import com.example.kramviapp.models.CreateCategoryModel
import com.example.kramviapp.requests.CategoryResquest
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

class CategoriesViewModel: ViewModel() {

    private val _categories: MutableStateFlow<List<CategoryModel>?> = MutableStateFlow(null)
    val categories = _categories.asStateFlow()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var categoriesService = retrofit.create(CategoriesService::class.java)

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

        categoriesService = retrofit.create(CategoriesService::class.java)
    }

    fun getCategories() {
        categoriesService.getCategories().enqueue(object: Callback<List<CategoryModel>> {
            override fun onResponse(call: Call<List<CategoryModel>>, response: Response<List<CategoryModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _categories.value = it
                    }
                }
            }
            override fun onFailure(call: Call<List<CategoryModel>>, t: Throwable) {

            }
        })
    }

    fun create(
        name: String,
        onResponse: (CategoryModel) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val category = CreateCategoryModel(name)
        val categoryRequest = CategoryResquest(category)
        categoriesService.create(categoryRequest).enqueue(object: Callback<CategoryModel> {
            override fun onResponse(call: Call<CategoryModel>, response: Response<CategoryModel>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onResponse(it)
                    }
                } else {
                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.string())
                        onFailure(jsonObject.getString("message"))
                    }
                }
            }
            override fun onFailure(call: Call<CategoryModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }
}