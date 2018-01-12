package com.markodevcic.newsreader.search

import com.markodevcic.newsreader.BaseView
import com.markodevcic.newsreader.data.Article

interface SearchView : BaseView {
	fun onSearchFailed()
	fun onSearchResults(articles: List<Article>)
	fun onNoSearchResults()
}