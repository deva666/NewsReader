package com.markodevcic.newsreader.articles

import android.content.SharedPreferences
import com.markodevcic.newsreader.Presenter
import com.markodevcic.newsreader.sync.SyncUseCase
import com.markodevcic.newsreader.util.CATEGORIES_TO_RES_MAP
import com.markodevcic.newsreader.util.KEY_CATEGORIES
import com.markodevcic.newsreader.util.KEY_DELETE_DAYS
import com.markodevcic.newsreader.util.SchedulerProvider
import rx.Observable
import rx.subscriptions.CompositeSubscription
import java.io.Closeable
import javax.inject.Inject

class ArticlesPresenter @Inject constructor(private val articlesUseCase: ArticlesUseCase,
											private val sharedPreferences: SharedPreferences,
											private val syncUseCase: SyncUseCase,
											private val schedulerProvider: SchedulerProvider) : Presenter<ArticlesView>, Closeable {

	private lateinit var view: ArticlesView

	private val subscriptions = CompositeSubscription()

	override fun bind(view: ArticlesView) {
		this.view = view
	}

	fun onStart() {
		view.onUnreadCountChanged(getUnreadCount())
		val deleteDays = sharedPreferences.getInt(KEY_DELETE_DAYS, 3)
		subscriptions.add(articlesUseCase.deleteOldArticles(deleteDays)
				.observeOn(schedulerProvider.ui)
				.subscribe {
					if (!articlesUseCase.hasArticles()) {
						view.onNoArticlesAvailable()
					}
				}
		)
	}

	fun onSelectedCategoriesChanged() {
		val selectedCategories = sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())
		val deletedCategories = CATEGORIES_TO_RES_MAP.keys.subtract(selectedCategories)
		subscriptions.add(articlesUseCase.onSelectedCategoriesChanged(deletedCategories)
				.observeOn(schedulerProvider.ui)
				.subscribe { view.onCategoriesUpdated() })
	}

	fun syncCategory(category: String?) {
		val selectedCategories = sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())
		subscriptions.add(articlesUseCase.getSources(category, selectedCategories)
				.flatMap { sources -> Observable.from(sources) }
				.flatMap { s -> syncUseCase.downloadArticles(s) }
				.toList()
				.map { l -> l.sum() }
				.observeOn(schedulerProvider.ui)
				.subscribe({ s ->
					view.onUnreadCountChanged(getUnreadCount())
					view.onArticlesDownloaded(s)
				}, { fail ->
					view.onSyncFailed(fail)
				}))
	}

	fun markArticlesRead(vararg articleUrl: String) {
		articlesUseCase.markArticlesRead(*articleUrl)
		view.onUnreadCountChanged(getUnreadCount())
	}

	fun markArticlesUnread(vararg articleUrl: String) {
		articlesUseCase.markArticlesUnread(*articleUrl)
		view.onUnreadCountChanged(getUnreadCount())
	}

	fun getArticlesInCategory(category: String?) {
		subscriptions.add(articlesUseCase.getArticles(category)
				.observeOn(schedulerProvider.ui)
				.subscribe { articles ->
					view.onArticlesUpdated(articles)
				})
	}

	private fun getUnreadCount(): Map<String, Long> {
		val categories = sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())
		return articlesUseCase.getUnreadCount(categories)
	}

	override fun close() {
		articlesUseCase.close()
		subscriptions.clear()
		syncUseCase.close()
	}
}