package com.markodevcic.newsreader.sync

import com.markodevcic.newsreader.api.NewsApi
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.storage.Repository
import com.markodevcic.newsreader.util.SchedulerProvider
import rx.Observable
import java.util.*

class SyncUseCaseImpl(private val newsApi: NewsApi,
					  private val sourcesRepository: Repository<Source>,
					  private val articlesRepository: Repository<Article>,
					  private val schedulerProvider: SchedulerProvider) : SyncUseCase {

	override fun downloadSources(categories: Collection<String>): Observable<Unit> {
		return sourcesRepository.deleteAll()
				.flatMap { Observable.from(categories) }
				.observeOn(schedulerProvider.io)
				.flatMap { c -> newsApi.getSources(c) }
				.observeOn(schedulerProvider.ui)
				.flatMap { s -> sourcesRepository.addAll(s.sources) }
	}

	override fun downloadArticles(source: Source): Observable<Int> {
		return Observable.just(source.id)
				.observeOn(schedulerProvider.io)
				.flatMap { id -> newsApi.getArticles(id) }
				.observeOn(schedulerProvider.ui)
				.doOnNext { r -> r.articles.forEach { article -> article.category = source.category } }
				.flatMap { r -> Observable.from(r.articles) }
				.filter { article -> articlesRepository.getById(article.url) == null }
				.collect({ ArrayList<Article>() }, { r, a -> r.add(a) })
				.flatMap { Observable.zip(Observable.just(it), articlesRepository.addAll(it), { p1, p2 -> Pair(p1, p2) }) }
				.map { (first) -> first.count() }
	}

	override fun close() {
		sourcesRepository.close()
		articlesRepository.close()
	}
}