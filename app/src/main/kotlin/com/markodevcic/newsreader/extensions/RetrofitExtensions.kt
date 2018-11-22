package com.markodevcic.newsreader.extensions

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> Call<T>.executeAsync(): T {
	return suspendCancellableCoroutine { continuation ->
		this.enqueue(object : Callback<T> {
			override fun onFailure(call: Call<T>?, t: Throwable) {
				continuation.resumeWithException(t)
			}

			override fun onResponse(call: Call<T>?, response: Response<T>) {
				if (response.isSuccessful) {
					continuation.resume(response.body()!!)
				} else {
					continuation.resumeWithException(HttpException(response))
				}
			}
		})
		continuation.invokeOnCancellation {
			if (continuation.isCancelled) {
				try {
					cancel()
				} catch (e: Throwable) {
					//ignored
				}
			}
		}
	}
}

fun <T> Call<T>.launchAsync(): Deferred<T> {
	return GlobalScope.async {
		this@launchAsync.execute().body()!!
	}
}

suspend fun <T> List<Deferred<T>>.waitAllAsync(): List<T> = this.map { job -> job.await() }