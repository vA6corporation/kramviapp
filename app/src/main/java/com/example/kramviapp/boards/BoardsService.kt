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

interface BoardsService {

    @GET("boards/activeBoards")
    fun getActiveBoards(): Call<List<BoardModel>>

    @GET("boards/activeBoardByTable/{tableId}")
    fun getActiveBoardByTable(@Path("tableId") tableId: String): Call<BoardModel>

    @GET("boards/changeBoard/{boardId}/{tableId}")
    fun changeBoard(
        @Path("boardId") boardId: String,
        @Path("tableId") tableId: String,
    ): Call<Unit>

    @POST("boards/{tableId}")
    fun createBoard(
        @Path("tableId") tableId: String,
        @Body boardRequest: BoardRequest,
    ): Call<BoardModel>

    @DELETE("boards/{boardId}")
    fun deleteBoard(
        @Path("boardId") boardId: String,
    ): Call<Unit>

    @DELETE("boards/boardItem/{boardId}/{boardItemId}/{quantity}")
    fun deleteBoardItem(
        @Path("boardId") boardId: String,
        @Path("boardItemId") boardItemId: String,
        @Path("quantity") quantity: Double,
    ): Call<Unit>

}