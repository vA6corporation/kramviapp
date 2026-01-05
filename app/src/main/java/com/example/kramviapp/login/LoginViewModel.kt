package com.example.kramviapp.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.kramviapp.DataStoreManager
import com.example.kramviapp.models.BusinessModel
import com.example.kramviapp.models.LoginResultModel
import com.example.kramviapp.models.ModuleModel
import com.example.kramviapp.models.OfficeModel
import com.example.kramviapp.models.ProfileModel
import com.example.kramviapp.models.SettingModel
import com.example.kramviapp.models.UserModel
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

class LoginViewModel(application: Application): AndroidViewModel(application) {

    private val dataStore = DataStoreManager(application)

    private val _businesses = MutableStateFlow<List<BusinessModel>>(listOf())
    val businesses = _businesses.asStateFlow()

    private val _modules = MutableStateFlow<List<ModuleModel>>(listOf())
    val modules = _modules.asStateFlow()
    fun setModules(modules: List<ModuleModel>) { _modules.value = modules }

    private val _office = MutableStateFlow(OfficeModel())
    val office = _office.asStateFlow()
    fun setOffice(office: OfficeModel) { _office.value = office }

    private val _offices = MutableStateFlow<List<OfficeModel>?>(null)
    val offices = _offices.asStateFlow()

    private val _business = MutableStateFlow(BusinessModel())
    val business = _business.asStateFlow()
    fun setBusiness(business: BusinessModel) { _business.value = business }

    private val _setting = MutableStateFlow(SettingModel())
    val setting = _setting.asStateFlow()
    fun setSetting(setting: SettingModel) { _setting.value = setting }

    private val _user = MutableStateFlow(UserModel())
    val user = _user.asStateFlow()
    fun setUser(user: UserModel) { _user.value = user }

    private val _profile: MutableStateFlow<ProfileModel?> = MutableStateFlow(null)
    val profile = _profile.asStateFlow()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var loginService = retrofit.create(LoginService::class.java)

    suspend fun getAccessToken(): String {
        return dataStore.read("accessToken") ?: ""
    }

    suspend fun setAccessToken(accessToken: String) {
        dataStore.save("accessToken", accessToken)
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val request: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            chain.proceed(request)
        }.build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        loginService = retrofit.create(LoginService::class.java)
    }

    fun login(
        email: String,
        password: String,
        onResponse: (LoginResultModel) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val loginObject = LoginObject(email, password)
        loginService.login(loginObject).enqueue(object: Callback<LoginResultModel> {
            override fun onResponse(call: Call<LoginResultModel>, response: Response<LoginResultModel>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _user.value = it.user
                        onResponse(it)
                    }
                } else {
                    onFailure("Usuario o contraseña incorrectos")
                }
            }
            override fun onFailure(call: Call<LoginResultModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun setBusinessOffice(
        business: BusinessModel,
        office: OfficeModel,
        onResponse: (LoginResultModel) -> Unit,
        onFailure: (String) -> Unit
    ) {
        loginService.setBusinessOffice(
            business._id,
            office._id,
            office.activityId
        ).enqueue(object: Callback<LoginResultModel> {
            override fun onResponse(call: Call<LoginResultModel>, response: Response<LoginResultModel>) {
                if (response.isSuccessful) {
                    response.body()?.let(onResponse)
                } else {
                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.string())
                        onFailure(jsonObject.getString("message"))
                    }
                }
            }
            override fun onFailure(call: Call<LoginResultModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun loadProfile(
        onFailure: (String) -> Unit,
    ) {
        loginService.loadProfile().enqueue(object: Callback<ProfileModel> {
            override fun onResponse(call: Call<ProfileModel>, response: Response<ProfileModel>) {
                if (response.isSuccessful) {
                    _profile.value = response.body()
                } else {
                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.string())
                        onFailure(jsonObject.getString("message"))
                    }
                }
            }
            override fun onFailure(call: Call<ProfileModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun loadOfficesByActivity() {
        loginService.loadOfficesByActivity().enqueue(object: Callback<List<OfficeModel>> {
            override fun onResponse(call: Call<List<OfficeModel>>, response: Response<List<OfficeModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _offices.value = it
                    }
                }
            }
            override fun onFailure(call: Call<List<OfficeModel>>, t: Throwable) {

            }
        })
    }

    fun loadBusinesses(
        onResponse: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        loginService.loadBusinesses().enqueue(object: Callback<List<BusinessModel>> {
            override fun onResponse(call: Call<List<BusinessModel>>, response: Response<List<BusinessModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _businesses.value = it
                        onResponse()
                    }
                }
            }
            override fun onFailure(call: Call<List<BusinessModel>>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

}