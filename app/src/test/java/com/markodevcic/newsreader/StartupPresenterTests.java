package com.markodevcic.newsreader;

import android.content.SharedPreferences;

import com.markodevcic.newsreader.startup.StartupPresenter;
import com.markodevcic.newsreader.sync.SyncServiceImpl;
import com.markodevcic.newsreader.util.Keys;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class StartupPresenterTests {

	@Mock
	private SyncServiceImpl syncService;
	
	@Mock
	private SharedPreferences sharedPreferences;
	
	@Mock
	private SharedPreferences.Editor editor;
	
	@Mock
	private StartupView startupView;
	
	@InjectMocks
	private StartupPresenter sut;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(sharedPreferences.edit()).thenReturn(editor);
	}
	
	@Test
	public void testCategorySaving() {
		sut.onCategoryChanging("entertainment", true);
		Mockito.verify(editor).putStringSet(Mockito.eq(Keys.KEY_CATEGORIES), Mockito.<String>anySet());
	}
}
