package com.markodevcic.newsreader

import android.app.Application
import com.markodevcic.newsreader.injection.Injector
import com.squareup.picasso.Picasso
import io.realm.Realm

class NewsReaderApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		Realm.init(this)
		Injector.init(this)
		val picasso = Picasso.Builder(this)
				.build()
		Picasso.setSingletonInstance(picasso)
	}
}