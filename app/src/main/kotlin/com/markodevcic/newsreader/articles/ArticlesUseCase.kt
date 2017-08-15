package com.markodevcic.newsreader.articles

import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.Source
import java.io.Closeable

interface ArticlesUseCase : Closeable {
	fun hasArticles(): Boolean
	fun markArticleRead(vararg url: String)
	fun getUnreadCount(): Map<String, Long>
	suspend fun getArticlesAsync(category: String?): List<Article>
	suspend fun getSourcesAsync(category: String?): List<Source>
	suspend fun onCategoriesChangedAsync()
}