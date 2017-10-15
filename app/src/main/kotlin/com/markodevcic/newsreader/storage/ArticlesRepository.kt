package com.markodevcic.newsreader.storage

import com.markodevcic.newsreader.data.Article

class ArticlesRepository : RepositoryBase<Article>() {

	override val primaryKey: String
		get() = "url"

	override val clazz: Class<Article>
		get() = Article::class.java
}