package com.markodevcic.newsreader.startup

import android.content.SharedPreferences
import com.markodevcic.newsreader.Presenter
import com.markodevcic.newsreader.categories.BaseCategoriesPresenter
import com.markodevcic.newsreader.data.CATEGORIES_TO_RES_MAP
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.storage.Repository
import com.markodevcic.newsreader.sync.SyncService
import com.markodevcic.newsreader.util.KEY_CATEGORIES
import rx.Observable
import javax.inject.Inject

class StartupPresenter @Inject constructor(private val syncService: SyncService,
										   private val sharedPreferences: SharedPreferences,
										   private val sourcesRepository: Repository<Source>) : BaseCategoriesPresenter(sharedPreferences), Presenter<StartupView> {

	protected override lateinit var view: StartupView

	override fun bind(view: StartupView) {
		this.view = view
	}

	fun downloadSourcesAsync(): Observable<Unit> {
		val categorySet = sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())
//		if (categorySet.isEmpty()) {
//			view.showNoCategorySelected()
//		} else {
			if (sourcesRepository.count() == 0L) {
				return syncService.downloadSources(CATEGORIES_TO_RES_MAP.keys)
			}
//		}
		return Observable.just(Unit)
	}

	val canOpenMainView: Boolean
		get() = sourcesRepository.count() > 0
}