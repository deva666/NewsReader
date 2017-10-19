package com.markodevcic.newsreader.storage

import io.realm.RealmModel
import io.realm.RealmQuery
import io.realm.Sort
import java.io.Closeable

interface Repository<T> : Closeable  where T : RealmModel {

	fun getById(id: String): T?

	suspend fun deleteAll()

	suspend fun delete(filter: RealmQuery<T>.() -> Unit)

	fun update(id:String, modifier: T.() -> Unit)

	fun add(item: T)

	suspend fun addAll(items: List<T>)

	fun count(filter: RealmQuery<T>.() -> Unit): Long

	fun count(): Long

	suspend fun query(filter: RealmQuery<T>.() -> Unit, sortFields: Array<String>?, orders: Array<Sort>?): List<T>

	val clazz: Class<T>
}