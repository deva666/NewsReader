package com.markodevcic.newsreader.articles

import android.content.SharedPreferences
import android.util.ArrayMap
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.CATEGORIES_TO_RES_MAP
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.storage.Repository
import com.markodevcic.newsreader.util.KEY_CATEGORIES
import io.realm.Sort

class ArticlesUseCaseImpl(private val sharedPreferences: SharedPreferences,
						  private val articlesRepository: Repository<Article>,
						  private val sourcesRepository: Repository<Source>): ArticlesUseCase {

	override fun hasArticles(): Boolean = articlesRepository.count { } > 0L

	suspend override fun onCategoriesChangedAsync() {
		val selectedCategories = sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())
		val deletedCategories = CATEGORIES_TO_RES_MAP.keys.subtract(selectedCategories)
		articlesRepository.delete {
			`in`("category", deletedCategories.toTypedArray())
		}
	}

	override fun markArticleRead(vararg url: String) {
		url.forEach {
			articlesRepository.update(it) {
				isUnread = false
			}
		}
	}

	suspend override fun getArticlesAsync(category: String?): List<Article> {
		return articlesRepository.query({
			if (category != null) {
				equalTo("category", category)
			} else {
				equalTo("isUnread", true)
			}
		}, arrayOf("isUnread", "publishedAt"), arrayOf(Sort.DESCENDING, Sort.DESCENDING))
	}

	suspend override fun getSourcesAsync(category: String?): List<Source> {
		return sourcesRepository.query({
			if (category != null) {
				equalTo("category", category)
			} else {
				val selectedCategories = sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())
				`in`("category", selectedCategories.toTypedArray())
			}
		}, null, null)
	}

	override fun getUnreadCount(): Map<String, Long> {
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

	override fun close() {
		articlesRepository.close()
		sourcesRepository.close()
	}
}
