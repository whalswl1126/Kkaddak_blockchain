package com.ssafy.kkaddak.data.remote.service

import com.ssafy.kkaddak.data.remote.datasource.base.BaseResponse
import com.ssafy.kkaddak.data.remote.datasource.song.SongResponse
import retrofit2.http.*

interface SongApiService {

    @GET("/api/v2/song/list/all")
    suspend fun getMusics(): BaseResponse<List<SongResponse>>

    @POST("/api/v2/song/like")
    suspend fun requestBookmark(@Body songId: String): BaseResponse<Boolean>
    
    @GET("/api/v2/song/{songId}")
    suspend fun getMusic(@Path("songId") songId: String): BaseResponse<SongResponse>

    @GET("/api/v2/song/song/myPlay/list")
    suspend fun getPlayList(): BaseResponse<List<SongResponse>>
}