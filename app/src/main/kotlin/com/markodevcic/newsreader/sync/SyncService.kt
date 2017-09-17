package com.markodevcic.newsreader.sync

import com.markodevcic.newsreader.data.Source
import rx.Observable

interface SyncService {
	fun downloadSourcesAsync(categories: Collection<String>): Observable<Unit>
	fun downloadArticlesAsync(source: Source): Observable<Int>
}
