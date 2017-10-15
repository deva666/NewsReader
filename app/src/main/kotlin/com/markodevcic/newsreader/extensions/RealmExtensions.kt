package com.markodevcic.newsreader.extensions

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults
import kotlin.coroutines.experimental.suspendCoroutine

suspend fun <T> RealmResults<T>.loadAsync(): List<T> where T : RealmModel {
	return suspendCoroutine { continuation ->
		this.addChangeListener { items, _ ->
			this.removeAllChangeListeners()
			continuation.resume(items)
		}
	}
}

inline suspend fun Realm.inTransactionAsync(crossinline receiver: Realm.() -> Unit) {
	return suspendCoroutine { continuation ->
		this.executeTransactionAsync({ realm ->
			receiver(realm)
		}, {
			this.refresh()
			continuation.resume(Unit)
		}, { fail ->
			continuation.resumeWithException(fail)
		})
	}
}