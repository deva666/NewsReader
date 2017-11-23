package com.markodevcic.newsreader.startup

import android.content.SharedPreferences
import com.markodevcic.newsreader.Presenter
import com.markodevcic.newsreader.categories.BaseCategoriesPresenter
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.storage.Repository
import com.markodevcic.newsreader.sync.SyncUseCase
import com.markodevcic.newsreader.util.CATEGORIES_TO_RES_MAP
import com.markodevcic.newsreader.util.SchedulerProvider
import javax.inject.Inject

class StartupPresenter @Inject constructor(private val syncUseCase: SyncUseCase,
										   private val sharedPreferences: SharedPreferences,
										   private val sourcesRepository: Repository<Source>,
										   private val schedulerProvider: SchedulerProvider) : BaseCategoriesPresenter(sharedPreferences), Presenter<StartupView> {

	protected override lateinit var view: StartupView

	override fun bind(view: StartupView) {
		this.view = view
	}

	fun downloadSources() {
		syncUseCase.downloadSources(CATEGORIES_TO_RES_MAP.keys)
				.observeOn(schedulerProvider.ui)
				.subscribe({}, { fail ->
					view.onError(fail)
				}, {
					view.startMainView()
				})
	}

	val canOpenMainView: Boolean
		get() = sourcesRepository.count() > 0

	fun close() {
		syncUseCase.close()
	}
}