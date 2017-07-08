package com.markodevcic.newsreader

import android.app.Application
import com.markodevcic.newsreader.injection.Injector
import io.realm.Realm

class NewsReaderApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		Realm.init(this)
		Injector.init(this)
	}
}