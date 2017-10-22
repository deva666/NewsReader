package com.markodevcic.newsreader.injection

import android.app.Activity
import android.content.Context
import java.lang.IllegalArgumentException

object Injector {
	private lateinit var context: Context
	lateinit var appComponent: AppComponent

	fun init(context: Context) {
		if (context is Activity) {
			throw IllegalArgumentException("pass an Application as an argument to avoid memory leaks")
		}

		this.context = context
		appComponent = DaggerAppComponent.builder()
				.appModule(AppModule(context))
				.build()
	}
}