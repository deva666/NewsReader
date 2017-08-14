package com.markodevcic.newsreader.storage

import io.realm.RealmModel
import io.realm.RealmQuery
import io.realm.Sort
import java.io.Closeable

interface Repository<T> : Closeable  where T : RealmModel {

	fun refresh()

	fun getById(id: String): T?

	suspend fun getAll(): List<T>

	suspend fun deleteAll()

	suspend fun delete(query: RealmQuery<T>.() -> Unit)

	fun update(id:String, modifier: T.() -> Unit)

	fun update(items: Array<T>, modifier: T.() -> Unit)

	fun add(item: T)

	suspend fun addAll(items: List<T>)

	fun count(query: RealmQuery<T>.() -> Unit): Long

	suspend fun query(init: RealmQuery<T>.() -> Unit, sortField: Array<String>?, order: Array<Sort>?): List<T>

	val clazz: Class<T>
}