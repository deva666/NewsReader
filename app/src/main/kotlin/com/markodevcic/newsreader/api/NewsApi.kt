package com.markodevcic.newsreader.api

import com.markodevcic.newsreader.BuildConfig
import com.markodevcic.newsreader.data.ArticleResponse
import com.markodevcic.newsreader.data.SourceResponse
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface NewsApi {

	@GET("articles")
	fun getArticles(@Query("source") source: String,
					@Query("apiKey") apiKey: String = BuildConfig.NEWS_READER_API_KEY): Observable<ArticleResponse>

	@GET("sources")
	fun getSources(@Query("category") category: String,
				   @Query("apiKey") apiKey: String = BuildConfig.NEWS_READER_API_KEY,
				   @Query("language") lang: String = "en"): Observable<SourceResponse>
}