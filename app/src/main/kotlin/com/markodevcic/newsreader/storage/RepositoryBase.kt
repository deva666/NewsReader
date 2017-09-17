package com.markodevcic.newsreader.storage

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmQuery
import io.realm.Sort
import rx.Observable

abstract class RepositoryBase<T> : Repository<T> where T : RealmModel {

	protected val realm: Realm = Realm.getDefaultInstance()

	abstract protected val primaryKey: String

	override fun refresh() {
		realm.refresh()
	}

	override fun getById(id: String): T? {
		return realm.where(clazz)
				.equalTo(primaryKey, id)
				.findFirst()
	}

	override fun getAll(): Observable<out List<T>> {
		return realm.where(clazz)
				.findAllAsync()
				.asObservable()
				.filter { r -> r.isLoaded }
				.first()

	}

	override fun delete(query: RealmQuery<T>.() -> Unit): Observable<Unit> {
		return Observable.create { s ->
			realm.executeTransactionAsync { r ->
				val results = r.where(clazz)
				query(results)
				results.findAll().deleteAllFromRealm()
				if (!s.isUnsubscribed) {
					s.onNext(Unit)
					s.onCompleted()
				}
			}
		}
	}

	override fun deleteAll(): Observable<Unit> {
		return Observable.create { s ->
			realm.executeTransactionAsync { r ->
				r.delete(clazz)
				if (!s.isUnsubscribed) {
					s.onNext(Unit)
					s.onCompleted()
				}
			}
		}
	}

	override fun update(id: String, modifier: T.() -> Unit) {
		realm.executeTransaction { r ->
			val dbItem = r.where(clazz)
					.equalTo(primaryKey, id)
					.findFirst()
			modifier(dbItem)
		}
	}

	override fun update(items: Array<T>, modifier: T.() -> Unit) {
		realm.executeTransaction {
			for (item in items) {
				modifier(item)
			}
		}
	}

	override fun add(item: T) {
		return realm.executeTransaction { r ->
			r.copyToRealm(item)
		}
	}

	override fun addAll(items: List<T>): Observable<Unit> {
		return Observable.create { s ->
			realm.executeTransactionAsync { r ->
				r.copyToRealmOrUpdate(items)
				if (!s.isUnsubscribed) {
					s.onNext(Unit)
					s.onCompleted()
				}
			}
		}
	}

	override fun count(query: RealmQuery<T>.() -> Unit): Long {
		val results = realm.where(clazz)
		query(results)
		return results.count()
	}

	override fun count(): Long {
		return realm.where(clazz).count()
	}

	override fun query(init: RealmQuery<T>.() -> Unit, sortField: Array<String>?, order: Array<Sort>?): Observable<out List<T>> {
		val results = realm.where(clazz)
		init(results)
		return if (sortField == null) {
			results.findAllAsync()
					.asObservable()
					.filter { r -> r.isLoaded }
					.first()
		} else {
			results.findAllSortedAsync(sortField, order)
					.asObservable()
					.filter { r -> r.isLoaded }
					.first()
		}
	}

	override fun close() {
		realm.close()
	}
}