package com.markodevcic.newsreader.articles

import com.markodevcic.newsreader.Presenter
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.storage.Repository
import javax.inject.Inject

class ArticlesPresenter @Inject constructor(private val articlesRepository: Repository<Article>) : Presenter<ArticlesView> {

	private lateinit var view: ArticlesView

	override fun bind(view: ArticlesView) {
		this.view = view
	}

	suspend fun onArticleRead(articleUrl: String) {
		articlesRepository.update(articleUrl) {
			isUnread = false
		}
	}
}