package com.markodevcic.newsreader.articles

import android.content.SharedPreferences
import com.markodevcic.newsreader.Presenter
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.sync.SyncUseCase
import com.markodevcic.newsreader.util.CATEGORIES_TO_RES_MAP
import com.markodevcic.newsreader.util.KEY_CATEGORIES
import com.markodevcic.newsreader.util.KEY_DELETE_DAYS
import java.io.Closeable
import javax.inject.Inject

class ArticlesPresenter @Inject constructor(private val articlesUseCase: ArticlesUseCase,
											private val sharedPreferences: SharedPreferences,
											private val syncUseCase: SyncUseCase) : Presenter<ArticlesView>, Closeable {
	private lateinit var view: ArticlesView

	override fun bind(view: ArticlesView) {
		this.view = view
	}

	suspend fun onStart() {
		view.onUnreadCountChanged(getUnreadCount())
		val deleteDays = sharedPreferences.getInt(KEY_DELETE_DAYS, 3) // 3 days is default value
		articlesUseCase.deleteOldArticlesAsync(deleteDays)
		if (!articlesUseCase.hasArticles()) {
			view.onNoArticlesAvailable()
		}
	}

	suspend fun onSelectedCategoriesChangedAsync() {
		val selectedCategories = sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())
		val deletedCategories = CATEGORIES_TO_RES_MAP.keys.subtract(selectedCategories)
		articlesUseCase.onCategoriesChangedAsync(deletedCategories)
	}

	suspend fun syncCategoryAsync(category: String?) {
		val categories = sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())
		val sources = articlesUseCase.getSourcesAsync(category, categories)
		var downloadCount = 0
		for (src in sources.toList()) {
			downloadCount += syncUseCase.downloadArticlesAsync(src)
		}
		view.onUnreadCountChanged(getUnreadCount())
		view.onArticlesDownloaded(downloadCount)
	}

	fun markArticlesRead(vararg articleUrl: String) {
		articlesUseCase.markArticlesRead(*articleUrl)
		view.onUnreadCountChanged(getUnreadCount())
	}

	fun markArticlesUnread(vararg articleUrl: String) {
		articlesUseCase.markArticlesUnread(*articleUrl)
		view.onUnreadCountChanged(getUnreadCount())
	}

	suspend fun getArticlesInCategoryAsync(category: String?): List<Article> =
			articlesUseCase.getArticlesAsync(category)

	private fun getUnreadCount(): Map<String, Long>  {
		val categories = sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())
		return articlesUseCase.getUnreadCount(categories)
	}

	override fun close() {
		articlesUseCase.close()
	}
}