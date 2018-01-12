package com.markodevcic.newsreader.sync

import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.Source

interface SyncUseCase {
	suspend fun downloadSourcesAsync(categories: Collection<String>)
	suspend fun downloadArticlesAsync(source: Source): Int
	suspend fun search(query: String): List<Article>
}
