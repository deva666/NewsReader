package com.markodevcic.newsreader.articles

import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.Source
import rx.Observable
import java.io.Closeable

interface ArticlesUseCase : Closeable {
	fun hasArticles(): Boolean
	fun markArticlesRead(vararg url: String)
	fun markArticlesUnread(vararg url: String)
	fun getUnreadCount(categories: Collection<String>): Map<String, Long>

	fun getArticles(category: String?): Observable<out List<Article>>
	fun getSources(category: String?, selectedCategories: Collection<String>): Observable<out List<Source>>
	fun onSelectedCategoriesChanged(deletedCategories: Collection<String>): Observable<Unit>
	fun deleteOldArticles(daysToDelete: Int): Observable<Unit>
}