package com.markodevcic.newsreader

import android.content.SharedPreferences
import com.markodevcic.newsreader.api.NewsApi
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.extensions.awaitAll
import com.markodevcic.newsreader.extensions.editorApply
import com.markodevcic.newsreader.extensions.launchAsync
import com.markodevcic.newsreader.storage.Repository
import com.markodevcic.newsreader.sync.SyncService
import com.markodevcic.newsreader.util.KEY_CATEGORIES
import javax.inject.Inject

class StartupPresenter @Inject constructor(private val syncService: SyncService,
										   private val sharedPreferences: SharedPreferences,
										   private val sourcesRepository: Repository<Source>,
										   private val newsApi: NewsApi) : Presenter<StartupView> {

	private lateinit var view: StartupView

	override fun bind(view: StartupView) {
		this.view = view
	}

	fun onCategoryChanging(tag: String, enabled: Boolean) {
		val categorySet = sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())
		if (enabled) {
			sharedPreferences.editorApply {
				putStringSet(KEY_CATEGORIES, categorySet + tag)
			}
		} else {
			sharedPreferences.editorApply {
				putStringSet(KEY_CATEGORIES, categorySet - tag)
			}
		}
	}

	suspend fun downloadAllArticlesAsync() {
		val categorySet = sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())
		if (categorySet.isEmpty()) {
			view.showNoCategorySelected()
			return
		} else {
			val sources = downloadSourcesAsync(categorySet)
//			sources.forEach { src ->
//				syncService.downloadArticlesAsync(src)
//			}
			view.startMainView()
		}
	}

	private suspend fun downloadSourcesAsync(categories: Collection<String>): Collection<Source> {
		sourcesRepository.deleteAll()
		val downloadJobs = categories.map { cat -> newsApi.getSources(cat).launchAsync() }
		val sources = downloadJobs.awaitAll().flatMap { job -> job.sources }
		sourcesRepository.addAll(sources)
		return sources
	}

	val canOpenMainView = sharedPreferences.contains(KEY_CATEGORIES)
}