package com.markodevcic.newsreader.sync

import com.markodevcic.newsreader.api.NewsApi
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.extensions.awaitAll
import com.markodevcic.newsreader.extensions.executeAsync
import com.markodevcic.newsreader.extensions.launchAsync
import com.markodevcic.newsreader.storage.Repository
import javax.inject.Inject
import javax.inject.Provider

class SyncService @Inject constructor(private val newsApi: NewsApi,
									  private val sourcesRepository: Provider<Repository<Source>>,
									  private val articlesRepository: Provider<Repository<Article>>) {

	suspend fun downloadSources(categories: Collection<String>) : Collection<Source> {
		sourcesRepository.get().use { repo ->
			repo.deleteAll()
			val downloadJobs = categories.map { cat -> newsApi.getSources(cat).launchAsync() }
			val sources = downloadJobs.awaitAll().flatMap { job -> job.sources }
			repo.addAll(sources)
			return sources
		}
	}

	suspend fun downloadArticles(source: Source) {
		val response = newsApi.getArticles(source.id).executeAsync()
		response.articles.forEach { article -> article.category = source.category }
		articlesRepository.get().use { repo ->
			repo.addAll(response.articles)
		}
	}
}