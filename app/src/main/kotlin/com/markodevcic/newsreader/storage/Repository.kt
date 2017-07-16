package com.markodevcic.newsreader.storage

import io.realm.RealmModel
import io.realm.RealmQuery
import java.io.Closeable

interface Repository<T> : Closeable  where T : RealmModel {

	suspend fun getById(id: String): T?

	suspend fun getAll(): List<T>

	suspend fun deleteAll()

	suspend fun delete(items: List<T>)

	fun update(id:String, modifier: T.() -> Unit)

	fun update(items: Array<T>, modifier: T.() -> Unit)

	suspend fun add(item: T)

	suspend fun addAll(items: List<T>)

	fun count(query: RealmQuery<T>.() -> Unit): Long

	suspend fun query(init: RealmQuery<T>.() -> Unit, sortField: String?, descending: Boolean): List<T>

	val clazz: Class<T>
}