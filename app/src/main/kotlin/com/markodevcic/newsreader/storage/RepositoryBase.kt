package com.markodevcic.newsreader.storage

import com.markodevcic.newsreader.extensions.transactionAsObservable
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmQuery
import io.realm.Sort
import rx.Observable

abstract class RepositoryBase<T> : Repository<T> where T : RealmModel {

	protected val realm: Realm = Realm.getDefaultInstance()

	abstract protected val primaryKey: String

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
		return realm.transactionAsObservable {
			val results = where(clazz)
			query(results)
			results.findAll().deleteAllFromRealm()
		}
	}

	override fun deleteAll(): Observable<Unit> {
		return realm.transactionAsObservable {
			delete(clazz)
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

	override fun add(item: T) {
		realm.executeTransactionAsync { r ->
			r.copyToRealm(item)
		}
	}

	override fun addAll(items: List<T>): Observable<Unit> {
		return realm.transactionAsObservable {
			copyToRealmOrUpdate(items)
		}
	}

	override fun count(filter: RealmQuery<T>.() -> Unit): Long {
		val results = realm.where(clazz)
		filter(results)
		return results.count()
	}

	override fun count(): Long = realm.where(clazz).count()

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