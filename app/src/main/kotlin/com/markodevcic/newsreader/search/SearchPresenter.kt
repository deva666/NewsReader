package com.markodevcic.newsreader.search

import com.markodevcic.newsreader.Presenter
import com.markodevcic.newsreader.sync.SyncUseCase
import javax.inject.Inject

class SearchPresenter @Inject constructor(private val syncUseCase: SyncUseCase) : Presenter<SearchView> {

	lateinit var view: SearchView

	override fun bind(view: SearchView) {
		this.view = view
	}

	suspend fun search(query: String) {
		try {
			val articles = syncUseCase.search(query)
			if (articles.isEmpty()) {
				view.onNoSearchResults()
			} else {
				view.onSearchResults(articles)
			}
		} catch (fail: Exception) {
			view.onSearchFailed()
		}
	}
}