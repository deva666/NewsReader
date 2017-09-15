package com.markodevcic.newsreader

import android.app.Application
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.injection.Injector
import com.squareup.picasso.Picasso
import io.realm.Realm
import java.util.*

class NewsReaderApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		Realm.init(this)
		Injector.init(this)
		val picasso = Picasso.Builder(this)
				.build()
		Picasso.setSingletonInstance(picasso)


		val threeDaysAgo = Date().time - 3 * 24 * 60 * 60 * 1000
		Realm.getDefaultInstance().executeTransaction { realm ->
			realm.where(Article::class.java)
					.lessThan("publishedAt", threeDaysAgo)
					.or()
					.equalTo("publishedAt", 0)
					.findAll()
					.deleteAllFromRealm()
		}
	}
}