package com.markodevcic.newsreader.storage

import com.markodevcic.newsreader.extensions.inTransactionAsync
import com.markodevcic.newsreader.extensions.loadAsync
import io.realm.*

abstract class RepositoryBase<T> : Repository<T> where T : RealmModel {

	protected val realm = Realm.getDefaultInstance()

	abstract protected val primaryKey: String

	override suspend fun getById(id: String): T? {
		return realm.where(clazz)
				.equalTo(primaryKey, id)
				.findFirstAsync()
				.loadAsync()
	}

	override suspend fun getAll(): List<T> {
		return realm.where(clazz)
				.findAllAsync()
				.loadAsync()
	}

	override suspend fun delete(items: List<T>) {
		return realm.inTransactionAsync {
			if (items is RealmResults<T>) {
				items.deleteAllFromRealm()
			}
		}
	}

	override suspend fun deleteAll() {
		return realm.inTransactionAsync { delete(clazz) }
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

	override suspend fun add(item: T) {
		return realm.inTransactionAsync {
			copyToRealm(item)
		}
	}

	override suspend fun addAll(items: List<T>) {
		return realm.inTransactionAsync { copyToRealm(items) }
	}

	override fun count(query: RealmQuery<T>.() -> Unit): Long {
		val results = realm.where(clazz)
		query(results)
		return results.count()
	}

	override suspend fun query(init: RealmQuery<T>.() -> Unit, sortField: String?, descending: Boolean): List<T> {
		val results = realm.where(clazz)
		init(results)
		if (sortField == null) {
			return results.findAllAsync()
					.loadAsync()
		} else {
			return results.findAllSortedAsync(sortField, if (descending) Sort.DESCENDING else Sort.ASCENDING)
					.loadAsync()
		}
	}

	override fun close() {
		realm.close()
	}
}