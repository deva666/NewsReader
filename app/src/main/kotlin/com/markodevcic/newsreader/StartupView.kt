package com.markodevcic.newsreader

interface StartupView : BaseView{
	fun showNoCategorySelected()
	fun finishView()
	fun onCategorySelected(categorySet: Set<String>)
}