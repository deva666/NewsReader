package com.markodevcic.newsreader.startup

import com.markodevcic.newsreader.categories.BaseCategoriesView

interface StartupView : BaseCategoriesView {
	fun startMainView()
	fun onError(fail: Throwable)
}