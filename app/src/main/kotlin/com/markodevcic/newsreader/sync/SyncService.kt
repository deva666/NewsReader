package com.markodevcic.newsreader.sync

import android.content.SharedPreferences
import android.util.Log
import com.markodevcic.newsreader.api.NewsApi
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.extensions.awaitAll
import com.markodevcic.newsreader.extensions.editorCommit
import com.markodevcic.newsreader.extensions.executeAsync
import com.markodevcic.newsreader.extensions.launchAsync
import com.markodevcic.newsreader.storage.Repository
import com.markodevcic.newsreader.util.KEY_LAST_DELETE_DATE
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class SyncService @Inject constructor(private val newsApi: NewsApi,
									  private val sourcesRepository: Provider<Repository<Source>>,
									  private val articlesRepository: Provider<Repository<Article>>,
									  private val sharedPreferences: SharedPreferences) {

	suspend fun downloadSourcesAsync(categories: Collection<String>) : Collection<Source> {
		sourcesRepository.get().use { repo ->
			repo.deleteAll()
			val downloadJobs = categories.map { cat -> newsApi.getSources(cat).launchAsync() }
			val sources = downloadJobs.awaitAll().flatMap { job -> job.sources }
			repo.addAll(sources)
			return sources
		}
	}

	suspend fun downloadArticlesAsync(source: Source) {
		val response = newsApi.getArticles(source.id).executeAsync()
		response.articles.forEach { article -> article.category = source.category }
		val repo = articlesRepository.get()
		repo.addAll(response.articles)
		repo.close()
	}

	fun deleteOldItemsAsync() {
		val lastDeleteDate = sharedPreferences.getLong(KEY_LAST_DELETE_DATE, 0)
		if (Date().time - THREE_DAYS_AGO >= lastDeleteDate) {
			async(CommonPool) {
				articlesRepository.get().use { repo ->

				}
				sharedPreferences.editorCommit {
					putLong(KEY_LAST_DELETE_DATE, Date().time)
				}
			}
		}
	}

	companion object {
		private const val THREE_DAYS_AGO = 3 * 24 * 60 * 60 * 1000
	}
}