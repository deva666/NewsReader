package com.markodevcic.newsreader.sync

import com.markodevcic.newsreader.data.Source
import rx.Observable
import java.io.Closeable

interface SyncUseCase : Closeable{
	fun downloadSources(categories: Collection<String>): Observable<Unit>
	fun downloadArticles(source: Source): Observable<Int>
}
