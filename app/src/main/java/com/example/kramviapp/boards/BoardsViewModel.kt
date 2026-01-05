package com.example.kramviapp.boards

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.kramviapp.enums.PrintZoneType
import com.example.kramviapp.enums.PrinterType
import com.example.kramviapp.models.BoardItemModel
import com.example.kramviapp.models.BoardModel
import com.example.kramviapp.models.ProductModel
import com.example.kramviapp.models.SettingModel
import com.example.kramviapp.models.TableModel
import com.example.kramviapp.models.UserModel
import com.example.kramviapp.printers.PrinterCommand58
import com.example.kramviapp.printers.PrinterCommand80
import com.example.kramviapp.requests.BoardRequest
import com.example.kramviapp.requests.BoardWithStockResponse
import com.example.kramviapp.room.PrinterModel
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

class BoardsViewModel: ViewModel() {

    companion object {
        fun printCommand(
            board: BoardModel,
            table: TableModel,
            setting: SettingModel,
            user: UserModel,
            printers: List<PrinterModel>
        ) {
            val kitchenItems: MutableList<BoardItemModel> = mutableListOf()
            val barItems: MutableList<BoardItemModel> = mutableListOf()
            val ovenItems: MutableList<BoardItemModel> = mutableListOf()
            val boxItems: MutableList<BoardItemModel> = mutableListOf()

            for (boardItem in board.boardItems) {
                if (boardItem.printZone == PrintZoneType.COCINA &&
                    boardItem.quantity - boardItem.preQuantity > 0) {
                    boardItem.quantity -= boardItem.preQuantity
                    kitchenItems.add(boardItem)
                }
                if (boardItem.printZone == PrintZoneType.BARRA &&
                    boardItem.quantity - boardItem.preQuantity > 0) {
                    boardItem.quantity -= boardItem.preQuantity
                    barItems.add(boardItem)
                }
                if (boardItem.printZone == PrintZoneType.HORNO &&
                    boardItem.quantity - boardItem.preQuantity > 0) {
                    boardItem.quantity -= boardItem.preQuantity
                    ovenItems.add(boardItem)
                }
                if (boardItem.printZone == PrintZoneType.CAJA &&
                    boardItem.quantity - boardItem.preQuantity > 0) {
                    boardItem.quantity -= boardItem.preQuantity
                    boxItems.add(boardItem)
                }
            }

            for (printer in printers) {
                if (printer.printKitchen && kitchenItems.size > 0) {
                    val printerCommand58 = PrinterCommand58(
                        table,
                        board,
                        kitchenItems,
                        setting,
                        user,
                    )
                    val printerCommand80 = PrinterCommand80(
                        table,
                        board,
                        kitchenItems,
                        setting,
                        user
                    )
                    when (printer.printerType) {
                        PrinterType.BLUETOOTH58 -> {
                            printerCommand58.printBluetooth()
                        }
                        PrinterType.BLUETOOTH80 -> {
                            printerCommand80.printBluetooth()
                        }
                        PrinterType.ETHERNET58 -> {
                            printerCommand58.printEthernet(printer.ipAddress)
                        }

                        PrinterType.ETHERNET80 -> {
                            printerCommand80.printEthernet(printer.ipAddress)
                        }
                    }
                }
                if (printer.printBar && barItems.size > 0) {
                    val printerCommand58 = PrinterCommand58(
                        table,
                        board,
                        barItems,
                        setting,
                        user,
                    )
                    val printerCommand80 = PrinterCommand80(
                        table,
                        board,
                        barItems,
                        setting,
                        user,
                    )
                    when (printer.printerType) {
                        PrinterType.BLUETOOTH58 -> {
                            printerCommand58.printBluetooth()
                        }
                        PrinterType.BLUETOOTH80 -> {
                            printerCommand80.printBluetooth()
                        }
                        PrinterType.ETHERNET58 -> {
                            printerCommand58.printEthernet(printer.ipAddress)
                        }
                        PrinterType.ETHERNET80 -> {
                            printerCommand80.printEthernet(printer.ipAddress)
                        }
                    }
                }
                if (printer.printOven && ovenItems.size > 0) {
                    val printerCommand58 = PrinterCommand58(
                        table,
                        board,
                        ovenItems,
                        setting,
                        user,
                    )
                    val printerCommand80 = PrinterCommand80(
                        table,
                        board,
                        ovenItems,
                        setting,
                        user,
                    )
                    when (printer.printerType) {
                        PrinterType.BLUETOOTH58 -> {
                            printerCommand58.printBluetooth()
                        }
                        PrinterType.BLUETOOTH80 -> {
                            printerCommand80.printBluetooth()
                        }
                        PrinterType.ETHERNET58 -> {
                            printerCommand58.printEthernet(printer.ipAddress)
                        }
                        PrinterType.ETHERNET80 -> {
                            printerCommand80.printEthernet(printer.ipAddress)
                        }
                    }
                }
                if (printer.printBox && boxItems.size > 0) {
                    val printerCommand58 = PrinterCommand58(
                        table,
                        board,
                        boxItems,
                        setting,
                        user,
                    )
                    val printerCommand80 = PrinterCommand80(
                        table,
                        board,
                        boxItems,
                        setting,
                        user,
                    )
                    when (printer.printerType) {
                        PrinterType.BLUETOOTH58 -> {
                            printerCommand58.printBluetooth()
                        }
                        PrinterType.BLUETOOTH80 -> {
                            printerCommand80.printBluetooth()
                        }
                        PrinterType.ETHERNET58 -> {
                            printerCommand58.printEthernet(printer.ipAddress)
                        }
                        PrinterType.ETHERNET80 -> {
                            printerCommand80.printEthernet(printer.ipAddress)
                        }
                    }
                }
            }
        }
    }

