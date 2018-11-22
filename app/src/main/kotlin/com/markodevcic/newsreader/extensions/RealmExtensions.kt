package com.markodevcic.newsreader.extensions

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


suspend fun <T> RealmResults<T>.loadAsync(): List<T> where T : RealmModel {
	return suspendCoroutine { continuation ->
		this.addChangeListener { items, _ ->
			this.removeAllChangeListeners()
			continuation.resume(items)
		}
	}
}

suspend inline fun Realm.inTransactionAsync(crossinline receiver: Realm.() -> Unit) {
	return suspendCoroutine { continuation ->
		this.executeTransactionAsync({ realm ->
			receiver(realm)
		}, {
			continuation.resume(Unit)
		}, { fail ->
			continuation.resumeWithException(fail)
		})
	}
}