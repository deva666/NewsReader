package com.markodevcic.newsreader.injection

import com.markodevcic.newsreader.StartupActivity
import dagger.Component

@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

	fun inject(startupActivity: StartupActivity)
}