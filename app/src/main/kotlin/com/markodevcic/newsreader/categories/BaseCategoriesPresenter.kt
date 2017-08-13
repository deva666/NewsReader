package com.markodevcic.newsreader.categories

import android.content.SharedPreferences
import com.markodevcic.newsreader.extensions.editorApply
import com.markodevcic.newsreader.util.KEY_CATEGORIES

abstract class BaseCategoriesPresenter (private val sharedPreferences: SharedPreferences)  {

	protected abstract val view: BaseCategoriesView

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

	fun onStartCategorySelect() {
		val categorySet = sharedPreferences.getStringSet(KEY_CATEGORIES, null)
		categorySet?.let { c -> view.onCategorySelected(c) }
	}

}
