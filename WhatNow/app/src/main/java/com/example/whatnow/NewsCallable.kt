package com.example.whatnow

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsCallable {

//    @GET("/v2/top-headlines?country=us&category=general&pageSize=30&apiKey=86e3ef7d102044b99ed021e109eb5388")
//    fun getNews(): Call<News>

    @GET("/v2/top-headlines")
    fun getNews(
        @Query("category") category: String,
        @Query("country") country: String,
        @Query("pageSize") pageSize: Int = 30,
        @Query("apiKey") apiKey : String = "86e3ef7d102044b99ed021e109eb5388"
    ) : Call<News>


}