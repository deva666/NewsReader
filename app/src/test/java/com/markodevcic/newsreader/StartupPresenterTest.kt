package com.markodevcic.newsreader

import android.content.SharedPreferences
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.startup.StartupPresenter
import com.markodevcic.newsreader.startup.StartupView
import com.markodevcic.newsreader.storage.Repository
import com.markodevcic.newsreader.sync.SyncUseCase
import com.markodevcic.newsreader.util.KEY_CATEGORIES
import com.markodevcic.newsreader.util.SchedulerProvider
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import rx.Observable
import rx.schedulers.Schedulers

class StartupPresenterTest {

	@Mock
	private lateinit var syncService: SyncUseCase

	@Mock
	private lateinit var sharedPreferences: SharedPreferences

	@Mock
	private lateinit var editor: SharedPreferences.Editor

	@Mock
	private lateinit var startupView: StartupView

	@Mock
	private lateinit var sourcesRepository: Repository<Source>

	@Mock
	private lateinit var schedulerProvider: SchedulerProvider

	@InjectMocks
	private lateinit var sut: StartupPresenter

	@Before
	fun setup() {
		MockitoAnnotations.initMocks(this)
		Mockito.`when`(sharedPreferences.edit()).thenReturn(editor)
		Mockito.`when`(schedulerProvider.io).thenReturn(Schedulers.immediate())
		Mockito.`when`(schedulerProvider.ui).thenReturn(Schedulers.immediate())
	}

	@Test
	fun testCategorySaving() {
		sut.onCategoryChanging("entertainment", true)
		Mockito.verify(editor)?.putStringSet(Mockito.eq<String>(KEY_CATEGORIES), Mockito.anySet<String>())
	}

	@Test
	fun testNoCategorySelected() {
		Mockito.`when`(sharedPreferences.getStringSet(KEY_CATEGORIES, setOf<String>())).thenReturn(setOf("set"))
		sut.bind(startupView)
		sut.onCategoryChanging("", false)
		Mockito.verify(startupView).showNoCategorySelected()
	}

	@Test
	fun testDownloadSources() {
		val categories = setOf("entertainment")
		Mockito.`when`(sharedPreferences.getStringSet(KEY_CATEGORIES, setOf<String>())).thenReturn(categories)
		Mockito.`when`(syncService.downloadSources(Mockito.anyCollection())).thenReturn(Observable.just(Unit))
		sut.bind(startupView)
		sut.downloadSources()
		Mockito.verify(startupView).startMainView()
	}
}
