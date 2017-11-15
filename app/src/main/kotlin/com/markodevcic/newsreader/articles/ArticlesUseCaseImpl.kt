package com.markodevcic.newsreader.articles

import android.util.ArrayMap
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.storage.Repository
import io.realm.Sort
import java.util.*

private const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000

class ArticlesUseCaseImpl(private val articlesRepository: Repository<Article>,
						  private val sourcesRepository: Repository<Source>): ArticlesUseCase {

	override fun hasArticles(): Boolean = articlesRepository.count() > 0L

	suspend override fun deleteOldArticlesAsync(daysToDelete: Int) {
		val deleteThreshold = Date().time - daysToDelete * MILLIS_IN_DAY
		articlesRepository.delete {
			lessThan("publishedAt", deleteThreshold)
		}
	}

	suspend override fun onCategoriesChangedAsync(deletedCategories: Collection<String>) {
		articlesRepository.delete {
			`in`("category", deletedCategories.toTypedArray())
		}
	}

	override fun markArticlesRead(vararg url: String) {
		url.forEach {
			articlesRepository.update(it) {
				isUnread = false
			}
		}
	}

	override fun markArticlesUnread(vararg url: String) {
		url.forEach { u ->
			articlesRepository.update(u) {
				isUnread = true
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

	suspend override fun getSourcesAsync(category: String?, selectedCategories: Collection<String>): List<Source> {
		return sourcesRepository.query({
			if (category != null) {
				equalTo("category", category)
			} else {
				`in`("category", selectedCategories.toTypedArray())
			}
		}, null, null)
	}

	override fun getUnreadCount(categories: Collection<String>): Map<String, Long> {
		val result = ArrayMap<String, Long>()
		categories.forEach { cat ->
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
