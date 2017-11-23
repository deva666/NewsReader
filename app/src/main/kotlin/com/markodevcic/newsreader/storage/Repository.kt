package com.markodevcic.newsreader.storage

import io.realm.RealmModel
import io.realm.RealmQuery
import io.realm.Sort
import rx.Observable
import java.io.Closeable

interface Repository<T> : Closeable  where T : RealmModel {

	fun getById(id: String): T?

	fun getAll(): Observable<out List<T>>

	fun deleteAll(): Observable<Unit>

	fun delete(query: RealmQuery<T>.() -> Unit): Observable<Unit>

	fun update(id:String, modifier: T.() -> Unit)

	fun add(item: T)

	fun addAll(items: List<T>): Observable<Unit>

	fun count(filter: RealmQuery<T>.() -> Unit): Long

	fun count(): Long

	fun query(init: RealmQuery<T>.() -> Unit, sortField: Array<String>?, order: Array<Sort>?): Observable<out List<T>>

	val clazz: Class<T>
}