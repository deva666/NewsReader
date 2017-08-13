package com.markodevcic.newsreader.categories

import android.content.SharedPreferences
import com.markodevcic.newsreader.Presenter
import com.markodevcic.newsreader.util.KEY_CATEGORIES
import javax.inject.Inject

class SelectCategoriesPresenter @Inject constructor (private val sharedPreferences: SharedPreferences) : BaseCategoriesPresenter(sharedPreferences), Presenter<SelectCategoriesView> {

	override lateinit var view: SelectCategoriesView

	override fun bind(view: SelectCategoriesView) {
		this.view = view
	}

	fun onSaveClicked() {
		val categorySet = sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())
		if (categorySet.isEmpty()) {
			view.showNoCategorySelected()
			return
		} else {
			view.finishOk()
		}
	}
}
