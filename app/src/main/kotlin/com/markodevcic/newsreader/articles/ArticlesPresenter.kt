package com.markodevcic.newsreader.articles

import android.content.SharedPreferences
import com.markodevcic.newsreader.Presenter
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.CATEGORIES_TO_RES_MAP
import com.markodevcic.newsreader.sync.SyncService
import com.markodevcic.newsreader.util.KEY_CATEGORIES
import java.io.Closeable
import javax.inject.Inject

class ArticlesPresenter @Inject constructor(private val articlesUseCase: ArticlesUseCase,
											private val sharedPreferences: SharedPreferences,
											private val syncService: SyncService) : Presenter<ArticlesView>, Closeable {
	private lateinit var view: ArticlesView

	override fun bind(view: ArticlesView) {
		this.view = view
	}

	fun onStart() {
		view.onUnreadCountChanged(getUnreadCount())
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
		for (src in sources.toTypedArray()) { //seems to be a bug in coroutines, if looping over normal List, only first item in the list is processed and function never ends... Works OK with Arrays
			downloadCount += syncService.downloadArticlesAsync(src)
		}
		view.onUnreadCountChanged(getUnreadCount())
		view.onArticlesDownloaded(downloadCount)
	}

	fun markArticleRead(vararg articleUrl: String) {
		articlesUseCase.markArticleRead(*articleUrl)
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