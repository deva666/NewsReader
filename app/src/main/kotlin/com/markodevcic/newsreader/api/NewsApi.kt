package com.markodevcic.newsreader.api

import com.markodevcic.newsreader.BuildConfig
import com.markodevcic.newsreader.data.ArticleResponse
import com.markodevcic.newsreader.data.SourceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

	@GET("top-headlines")
	fun getArticles(@Query("sources") source: String,
					@Query("apiKey") apiKey: String = BuildConfig.NEWS_READER_API_KEY): Call<ArticleResponse>

	@GET("sources")
	fun getSources(@Query("category") category: String,
				   @Query("apiKey") apiKey: String = BuildConfig.NEWS_READER_API_KEY,
				   @Query("language") lang: String = "en"): Call<SourceResponse>

	@GET("everything")
	fun search(@Query("q") query: String,
			   @Query("apiKey") apiKey: String = BuildConfig.NEWS_READER_API_KEY) : Call<ArticleResponse>
}