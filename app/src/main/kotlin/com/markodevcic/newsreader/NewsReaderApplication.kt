package com.markodevcic.newsreader

import android.app.Application
import io.realm.Realm

class NewsReaderApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		Realm.init(this)
	}
}