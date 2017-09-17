package com.markodevcic.newsreader.sync

import com.markodevcic.newsreader.api.NewsApi
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.storage.Repository
import rx.Observable
import java.util.*
import javax.inject.Provider

class SyncServiceImpl(private val newsApi: NewsApi,
					  private val sourcesRepository: Provider<Repository<Source>>,
					  private val articlesRepository: Provider<Repository<Article>>) : SyncService {

	override fun downloadSourcesAsync(categories: Collection<String>): Observable<Unit> {
		val repo = sourcesRepository.get()
		return repo.deleteAll()
				.flatMap { Observable.from(categories) }
				.flatMap { c -> newsApi.getSources(c) }
				.flatMap { s -> repo.addAll(s.sources) }
				.doOnTerminate { repo.close() }
	}

	override fun downloadArticlesAsync(source: Source): Observable<Int> {
		val repo = articlesRepository.get()
		return newsApi.getArticles(source.id)
				.doOnNext { r -> r.articles.forEach { article -> article.category = source.category } }
				.flatMap { r -> Observable.from(r.articles) }
				.filter { article -> repo.getById(article.url) == null }
				.doOnNext { article ->
					if (article.publishedAt == null || article.publishedAt!! == 0L) {
						article.publishedAt = Date().time
					}
					repo.add(article)
				}
				.count()
	}
}