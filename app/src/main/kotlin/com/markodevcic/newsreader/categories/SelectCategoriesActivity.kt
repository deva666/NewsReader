package com.markodevcic.newsreader.categories

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.markodevcic.newsreader.R
import com.markodevcic.newsreader.injection.Injector
import kotlinx.android.synthetic.main.layout_categories.*
import javax.inject.Inject

class SelectCategoriesActivity: BaseCategoriesActivity(), SelectCategoriesView {

	override val categoriesViewGroup: ViewGroup
		get() = categoriesHost

	@Inject
	override lateinit var presenter: SelectCategoriesPresenter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_select_categories)
		Injector.appComponent.inject(this)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		chooseCategoriesText.text = getString(R.string.available_categories)
		presenter.bind(this)
		fillCategories()
		saveCategoriesBtn.visibility = View.GONE
		presenter.onStartCategorySelect()
	}

	override fun finishOk() {
		setResult(Activity.RESULT_OK)
		onBackPressed()
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			finishOk()
			return true
		}
		return super.onOptionsItemSelected(item)
	}
}