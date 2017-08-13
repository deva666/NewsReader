package com.markodevcic.newsreader.categories

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import com.markodevcic.newsreader.R
import com.markodevcic.newsreader.data.CATEGORIES_TO_RES_MAP
import com.markodevcic.newsreader.extensions.showToast

abstract class BaseCategoriesActivity : AppCompatActivity(), BaseCategoriesView {

	protected abstract val categoriesViewGroup: ViewGroup

	protected abstract val presenter: BaseCategoriesPresenter

	protected fun fillCategories() {
		for ((key, resId) in CATEGORIES_TO_RES_MAP) {
			val checkBox = LayoutInflater.from(this).inflate(R.layout.item_category, categoriesViewGroup, false) as CheckBox
			checkBox.tag = key
			checkBox.setText(resId)
			checkBox.typeface = Typeface.SERIF
			checkBox.setOnCheckedChangeListener { box, checked ->
				if (!presenter.onCategoryChanging(box.tag.toString(), checked)) {
					box.isChecked = true
				}
			}
			categoriesViewGroup.addView(checkBox)
		}
	}

	override fun showNoCategorySelected() {
		showToast("Please choose at least one category")
	}

	override fun onCategorySelected(categorySet: Set<String>) {
		(0..categoriesViewGroup.childCount - 1)
				.map { categoriesViewGroup.getChildAt(it) }
				.filterIsInstance<CheckBox>()
				.filter { categorySet.contains(it.tag) }
				.forEach { it.isChecked = true }
	}
}