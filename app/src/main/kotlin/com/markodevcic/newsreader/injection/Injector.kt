package com.markodevcic.newsreader.injection

import android.content.Context

object Injector {
	private lateinit var context: Context
	lateinit var appComponent: AppComponent


	fun init(context: Context) {
		this.context = context
		appComponent = DaggerAppComponent.builder()
				.appModule(AppModule(context))
				.build()
	}

}