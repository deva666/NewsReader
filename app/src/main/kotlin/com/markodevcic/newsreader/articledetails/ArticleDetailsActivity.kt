package com.markodevcic.newsreader.articledetails

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.markodevcic.newsreader.R
import kotlinx.android.synthetic.main.activity_article_details.*
@SuppressLint("SetJavaScriptEnabled")

class ArticleDetailsActivity : AppCompatActivity() {

	private lateinit var articleUrl: String

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_article_details)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		webView.settings.javaScriptEnabled = true
		webView.setWebChromeClient(object : WebChromeClient() {
			override fun onProgressChanged(view: WebView?, newProgress: Int) {
				super.onProgressChanged(view, newProgress)
				if (newProgress == 100) {
					webView.visibility = View.VISIBLE
					progressBar.visibility = View.GONE
					setResult(Activity.RESULT_OK, Intent().apply { putExtra(KEY_ARTICLE_URL, articleUrl) })
				}
				progressBar.progress = newProgress
			}

			override fun onReceivedTitle(view: WebView?, title: String?) {
				super.onReceivedTitle(view, title)
				supportActionBar?.title = title
			}
		})
		webView.setWebViewClient(object : WebViewClient() {

		})
		articleUrl = intent.getStringExtra(KEY_ARTICLE_URL) ?: throw IllegalStateException("article url expected in bundle")
		webView.visibility = View.GONE
		webView.loadUrl(articleUrl)
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.article_details, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			onBackPressed()
			return true
		} else if (item.itemId == R.id.action_share) {
			val intent = Intent(Intent.ACTION_SEND)
			intent.type = "text/plain"
			intent.putExtra(Intent.EXTRA_SUBJECT, "Share Article Link")
			intent.putExtra(Intent.EXTRA_TEXT, articleUrl)
			startActivity(Intent.createChooser(intent, "Share Article Link"))
			return true
		}
		return super.onOptionsItemSelected(item)
	}

	companion object {
		const val KEY_ARTICLE_URL = "KEY_ARTICLE_URL"
	}
}
