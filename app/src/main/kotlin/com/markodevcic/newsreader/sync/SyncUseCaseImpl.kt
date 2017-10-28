package com.markodevcic.newsreader.sync

import com.markodevcic.newsreader.api.NewsApi
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.extensions.executeAsync
import com.markodevcic.newsreader.extensions.launchAsync
import com.markodevcic.newsreader.extensions.waitAllAsync
import com.markodevcic.newsreader.storage.Repository
import kotlinx.coroutines.experimental.async
import java.util.*
import javax.inject.Provider

class SyncUseCaseImpl(private val newsApi: NewsApi,
					  private val sourcesRepository: Provider<Repository<Source>>,
					  private val articlesRepository: Provider<Repository<Article>>) : SyncUseCase {

	override suspend fun downloadSourcesAsync(categories: Collection<String>) {
		sourcesRepository.get().use { repo ->
			val downloadJobs = categories.map { cat -> newsApi.getSources(cat).launchAsync() }
			val sources = downloadJobs.waitAllAsync().flatMap { job -> job.sources }
			repo.deleteAll()
			repo.addAll(sources)
		}
	}

	override suspend fun downloadArticlesAsync(source: Source): Int {
		val response = newsApi.getArticles(source.id).executeAsync()
		response.articles.forEach { it.category = source.category }
		var downloadCount = 0
		async {
			val repository = articlesRepository.get()
			repository.use { repo ->
				for (article in response.articles) {
					if (repo.getById(article.url) == null) {
						if (article.publishedAt == null || article.publishedAt!! == 0L) {
							article.publishedAt = Date().time
						}
						repo.add(article)
						downloadCount++
					}
				}
			}
		}.await()
		return downloadCount
	}
}