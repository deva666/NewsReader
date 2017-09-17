package com.markodevcic.newsreader.articles

import com.markodevcic.newsreader.BaseView
import com.markodevcic.newsreader.data.Article

interface ArticlesView : BaseView {
	fun onUnreadCountChanged(counts: Map<String, Long>)

	fun onNoArticlesAvailable()

	fun onArticlesDownloaded(count: Int)

	fun onCategoriesUpdated()

	fun onArticlesUpdated(articles: List<Article>)

	fun onSyncFailed()
}