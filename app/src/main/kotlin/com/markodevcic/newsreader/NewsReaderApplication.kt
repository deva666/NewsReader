package com.markodevcic.newsreader

import android.app.Application
import com.markodevcic.newsreader.injection.Injector
import com.squareup.picasso.Cache
import com.squareup.picasso.Picasso
import io.realm.Realm
import java.util.concurrent.Executors

class NewsReaderApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		Realm.init(this)
		Injector.init(this)
		val picasso = Picasso.Builder(this)
				.executor(Executors.newSingleThreadExecutor())
				.memoryCache(Cache.NONE)
				.build()
		Picasso.setSingletonInstance(picasso)
	}
}