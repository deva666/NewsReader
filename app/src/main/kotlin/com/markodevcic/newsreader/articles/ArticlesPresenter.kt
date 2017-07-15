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

	suspend fun onArticleRead(articleUrl: String) {
		articlesRepository.update(articleUrl) {
			isUnread = false
		}
	}

	suspend fun getAllArticles(): List<Article> {
		return articlesRepository.getAll()
	}

	suspend fun getArticlesInCategory(category: String) : List<Article> {
		return articlesRepository.query {
			equalTo("category", category)
			equalTo("isUnread", true)
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

	suspend fun syncCategory(category: String?) {
		val sources = sourcesRespository.query {
			if (category != null) {
				equalTo("category", category)
			}
		}
		sources.forEach { src ->
			syncService.downloadArticlesAsync(src)
		}
	}

	override fun close() {
		articlesRepository.close()
	}
}