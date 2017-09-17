package com.markodevcic.newsreader.extensions

//suspend fun <T> Call<T>.executeAsync(): T {
//	return suspendCancellableCoroutine { continuation ->
//		this.enqueue(object : Callback<T> {
//			override fun onFailure(call: Call<T>?, t: Throwable) {
//				continuation.resumeWithException(t)
//			}
//
//			override fun onResponse(call: Call<T>?, response: Response<T>) {
//				if (response.isSuccessful) {
//					continuation.resume(response.body()!!)
//				} else {
//					continuation.resumeWithException(retrofit2.HttpException(response))
//				}
//			}
//		})
//		continuation.invokeOnCompletion {
//			if (continuation.isCancelled) {
//				try {
//					cancel()
//				} catch (e: Throwable) {
//					//ignored
//				}
//			}
//		}
//	}
//}
//
//fun <T> Call<T>.launchAsync(): Deferred<T> {
//	return async(CommonPool) { this@launchAsync.execute().body()!! }
//}
//
//suspend fun <T> List<Deferred<T>>.waitAllAsync(): List<T> {
//	return this.map { job -> job.await() }
//}