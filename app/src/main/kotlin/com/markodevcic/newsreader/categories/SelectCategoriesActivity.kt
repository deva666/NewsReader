package com.markodevcic.newsreader.categories

import android.os.Bundle
import android.view.ViewGroup
import com.markodevcic.newsreader.R
import kotlinx.android.synthetic.main.layout_categories.*

class SelectCategoriesActivity: BaseCategoriesActivity() {

	override val categoriesViewGroup: ViewGroup
		get() = categoriesHost

	override val presenter: BaseCategoriesPresenter
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_select_categories)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.title = "Categories"
	}
}