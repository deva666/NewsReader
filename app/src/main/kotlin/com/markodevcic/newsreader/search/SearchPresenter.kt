package com.markodevcic.newsreader.search

import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.sync.SyncUseCase
import javax.inject.Inject

class SearchPresenter @Inject constructor(private val syncUseCase: SyncUseCase) {

	suspend fun search(query: String): List<Article> = syncUseCase.search(query)
}