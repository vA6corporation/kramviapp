package com.example.kramviapp.openTurn

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.kramviapp.models.CreateExpenseModel
import com.example.kramviapp.models.CreateTurnModel
import com.example.kramviapp.models.ExpenseModel
import com.example.kramviapp.models.SummaryPaymentModel
import com.example.kramviapp.models.SummarySaleItemModel
import com.example.kramviapp.models.TurnModel
import com.example.kramviapp.requests.ExpenseResquest
import com.example.kramviapp.requests.TurnRequest
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


class OpenTurnViewModel: ViewModel() {

    private val _turn: MutableStateFlow<TurnModel?> = MutableStateFlow(null)
    val turn = _turn.asStateFlow()

    private val _summaryPayments: MutableStateFlow<List<SummaryPaymentModel>> = MutableStateFlow(listOf())
    val summaryPayments = _summaryPayments.asStateFlow()

//    private val _summarySaleItems: MutableStateFlow<List<SummarySaleItemModel>> = MutableStateFlow(listOf())
//    val summarySaleItems = _summarySaleItems.asStateFlow()

    private val _expenses: MutableStateFlow<MutableList<ExpenseModel>> = MutableStateFlow(mutableStateListOf())
    val expenses = _expenses
    fun addExpense(expense: ExpenseModel) {
        _expenses.value = _expenses.value.toMutableList().apply {
            add(expense)
        }
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var openTurnService = retrofit.create(OpenTurnService::class.java)

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

        openTurnService = retrofit.create(OpenTurnService::class.java)
    }

    fun loadCloseTurn(
        turnId: String,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        openTurnService.getCloseTurn(turnId).enqueue(object: Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    _turn.value = null
                    onResponse()
                }
            }
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun loadSummaryPaymentsByTurn(turnId: String) {
        openTurnService.getSummaryPaymentsByTurn(turnId).enqueue(object: Callback<List<SummaryPaymentModel>> {
            override fun onResponse(call: Call<List<SummaryPaymentModel>>, response: Response<List<SummaryPaymentModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _summaryPayments.value = it
                    }
                }
            }
            override fun onFailure(call: Call<List<SummaryPaymentModel>>, t: Throwable) {
            }
        })
    }

    fun loadSummarySaleItemsByTurn(
        turnId: String,
        onResponse: (List<SummarySaleItemModel>) -> Unit,
    ) {
        openTurnService.getSummarySaleItemsByTurn(turnId).enqueue(object: Callback<List<SummarySaleItemModel>> {
            override fun onResponse(call: Call<List<SummarySaleItemModel>>, response: Response<List<SummarySaleItemModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
//                        _summarySaleItems.value = it
                        onResponse(it)
                    }
                }
            }
            override fun onFailure(call: Call<List<SummarySaleItemModel>>, t: Throwable) {
            }
        })
    }

    fun loadExpensesByTurn(turnId: String) {
        openTurnService.getExpensesByTurn(turnId).enqueue(object: Callback<MutableList<ExpenseModel>> {
            override fun onResponse(call: Call<MutableList<ExpenseModel>>, response: Response<MutableList<ExpenseModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _expenses.value = it
                    }
                }
            }
            override fun onFailure(call: Call<MutableList<ExpenseModel>>, t: Throwable) {
            }
        })
    }

    fun loadTurnOffice() {
        openTurnService.getTurnOffice().enqueue(object: Callback<TurnModel> {
            override fun onResponse(call: Call<TurnModel>, response: Response<TurnModel>) {
                if (response.isSuccessful) {
                    _turn.value = response.body()
                }
            }
            override fun onFailure(call: Call<TurnModel>, t: Throwable) {
            }
        })
    }

    fun loadTurnUser() {
        openTurnService.getTurnUser().enqueue(object: Callback<TurnModel> {
            override fun onResponse(call: Call<TurnModel>, response: Response<TurnModel>) {
                if (response.isSuccessful) {
                    _turn.value = response.body()
                }
            }
            override fun onFailure(call: Call<TurnModel>, t: Throwable) {
            }
        })
    }

    fun createTurnOffice(
        turn: CreateTurnModel,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        openTurnService.createTurnOffice(turn).enqueue(object: Callback<TurnModel> {
            override fun onResponse(call: Call<TurnModel>, response: Response<TurnModel>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _turn.value = response.body()
                        onResponse()
                    }
                } else {
                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.string())
                        onFailure(jsonObject.getString("message"))
                    }
                }
            }
            override fun onFailure(call: Call<TurnModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun createTurnUser(
        turn: CreateTurnModel,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        openTurnService.createTurnUser(turn).enqueue(object: Callback<TurnModel> {
            override fun onResponse(call: Call<TurnModel>, response: Response<TurnModel>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _turn.value = response.body()
                        onResponse()
                    }
                } else {
                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.string())
                        onFailure(jsonObject.getString("message"))
                    }
                }
            }
            override fun onFailure(call: Call<TurnModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun createExpense(
        expense: CreateExpenseModel,
        onResponse: (ExpenseModel) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val expenseResquest = ExpenseResquest(expense)
        openTurnService.createExpense(expenseResquest).enqueue(object: Callback<ExpenseModel> {
            override fun onResponse(call: Call<ExpenseModel>, response: Response<ExpenseModel>) {
                if (response.isSuccessful) {
                    response.body()?.let(onResponse)
                }
            }
            override fun onFailure(call: Call<ExpenseModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun updateExpense(
        expenseId: String,
        expense: CreateExpenseModel,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val expenseResquest = ExpenseResquest(expense)
        openTurnService.updateExpense(expenseId, expenseResquest).enqueue(object: Callback<Unit> {
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

    fun deleteExpense(
        expenseId: String,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        openTurnService.deleteExpense(expenseId).enqueue(object: Callback<Unit> {
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

    fun updateTurn(
        turnId: String,
        turn: TurnModel,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val turnRequest = TurnRequest(turn)
        openTurnService.updateTurn(turnId, turnRequest).enqueue(object: Callback<Unit> {
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