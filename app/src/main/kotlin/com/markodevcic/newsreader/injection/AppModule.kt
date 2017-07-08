package com.markodevcic.newsreader.injection

import android.content.Context
import com.markodevcic.newsreader.data.SHARED_PREFS
import dagger.Module
import dagger.Provides

@Module
class AppModule (private val context: Context){

	@Provides
	fun providesSharedPrefs() = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
}