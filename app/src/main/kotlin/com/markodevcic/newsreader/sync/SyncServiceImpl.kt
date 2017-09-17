package com.markodevcic.newsreader.sync

import com.markodevcic.newsreader.api.NewsApi
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.storage.Repository
import com.markodevcic.newsreader.util.SchedulerProvider
import rx.Observable
import java.util.*
import javax.inject.Provider

class SyncServiceImpl(private val newsApi: NewsApi,
					  private val sourcesRepository: Provider<Repository<Source>>,
					  private val articlesRepository: Provider<Repository<Article>>,
					  private val schedulerProvider: SchedulerProvider) : SyncService {

	override fun downloadSources(categories: Collection<String>): Observable<Unit> {
		val repo = sourcesRepository.get()
		return repo.deleteAll()
				.flatMap { Observable.from(categories) }
				.observeOn(schedulerProvider.io)
				.flatMap { c -> newsApi.getSources(c) }
				.observeOn(schedulerProvider.ui)
				.flatMap { s -> repo.addAll(s.sources) }
				.doOnTerminate { repo.close() }
	}

	override fun downloadArticles(source: Source): Observable<Int> {
		val repo = articlesRepository.get()
		return Observable.just(source.id)
				.observeOn(schedulerProvider.io)
				.flatMap { id -> newsApi.getArticles(id) }
				.observeOn(schedulerProvider.ui)
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
				.doOnTerminate { repo.close() }
	}
}