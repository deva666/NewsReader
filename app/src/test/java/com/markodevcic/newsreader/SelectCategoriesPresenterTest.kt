package com.markodevcic.newsreader

import android.content.SharedPreferences
import com.markodevcic.newsreader.categories.SelectCategoriesPresenter
import com.markodevcic.newsreader.categories.SelectCategoriesView
import com.markodevcic.newsreader.util.KEY_CATEGORIES
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class SelectCategoriesPresenterTest {

	@Mock
	private lateinit var sharedPrefs: SharedPreferences

	@Mock
	private lateinit var editor: SharedPreferences.Editor

	@Mock
	private lateinit var selectCategoriesView: SelectCategoriesView

	@InjectMocks
	private lateinit var sut: SelectCategoriesPresenter

	@Before
	fun setup() {
		MockitoAnnotations.initMocks(this)
		Mockito.`when`(sharedPrefs.edit()).thenReturn(editor)
		sut.bind(selectCategoriesView)
	}

	@Test
	fun testCategoryAdd() {
		Mockito.`when`(sharedPrefs.getStringSet(KEY_CATEGORIES, setOf())).thenReturn(setOf())
		sut.onCategoryChanging("music", true)
		Mockito.verify(editor).putStringSet(KEY_CATEGORIES, setOf("music"))
	}

	@Test
	fun testCategoryRemoval() {
		Mockito.`when`(sharedPrefs.getStringSet(KEY_CATEGORIES, setOf())).thenReturn(setOf("general", "music"))
		sut.onCategoryChanging("general", false)
		Mockito.verify(editor).putStringSet(KEY_CATEGORIES, setOf("music"))
	}
}