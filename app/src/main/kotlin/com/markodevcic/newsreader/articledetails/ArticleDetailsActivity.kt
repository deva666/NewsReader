package com.markodevcic.newsreader.articledetails

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.markodevcic.newsreader.R
import kotlinx.android.synthetic.main.activity_article_details.*

class ArticleDetailsActivity : AppCompatActivity() {

	private var pageLoaded = false
	private lateinit var articleUrl: String

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_article_details)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		window.setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
		webView.settings.javaScriptEnabled = true
		webView.setWebChromeClient(object : WebChromeClient() {
			override fun onProgressChanged(view: WebView?, newProgress: Int) {
				super.onProgressChanged(view, newProgress)
				if (newProgress == 100) {
					pageLoaded = true
					webView.visibility = View.VISIBLE
					progressBar.visibility = View.GONE
					setResult(Activity.RESULT_OK, Intent().apply { putExtra(KEY_ARTICLE_URL, articleUrl) })
				}
			}

			override fun onReceivedTitle(view: WebView?, title: String?) {
				super.onReceivedTitle(view, title)
				supportActionBar?.title = title
			}
		})
		articleUrl = intent.getStringExtra(KEY_ARTICLE_URL) ?: throw IllegalStateException("article url expected in bundle")
		webView.visibility = View.GONE
		webView.loadUrl(articleUrl)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			onBackPressed()
			return true
		}
		return super.onOptionsItemSelected(item)
	}

	companion object {
		const val KEY_ARTICLE_URL = "KEY_ARTICLE_URL"
	}
}
