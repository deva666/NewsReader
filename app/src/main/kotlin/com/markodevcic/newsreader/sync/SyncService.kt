package com.markodevcic.newsreader.sync

import com.markodevcic.newsreader.api.NewsApi
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.extensions.executeAsync
import com.markodevcic.newsreader.extensions.launchAsync
import com.markodevcic.newsreader.storage.ArticlesRepository
import com.markodevcic.newsreader.storage.SourcesRepository

class SyncService constructor(private val newsApi: NewsApi) {

	suspend fun downloadSources(categories: Set<String>) {
		val sourcesRepository = SourcesRepository()
		sourcesRepository.deleteAll()
		val deferreds = categories.map { c -> newsApi.getSources(c).launchAsync() }
		deferreds.forEach { def ->
			val response = def.await()
			sourcesRepository.addAll(response.sources)
		}
//		categories.forEach { cat ->
//			val response = newsApi.getSources(cat).executeAsync()
//			sourcesRepository.addAll(response.sources)
//		}
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