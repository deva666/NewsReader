package com.markodevcic.newsreader.categories

import android.content.SharedPreferences
import com.markodevcic.newsreader.Presenter
import javax.inject.Inject

class SelectCategoriesPresenter @Inject constructor (sharedPreferences: SharedPreferences) : BaseCategoriesPresenter(sharedPreferences), Presenter<SelectCategoriesView> {

	override lateinit var view: SelectCategoriesView

	override fun bind(view: SelectCategoriesView) {
		this.view = view
	}
}
