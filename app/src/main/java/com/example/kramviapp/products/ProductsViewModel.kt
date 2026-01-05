package com.example.kramviapp.products

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.kramviapp.enums.PriceType
import com.example.kramviapp.models.CategoryModel
import com.example.kramviapp.models.CreateProductModel
import com.example.kramviapp.models.FavoriteModel
import com.example.kramviapp.models.OfficeModel
import com.example.kramviapp.models.PriceListModel
import com.example.kramviapp.models.PriceModel
import com.example.kramviapp.models.ProductModel
import com.example.kramviapp.models.SettingModel
import com.example.kramviapp.models.UnitCode
import com.example.kramviapp.requests.ProductRequest
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

class ProductsViewModel : ViewModel() {

    val unitCodes: List<UnitCode> = listOf(
        UnitCode("NIU", "UNIDADES (Productos)"),
        UnitCode("ZZ", "UNIDADES (Servicios)"),
        UnitCode("BG", "BOLSA"),
        UnitCode("BO", "BOTELLA"),
        UnitCode("BX", "CAJA"),
        UnitCode("PK", "PAQUETE"),
        UnitCode("KT", "KIT"),
        UnitCode("SET", "JUEGO"),
        UnitCode("MTR", "METROS"),
        UnitCode("MTQ", "METROS CUBICOS"),
        UnitCode("MTK", "METRO CUADRADO"),
        UnitCode("MMT", "MILIMETROS"),
        UnitCode("KGM", "KILOGRAMOS"),
        UnitCode("GRM", "GRAMOS"),
        UnitCode("MGM", "MILIGRAMOS"),
        UnitCode("LTR", "LITROS"),
        UnitCode("MLT", "MILIMETROS"),
        UnitCode("GLL", "GALONES"),
        UnitCode("DZN", "DOCENA"),
        UnitCode("CEN", "CIENTO"),
        UnitCode("MIL", "MILLARES"),
        UnitCode("TNE", "TONELADAS")
    )

    companion object {
        fun setPrices(
            products: List<ProductModel>,
            priceListId: String?,
            office: OfficeModel,
            setting: SettingModel,
        ) {
            when (setting.defaultPrice) {
                PriceType.GLOBAL -> {

                }

                PriceType.OFICINA -> {
                    for (product in products) {
                        val price =
                            product.prices.find { it.officeId == office._id && it.priceListId == null }
                        price?.let { product.price = price.price }
                    }
                }

                PriceType.LISTA -> {
                    for (product in products) {
                        val price = product.prices.find { it.priceListId == priceListId }
                        price?.let { product.price = price.price }
                    }
                }

                PriceType.LISTAOFICINA -> {
                    for (product in products) {
                        val price =
                            product.prices.find { it.priceListId == priceListId && it.officeId == office._id }
                        price?.let { product.price = price.price }
                    }
                }
            }
        }
    }

    private val _priceLists: MutableStateFlow<List<PriceListModel>?> = MutableStateFlow(null)
    val priceLists = _priceLists.asStateFlow()

    private val _favorites: MutableStateFlow<List<FavoriteModel>?> = MutableStateFlow(null)
    val favorites = _favorites.asStateFlow()

    private val _products: MutableStateFlow<List<ProductModel>> = MutableStateFlow(listOf())
    val products = _products.asStateFlow()
    fun setProducts(products: List<ProductModel>) {
        _products.value = products
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var productsService = retrofit.create(ProductsService::class.java)

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

        productsService = retrofit.create(ProductsService::class.java)
    }

    fun getProductByUpcGlobal(
        upc: String,
        onResponse: (ProductModel) -> Unit,
        onFailure: (String) -> Unit
    ) {
        productsService.getProductByUpcGlobal(upc).enqueue(object : Callback<ProductModel> {
            override fun onResponse(call: Call<ProductModel>, response: Response<ProductModel>) {
                if (response.isSuccessful) {
                    response.body()?.let { product ->
                        onResponse(product)
                    }
                } else {
                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.string())
                        onFailure(jsonObject.getString("message"))
                    }
                }
            }

            override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun getFavorites() {
        productsService.getFavoritesWitProducts().enqueue(object : Callback<List<FavoriteModel>> {
            override fun onResponse(
                call: Call<List<FavoriteModel>>,
                response: Response<List<FavoriteModel>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { favorites ->
                        _favorites.value = favorites
                    }
                }
            }

            override fun onFailure(call: Call<List<FavoriteModel>>, t: Throwable) {

            }
        })
    }

    fun getProductsByKey(
        key: String,
        onResponse: (List<ProductModel>) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        productsService.getProductsByKey(key).enqueue(object : Callback<List<ProductModel>> {
            override fun onResponse(
                call: Call<List<ProductModel>>,
                response: Response<List<ProductModel>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let(onResponse)
                } else {
                    onFailure("Sin resultados")
                }
            }

            override fun onFailure(call: Call<List<ProductModel>>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun getPriceLists() {
        productsService.getPriceLists().enqueue(object : Callback<List<PriceListModel>> {
            override fun onResponse(
                call: Call<List<PriceListModel>>,
                response: Response<List<PriceListModel>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _priceLists.value = it
                    }
                }
            }

            override fun onFailure(call: Call<List<PriceListModel>>, t: Throwable) {

            }
        })
    }

    fun getStock(
        productId: String,
        onResponse: (Double) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        productsService.getStock(productId).enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                if (response.isSuccessful) {
                    response.body()?.let(onResponse)
                }
            }

            override fun onFailure(call: Call<Double>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun createFavorite(
        productId: String,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        productsService.createFavorite(productId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onResponse()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun createProduct(
        product: CreateProductModel,
        prices: List<PriceModel>,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val productRequest = ProductRequest(product, prices)
        productsService.createProduct(productRequest).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onResponse()
                } else {
                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.string())
                        onFailure(jsonObject.getString("message"))
                    }
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun deleteFavorite(
        productId: String,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        productsService.deleteFavorite(productId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onResponse()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun getProductsByCategory(
        categoryId: String,
        onResponse: (List<ProductModel>) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        productsService.getProductsByCategory(categoryId)
            .enqueue(object : Callback<List<ProductModel>> {
                override fun onResponse(
                    call: Call<List<ProductModel>>,
                    response: Response<List<ProductModel>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let(onResponse)
                    }
                }

                override fun onFailure(call: Call<List<ProductModel>>, t: Throwable) {
                    t.message?.let { onFailure(it) }
                }
            })
    }

    fun getProductsByPage(
        pageIndex: Int,
        pageSize: Int,
        onResponse: (List<ProductModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        productsService.getProductsByPage(pageIndex, pageSize)
            .enqueue(object : Callback<List<ProductModel>> {
                override fun onResponse(
                    call: Call<List<ProductModel>>,
                    response: Response<List<ProductModel>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let(onResponse)
                    }
                }

                override fun onFailure(call: Call<List<ProductModel>>, t: Throwable) {
                    t.message?.let { onFailure(it) }
                }
            })
    }

    fun trackStock(
        productId: String,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        productsService.trackStock(productId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onResponse()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

}