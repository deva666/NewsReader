package com.markodevcic.newsreader.categories

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ToggleButton
import com.markodevcic.newsreader.R
import com.markodevcic.newsreader.util.CATEGORIES_TO_RES_MAP
import com.markodevcic.newsreader.extensions.showToast

abstract class BaseCategoriesActivity : AppCompatActivity(), BaseCategoriesView {

	protected abstract val categoriesViewGroup: ViewGroup

	protected abstract val presenter: BaseCategoriesPresenter

	protected fun fillCategories() {
		for ((key, resId) in CATEGORIES_TO_RES_MAP) {
			val checkBox = LayoutInflater.from(this).inflate(R.layout.item_category, categoriesViewGroup, false) as ToggleButton
			checkBox.tag = key
			checkBox.setText(resId)
			checkBox.textOff = getString(resId)
			checkBox.textOn = getString(resId)
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
		showToast(getString(R.string.choose_one_category))
	}

	override fun onCategorySelected(categorySet: Set<String>) {
		(0 until categoriesViewGroup.childCount)
				.map { categoriesViewGroup.getChildAt(it) }
				.filterIsInstance<ToggleButton>()
				.filter { categorySet.contains(it.tag) }
				.forEach { it.isChecked = true }
	}
}