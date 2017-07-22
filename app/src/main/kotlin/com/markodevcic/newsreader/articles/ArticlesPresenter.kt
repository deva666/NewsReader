package com.markodevcic.newsreader.articles

import android.content.SharedPreferences
import android.util.ArrayMap
import com.markodevcic.newsreader.Presenter
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.storage.Repository
import com.markodevcic.newsreader.sync.SyncService
import com.markodevcic.newsreader.util.KEY_CATEGORIES
import java.io.Closeable
import javax.inject.Inject

class ArticlesPresenter @Inject constructor(private val articlesRepository: Repository<Article>,
											private val sourcesRespository: Repository<Source>,
											private val sharedPreferences: SharedPreferences,
											private val syncService: SyncService) : Presenter<ArticlesView>, Closeable {
	private lateinit var view: ArticlesView

	override fun bind(view: ArticlesView) {
		this.view = view
	}

	suspend fun markArticleReadAsync(articleUrl: String) {
		articlesRepository.update(articleUrl) {
			isUnread = false
		}
	}

	suspend fun getAllArticlesAsync(): List<Article> {
		return articlesRepository.getAll()
	}

	suspend fun getArticlesInCategoryAsync(category: String?): List<Article> {
		return articlesRepository.query({
			if (category != null) {
				equalTo("category", category)
			} else {
				equalTo("isUnread", true)
			}
		}, "isUnread", true)
	}

	fun markItemsRead(items: Array<Article>) {
		articlesRepository.update(items) {
			isUnread = false
		}
	}

	fun syncUnreadCount(): Map<String, Long> {
		val result = ArrayMap<String, Long>()
		val categories = sharedPreferences.getStringSet(KEY_CATEGORIES, null)
		categories?.forEach { cat ->
			val count = articlesRepository.count {
				equalTo("category", cat)
				equalTo("isUnread", true)
			}
			result.put(cat, count)
		}
		return result
	}

	suspend fun syncCategoryAsync(category: String?) {
		val sources = sourcesRespository.query({
			if (category != null) {
				equalTo("category", category)
			}
		}, null, true)
		for (src in sources.toTypedArray()) { //seems to be a bug in coroutines, if looping over normal List, only first item in the list is processed and function never ends
			syncService.downloadArticlesAsync(src)
		}
	}

	override fun close() {
		articlesRepository.close()
	}
}