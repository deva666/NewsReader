package com.markodevcic.newsreader.sync

import com.markodevcic.newsreader.api.NewsApi
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.extensions.awaitAll
import com.markodevcic.newsreader.extensions.executeAsync
import com.markodevcic.newsreader.extensions.launchAsync
import com.markodevcic.newsreader.storage.ArticlesRepository
import com.markodevcic.newsreader.storage.SourcesRepository
import javax.inject.Inject

class SyncService @Inject constructor(private val newsApi: NewsApi) {

	suspend fun downloadSources(categories: Collection<String>) {
		val sourcesRepository = SourcesRepository()
		sourcesRepository.deleteAll()
		val downloadJobs = categories.map { c -> newsApi.getSources(c).launchAsync() }
		downloadJobs.awaitAll().forEach { j ->
			sourcesRepository.addAll(j.sources)
		}
		sourcesRepository.close()
	}

	suspend fun downloadArticles(source: Source) {
		val response = newsApi.getArticles(source.id).executeAsync()
		response.articles.forEach { article -> article.category = source.category }
		val repo = ArticlesRepository()
		repo.addAll(response.articles)
		repo.close()
	}
}