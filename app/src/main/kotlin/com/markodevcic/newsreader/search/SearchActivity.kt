package com.markodevcic.newsreader.search

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.markodevcic.newsreader.R
import com.markodevcic.newsreader.articles.ArticlesViewHolder
import com.markodevcic.newsreader.injection.Injector
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class SearchActivity : AppCompatActivity() {

	private val job = Job()

	@Inject
	lateinit var presenter: SearchPresenter
	private var adapter: SearchAdapter? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_search)

		Injector.appComponent.inject(this)

		setSupportActionBar(toolbar)
		supportActionBar?.setHomeButtonEnabled(true)

		setupArticlesView()

		if (Intent.ACTION_SEARCH == intent.action) {
			val query = intent.getStringExtra(SearchManager.QUERY)
			supportActionBar?.title = query.capitalize()
			launch(UI + job) {
				progressBar.visibility = View.VISIBLE
				val articles = presenter.search(query)
				adapter = SearchAdapter(articles)
				articlesView.adapter = adapter
				progressBar.visibility = View.GONE
			}
		} else {
			throw IllegalStateException("this activity should be called from search view")
		}
	}

	private fun setupArticlesView() {
		articlesView.setHasFixedSize(true)
		articlesView.isDrawingCacheEnabled = true
		articlesView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
		articlesView.layoutManager = LinearLayoutManager(this@SearchActivity)
		articlesView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrollStateChanged(recyclerView: RecyclerView?, scrollState: Int) {
				super.onScrollStateChanged(recyclerView, scrollState)
				val picasso = Picasso.with(this@SearchActivity.applicationContext)
				if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
					picasso.resumeTag(ArticlesViewHolder)
				} else {
					picasso.pauseTag(ArticlesViewHolder)
				}
			}
		})
	}
}
