package com.markodevcic.newsreader.injection

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context

@SuppressLint("StaticFieldLeak")
object Injector {
	private lateinit var context: Context
	lateinit var appComponent: AppComponent

	fun init(context: Context) {
		if (context is Activity) {
			throw IllegalStateException("pass an Application as an argument to avoid memory leaks")
		}

		this.context = context
		appComponent = DaggerAppComponent.builder()
				.appModule(AppModule(context))
				.build()
	}
}