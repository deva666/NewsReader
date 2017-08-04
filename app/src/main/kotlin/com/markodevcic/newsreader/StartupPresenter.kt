package com.markodevcic.newsreader

import android.content.SharedPreferences
import com.markodevcic.newsreader.extensions.editorApply
import com.markodevcic.newsreader.sync.SyncService
import com.markodevcic.newsreader.util.KEY_CATEGORIES
import javax.inject.Inject

class StartupPresenter @Inject constructor(private val syncService: SyncService,
										   private val sharedPreferences: SharedPreferences) : Presenter<StartupView> {

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
			syncService.downloadSourcesAsync(categorySet)
			view.startMainView()
		}
	}

	val canOpenMainView = sharedPreferences.contains(KEY_CATEGORIES)
}