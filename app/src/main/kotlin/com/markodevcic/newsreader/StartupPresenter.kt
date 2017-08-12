package com.markodevcic.newsreader

import android.content.SharedPreferences
import com.markodevcic.newsreader.data.CATEGORIES_TO_RES_MAP
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.extensions.editorApply
import com.markodevcic.newsreader.storage.Repository
import com.markodevcic.newsreader.sync.SyncService
import com.markodevcic.newsreader.util.KEY_CATEGORIES
import javax.inject.Inject

class StartupPresenter @Inject constructor(private val syncService: SyncService,
										   private val sharedPreferences: SharedPreferences,
										   private val sourcesRepository: Repository<Source>) : Presenter<StartupView> {

	private lateinit var view: StartupView

	override fun bind(view: StartupView) {
		this.view = view
	}

	fun onStartCategorySelect() {
		val categorySet = sharedPreferences.getStringSet(KEY_CATEGORIES, null)
		categorySet?.let { c -> view.onCategorySelected(c) }
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
			if (sourcesRepository.count { } == 0L) {
				syncService.downloadSourcesAsync(CATEGORIES_TO_RES_MAP.keys)
			}
			view.startMainView()
		}
	}

	val canOpenMainView = sourcesRepository.count { } > 0
}