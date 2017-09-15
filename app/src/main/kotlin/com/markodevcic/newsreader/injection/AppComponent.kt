package com.markodevcic.newsreader.injection

import com.markodevcic.newsreader.articles.ArticlesActivity
import com.markodevcic.newsreader.categories.SelectCategoriesActivity
import com.markodevcic.newsreader.settings.SettingsActivity
import com.markodevcic.newsreader.startup.StartupActivity
import dagger.Component

@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

	fun inject(startupActivity: StartupActivity)
	fun inject(articlesActivity: ArticlesActivity)
	fun inject(selectCategoriesActivity: SelectCategoriesActivity)
	fun inject(settingsActivity: SettingsActivity)
}