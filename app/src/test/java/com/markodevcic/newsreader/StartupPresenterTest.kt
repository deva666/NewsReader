package com.markodevcic.newsreader

import android.content.SharedPreferences
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.startup.StartupPresenter
import com.markodevcic.newsreader.startup.StartupView
import com.markodevcic.newsreader.storage.Repository
import com.markodevcic.newsreader.sync.SyncUseCase
import com.markodevcic.newsreader.util.KEY_CATEGORIES
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class StartupPresenterTest {

	@Mock
	private lateinit var syncUseCase: SyncUseCase

	@Mock
	private lateinit var sharedPreferences: SharedPreferences

	@Mock
	private lateinit var editor: SharedPreferences.Editor

	@Mock
	private lateinit var startupView: StartupView

	@Mock
	private lateinit var sourcesRepository: Repository<Source>

	@InjectMocks
	private lateinit var sut: StartupPresenter

	@Before
	fun setup() {
		MockitoAnnotations.initMocks(this)
		Mockito.`when`(sharedPreferences.edit()).thenReturn(editor)
	}

	@Test
	fun testCategorySaving() {
		sut.onCategoryChanging("entertainment", true)
		Mockito.verify(editor)?.putStringSet(Mockito.eq<String>(KEY_CATEGORIES), Mockito.anySet<String>())
	}

	@Test
	fun testNoCategorySelected() {
		Mockito.`when`(sharedPreferences.getStringSet(KEY_CATEGORIES, setOf<String>())).thenReturn(setOf())
		sut.bind(startupView)
		runBlocking {
			sut.downloadSourcesAsync()
		}
		Mockito.verify(startupView).showNoCategorySelected()
	}
}
