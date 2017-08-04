package com.markodevcic.newsreader

interface StartupView : BaseView{
	fun showNoCategorySelected()
	fun startMainView()
	fun onCategorySelected(categorySet: Set<String>)
}