package com.markodevcic.newsreader.sync

import com.markodevcic.newsreader.data.Source

interface SyncService {
	suspend fun downloadSourcesAsync(categories: Collection<String>): Collection<Source>
	suspend fun downloadArticlesAsync(source: Source): Int
}
