package com.markodevcic.newsreader.articledetails

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.markodevcic.newsreader.R
import kotlinx.android.synthetic.main.activity_article_details.*

class ArticleDetailsActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_article_details)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		webView.settings.javaScriptEnabled = true
		webView.setWebChromeClient(object: WebChromeClient() {
			override fun onProgressChanged(view: WebView?, newProgress: Int) {
				super.onProgressChanged(view, newProgress)
				if (newProgress == 100) {
					setResult(Activity.RESULT_OK, this@ArticleDetailsActivity.intent)
				}
			}

			override fun onReceivedTitle(view: WebView?, title: String?) {
				super.onReceivedTitle(view, title)
			}
		})
		webView.loadUrl(intent.getStringExtra(KEY_ARTICLE_URL) ?: throw IllegalStateException("article url expected in bundle"))
	}

	companion object {
		const val KEY_ARTICLE_URL = "KEY_ARTICLE_URL"
	}
}
