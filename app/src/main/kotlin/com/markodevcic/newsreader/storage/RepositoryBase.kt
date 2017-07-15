package com.markodevcic.newsreader.storage

import com.markodevcic.newsreader.extensions.inTransactionAsync
import com.markodevcic.newsreader.extensions.loadAsync
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmQuery

abstract class RepositoryBase<T> : Repository<T> where T : RealmModel {

	protected val realm = Realm.getDefaultInstance()

	abstract protected val primaryKey: String

	override suspend fun getById(id: String): T {
		return realm.where(clazz)
				.equalTo(primaryKey, id)
				.findFirstAsync()
				.loadAsync()
	}

	override fun getAll(): List<T> {
		return realm.where(clazz)
				.findAll()
//				.loadAsync()
	}

	override suspend fun deleteAll() {
		return realm.inTransactionAsync { delete(clazz) }
	}

	override suspend fun update(id: String, modifier: T.() -> Unit) {
		return realm.inTransactionAsync {
			val dbItem = where(clazz)
					.equalTo(primaryKey, id)
					.findFirst()
			modifier(dbItem)
		}
	}

	override suspend fun add(item: T) {
		return realm.inTransactionAsync {
			copyToRealm(item)
		}
	}

	override suspend fun addAll(items: List<T>) {
		return realm.inTransactionAsync { copyToRealmOrUpdate(items) }
	}

	override fun count(query: RealmQuery<T>.() -> Unit): Long {
		val results = realm.where(clazz)
		query(results)
		return results.count()
	}

	override fun query(init: RealmQuery<T>.() -> Unit): List<T> {
		val results = realm.where(clazz)
		init(results)
		return results.findAll()
//				.loadAsync()
	}

	override fun close() {
		realm.close()
	}
}