package com.markodevcic.newsreader.categories

import com.markodevcic.newsreader.BaseView

interface BaseCategoriesView : BaseView {
	fun onCategorySelected(categorySet: Set<String>)
	fun showNoCategorySelected()
}