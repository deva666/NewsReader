package com.markodevcic.newsreader.categories

import android.content.SharedPreferences
import com.markodevcic.newsreader.extensions.apply
import com.markodevcic.newsreader.util.KEY_CATEGORIES

abstract class BaseCategoriesPresenter(private val sharedPreferences: SharedPreferences) {

	protected abstract val view: BaseCategoriesView

	fun onCategoryChanging(tag: String, enabled: Boolean): Boolean {
		val categorySet = sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())
		if (enabled) {
			sharedPreferences.apply {
				putStringSet(KEY_CATEGORIES, categorySet + tag)
			}
		} else {
			if (categorySet.size == 1) {
				view.showNoCategorySelected()
				return false
			}
			sharedPreferences.apply {
				putStringSet(KEY_CATEGORIES, categorySet - tag)
			}
		}
		return true
	}

	fun onStartCategorySelect() {
		val categorySet = sharedPreferences.getStringSet(KEY_CATEGORIES, null)
		categorySet?.let { c -> view.onCategorySelected(c) }
	}

}
