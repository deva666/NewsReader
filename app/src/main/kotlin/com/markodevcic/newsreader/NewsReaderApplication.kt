package com.markodevcic.newsreader

import android.app.Application
import com.markodevcic.newsreader.injection.Injector
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmConfiguration



class NewsReaderApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		Realm.init(this)
		val config = RealmConfiguration.Builder()
				.name("newsReader.realm")
				.deleteRealmIfMigrationNeeded()
				.build()
		Realm.setDefaultConfiguration(config)
		Injector.init(this)
		val picasso = Picasso.Builder(this)
				.build()
		Picasso.setSingletonInstance(picasso)
	}
}