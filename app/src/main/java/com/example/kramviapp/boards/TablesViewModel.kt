package com.example.kramviapp.boards

import androidx.lifecycle.ViewModel
import com.example.kramviapp.models.TableModel
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

class TablesViewModel: ViewModel() {

    private val _tables: MutableStateFlow<List<TableModel>?> = MutableStateFlow(null)
    val tables = _tables.asStateFlow()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var tablesService = retrofit.create(TablesService::class.java)

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

        tablesService = retrofit.create(TablesService::class.java)
    }

    fun loadTables() {
        tablesService.getTables().enqueue(object: Callback<List<TableModel>> {
            override fun onResponse(call: Call<List<TableModel>>, response: Response<List<TableModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let { tables ->
                        _tables.value = tables
                    }
                }
            }
            override fun onFailure(call: Call<List<TableModel>>, t: Throwable) {

            }
        })
    }

}