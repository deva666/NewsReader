@file:Suppress("MemberVisibilityCanPrivate")

package com.markodevcic.newsreader.api

import android.util.Log
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

object ApiFactory {

	inline fun <reified T> create(): T {
		val httpClientBuilder = OkHttpClient.Builder()
		httpClientBuilder.readTimeout(60, TimeUnit.SECONDS)
				.connectTimeout(60, TimeUnit.SECONDS)

		val loggingInterceptor = HttpLoggingInterceptor({ l -> Log.d("HTTP", l) })
		loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

		httpClientBuilder.addInterceptor(loggingInterceptor)

		val httpClient = httpClientBuilder.build()

		val retrofit = Retrofit.Builder()
				.baseUrl("https://newsapi.org/v1/")
				.addConverterFactory(JacksonConverterFactory.create(createObjectMapper()))
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.client(httpClient)
				.build()

		return retrofit.create(T::class.java)
	}

	fun createObjectMapper(): ObjectMapper {
		val objectMapper = ObjectMapper()
		objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false)
		objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false)
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
		return objectMapper
	}
}