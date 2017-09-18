package com.markodevcic.newsreader.extensions

import io.realm.Realm
import rx.Emitter
import rx.Observable

fun Realm.transactionAsObservable(init: Realm.() -> Unit): Observable<Unit> {
	return Observable.create({ emitter ->
		this.executeTransactionAsync({ r ->
			r.init()
		}, {
			emitter.onNext(Unit)
			emitter.onCompleted()
		}, { fail ->
			emitter.onError(fail)
		})
	}, Emitter.BackpressureMode.DROP)
}