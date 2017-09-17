package com.markodevcic.newsreader.sync

import com.markodevcic.newsreader.api.NewsApi
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.storage.Repository
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import javax.inject.Provider

class SyncServiceImpl(private val newsApi: NewsApi,
					  private val sourcesRepository: Provider<Repository<Source>>,
					  private val articlesRepository: Provider<Repository<Article>>) : SyncService {

	override fun downloadSources(categories: Collection<String>): Observable<Unit> {
		val repo = sourcesRepository.get()
		return repo.deleteAll()
				.flatMap { Observable.from(categories) }
				.flatMap { c -> newsApi.getSources(c) }
				.flatMap { s -> repo.addAll(s.sources) }
				.doOnTerminate { repo.close() }
	}

	override fun downloadArticles(source: Source): Observable<Int> {
		val repo = articlesRepository.get()
		return Observable.just(source.id)
				.observeOn(Schedulers.io())
				.flatMap { id -> newsApi.getArticles(id) }
				.observeOn(AndroidSchedulers.mainThread())
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