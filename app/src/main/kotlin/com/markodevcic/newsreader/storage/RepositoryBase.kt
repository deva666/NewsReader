package com.markodevcic.newsreader.storage

import com.markodevcic.newsreader.extensions.inTransactionAsync
import com.markodevcic.newsreader.extensions.loadAsync
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmQuery
import io.realm.Sort

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

	override suspend fun getAll(): List<T> {
		return realm.where(clazz)
				.findAllAsync()
				.loadAsync()
	}

	override suspend fun delete(query: RealmQuery<T>.() -> Unit) {
		return realm.inTransactionAsync {
			val results = where(clazz)
			query(results)
			results.findAll().deleteAllFromRealm()
		}
	}

	override suspend fun deleteAll() = realm.inTransactionAsync { delete(clazz) }

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

	override suspend fun addAll(items: List<T>) =
			realm.inTransactionAsync { copyToRealmOrUpdate(items) }

	override fun count(query: RealmQuery<T>.() -> Unit): Long {
		val results = realm.where(clazz)
		query(results)
		return results.count()
	}

	override suspend fun query(init: RealmQuery<T>.() -> Unit, sortField: Array<String>?, order: Array<Sort>?): List<T> {
		val results = realm.where(clazz)
		init(results)
		return if (sortField == null) {
			results.findAllAsync()
					.loadAsync()
		} else {
			results.findAllSortedAsync(sortField, order)
					.loadAsync()
		}
	}

	override fun close() {
		realm.close()
	}
}