package com.markodevcic.newsreader.injection

import android.content.Context
import com.markodevcic.newsreader.api.ApiFactory
import com.markodevcic.newsreader.api.NewsApi
import com.markodevcic.newsreader.util.SHARED_PREFS
import dagger.Module
import dagger.Provides

@Module
class AppModule (private val context: Context){

	@Provides
	fun providesSharedPrefs() = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

	@Provides
	fun providesNewsApi() = ApiFactory.create<NewsApi>()
}