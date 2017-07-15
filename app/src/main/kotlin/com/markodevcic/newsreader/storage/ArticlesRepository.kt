package com.markodevcic.newsreader.storage

import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.extensions.loadAsync
import io.realm.Sort

class ArticlesRepository : RepositoryBase<Article>() {

	override val primaryKey: String
		get() = "url"

	override val clazz: Class<Article>
		get() = Article::class.java

	override suspend fun getAll(): List<Article> {
		return realm.where(clazz)
				.findAllSortedAsync("isUnread", Sort.DESCENDING)
				.loadAsync()
	}
}