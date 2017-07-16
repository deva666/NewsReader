package com.markodevcic.newsreader.extensions

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.RealmResults
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

suspend fun <T> T.loadAsync(): T  where T : RealmModel {
	return suspendCancellableCoroutine { continuation ->
		(this as RealmObject).addChangeListener<T> { item, _ ->
			this.removeAllChangeListeners()
			continuation.resume(item)
		}
	}
}

suspend fun <T> RealmResults<T>.loadAsync(): List<T> where T : RealmModel {
	return suspendCancellableCoroutine { continuation ->
		this.addChangeListener { items, _ ->
			this.removeAllChangeListeners()
			continuation.resume(items)
		}
	}
}

suspend fun Realm.inTransactionAsync(receiver: Realm.() -> Unit) {
	return suspendCancellableCoroutine { continuation ->
		this.executeTransactionAsync { realm ->
			receiver(realm)
			continuation.resume(Unit)
		}
	}
}