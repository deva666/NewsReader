package com.markodevcic.newsreader.storage

import com.markodevcic.newsreader.data.Article
import io.realm.Sort

class ArticlesRepository : RepositoryBase<Article>() {

	override val primaryKey: String
		get() = "url"

	override val clazz: Class<Article>
		get() = Article::class.java

	override fun getAll(): List<Article> {
		return realm.where(clazz)
				.findAllSorted("isUnread", Sort.DESCENDING)
//				.loadAsync()
	}
}