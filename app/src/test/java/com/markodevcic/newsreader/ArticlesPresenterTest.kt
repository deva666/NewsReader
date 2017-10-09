package com.markodevcic.newsreader

import android.content.SharedPreferences
import com.markodevcic.newsreader.articles.ArticlesPresenter
import com.markodevcic.newsreader.articles.ArticlesUseCase
import com.markodevcic.newsreader.articles.ArticlesView
import com.markodevcic.newsreader.sync.SyncUseCase
import com.markodevcic.newsreader.util.KEY_CATEGORIES
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class ArticlesPresenterTest {

	@Mock
	private lateinit var articlesUseCase: ArticlesUseCase

	@Mock
	private lateinit var sharedPreferences: SharedPreferences

	@Mock
	private lateinit var syncUseCase: SyncUseCase

	@Mock
	private lateinit var view: ArticlesView

	@InjectMocks
	private lateinit var sut: ArticlesPresenter

	@Before
	fun setup() {
		MockitoAnnotations.initMocks(this)
		sut.bind(view)
	}

	@Test
	fun testOnStartChecksUnreadCount() {
		Mockito.`when`(sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())).thenReturn(setOf())
		val unreads = mapOf("gaming" to 20L)
		Mockito.`when`(articlesUseCase.getUnreadCount(setOf())).thenReturn(unreads)
		Mockito.`when`(articlesUseCase.hasArticles()).thenReturn(true)
		runBlocking {
			sut.onStart()
		}
		Mockito.verify(view).onUnreadCountChanged(unreads)
		Mockito.verify(view, Mockito.never()).onNoArticlesAvailable()
	}

	@Test
	fun testOnStartCallsNoArticles() {
		Mockito.`when`(sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())).thenReturn(setOf())
		val unreads = mapOf("gaming" to 20L)
		Mockito.`when`(articlesUseCase.getUnreadCount(setOf())).thenReturn(unreads)
		Mockito.`when`(articlesUseCase.hasArticles()).thenReturn(false)
		runBlocking {
			sut.onStart()
		}
		Mockito.verify(view).onUnreadCountChanged(unreads)
		Mockito.verify(view).onNoArticlesAvailable()
	}

	@Test
	fun testOnReadCallsUnreadCount() {
		Mockito.`when`(sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())).thenReturn(setOf())
		val unreads = mapOf("gaming" to 20L)
		Mockito.`when`(articlesUseCase.getUnreadCount(setOf())).thenReturn(unreads)
		sut.markArticlesRead("url")
		Mockito.verify(view).onUnreadCountChanged(unreads)
	}
}