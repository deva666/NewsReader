package com.markodevcic.newsreader

import android.content.SharedPreferences
import com.markodevcic.newsreader.articles.ArticlesPresenter
import com.markodevcic.newsreader.articles.ArticlesUseCase
import com.markodevcic.newsreader.articles.ArticlesView
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

class ArticlesPresenterTest {

	@Mock
	private lateinit var articlesUseCase: ArticlesUseCase

	@Mock
	private lateinit var sharedPreferences: SharedPreferences

	@Mock
	private lateinit var syncUseCase: SyncUseCase

	@Mock
	private lateinit var view: ArticlesView

	@Mock
	private lateinit var schedulerProvider: SchedulerProvider

	@InjectMocks
	private lateinit var sut: ArticlesPresenter

	@Before
	fun setup() {
		MockitoAnnotations.initMocks(this)
		Mockito.`when`(schedulerProvider.io).thenReturn(Schedulers.immediate())
		Mockito.`when`(schedulerProvider.ui).thenReturn(Schedulers.immediate())
		sut.bind(view)
	}

	@Test
	fun testOnStartChecksUnreadCount() {
		Mockito.`when`(sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())).thenReturn(setOf())
		val unreads = mapOf("gaming" to 20L)
		Mockito.`when`(articlesUseCase.getUnreadCount(setOf())).thenReturn(unreads)
		Mockito.`when`(articlesUseCase.hasArticles()).thenReturn(true)
		Mockito.`when`(articlesUseCase.deleteOldArticles(Mockito.anyInt())).thenReturn(Observable.just(Unit))
		sut.onStart()
		Mockito.verify(view).onUnreadCountChanged(unreads)
		Mockito.verify(view, Mockito.never()).onNoArticlesAvailable()
	}

	@Test
	fun testOnStartCallsNoArticles() {
		Mockito.`when`(sharedPreferences.getStringSet(KEY_CATEGORIES, setOf())).thenReturn(setOf())
		val unreads = mapOf("gaming" to 20L)
		Mockito.`when`(articlesUseCase.getUnreadCount(setOf())).thenReturn(unreads)
		Mockito.`when`(articlesUseCase.hasArticles()).thenReturn(false)
		Mockito.`when`(articlesUseCase.deleteOldArticles(Mockito.anyInt())).thenReturn(Observable.just(Unit))
		sut.onStart()
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