    private val _boards: MutableStateFlow<List<BoardModel>> = MutableStateFlow(listOf())
    val boards = _boards.asStateFlow()

    private val _boardItems: MutableStateFlow<MutableList<BoardItemModel>> = MutableStateFlow(mutableStateListOf())
    val boardItems = _boardItems.asStateFlow()

    private val _preBoardItems: MutableStateFlow<MutableList<BoardItemModel>> = MutableStateFlow(mutableStateListOf())

    fun setBoardItems(boardItems: MutableList<BoardItemModel>) {
        _boardItems.value = boardItems.toMutableList()
        _preBoardItems.value = boardItems.toMutableList()
    }

    fun addBoardItem(product: ProductModel, observations: String = "") {
        val boardItems = _boardItems.value.toMutableList()
        var foundIndex = -1
        val boardItem = BoardItemModel(
            _id = "",
            fullName = product.fullName,
            price = product.price,
            quantity = 1.0,
            preQuantity = 0.0,
            igvCode = product.igvCode,
            preIgvCode = product.igvCode,
            product.unitCode,
            observations,
            printZone = product.printZone,
            isTrackStock = product.isTrackStock,
            categoryId = product.categoryId,
            productId = product._id,
            boardId = ""
        )
        for (element in boardItems) {
            if (boardItem.productId == element.productId &&
                boardItem.igvCode == element.igvCode &&
                boardItem.observations == element.observations) {
                foundIndex = boardItems.indexOf(element)
            }
        }
        if (foundIndex >= 0) {
            val quantity = boardItems[foundIndex].quantity
            boardItems[foundIndex] = boardItems[foundIndex].copy(quantity = quantity + 1)
        } else {
            boardItems.add(boardItem)
        }
        _boardItems.value = boardItems
    }

    fun updateBoardItem(index: Int, boardItem: BoardItemModel) {
        _boardItems.value[index] = _boardItems.value[index].copy(
            observations = boardItem.observations,
            quantity = boardItem.quantity,
            price = boardItem.price,
            igvCode = boardItem.igvCode
        )
    }

    fun removeBoardItem(index: Int) {
        val boardItems = _boardItems.value.toMutableList()
        boardItems.removeAt(index)
        _boardItems.value = boardItems
    }

    fun removeAllBoardItems() {
        _boardItems.value = mutableStateListOf()
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var boardsService = retrofit.create(BoardsService::class.java)

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

        boardsService = retrofit.create(BoardsService::class.java)
    }

    fun createBoard(
        tableId: String,
        boardItems: List<BoardItemModel>,
        onResponse: (BoardModel) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val boardRequest = BoardRequest(boardItems, _preBoardItems.value)
        boardsService.createBoard(tableId, boardRequest).enqueue(object: Callback<BoardModel> {
            override fun onResponse(call: Call<BoardModel>, response: Response<BoardModel>) {
                if (response.isSuccessful) {
                    response.body()?.let(onResponse)
                } else {
                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.string())
                        onFailure(jsonObject.getString("message"))
                    }
                }
            }
            override fun onFailure(call: Call<BoardModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun changeBoard(
        boardId: String,
        tableId: String,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        boardsService.changeBoard(boardId, tableId).enqueue(object: Callback<Unit> {
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

    fun deleteBoard(
        boardId: String,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        boardsService.deleteBoard(boardId).enqueue(object: Callback<Unit> {
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

    fun deleteBoardItem(
        boardId: String,
        boardItemId: String,
        quantity: Double,
        onResponse: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        boardsService.deleteBoardItem(boardId, boardItemId, quantity).enqueue(object: Callback<Unit> {
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

    fun loadActiveBoardByTable(
        tableId: String,
        onResponse: (BoardModel) -> Unit,
        onFailure: (String) -> Unit
    ) {
        boardsService.getActiveBoardByTable(tableId).enqueue(object: Callback<BoardModel> {
            override fun onResponse(call: Call<BoardModel>, response: Response<BoardModel>) {
                if (response.isSuccessful) {
                    response.body()?.let(onResponse)
                } else {
                    onFailure("Error desconocido")
                }
            }
            override fun onFailure(call: Call<BoardModel>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun loadActiveBoards() {
        boardsService.getActiveBoards().enqueue(object: Callback<List<BoardModel>> {
            override fun onResponse(call: Call<List<BoardModel>>, response: Response<List<BoardModel>>) {
                if (response.isSuccessful) {
                    response.body()?.let { boards ->
                        _boards.value = boards
                    }
                }
            }
            override fun onFailure(call: Call<List<BoardModel>>, t: Throwable) {

            }
        })
    }

}