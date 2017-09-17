package com.markodevcic.newsreader.storage

import com.markodevcic.newsreader.data.Article
import io.realm.Sort
import rx.Observable

class ArticlesRepository : RepositoryBase<Article>() {

	override val primaryKey: String
		get() = "url"

	override val clazz: Class<Article>
		get() = Article::class.java

	override fun getAll(): Observable<out List<Article>> {
		return realm.where(clazz)
				.findAllSortedAsync("isUnread", Sort.DESCENDING)
				.asObservable()
				.filter { r -> r.isLoaded }
				.first()
	}
}