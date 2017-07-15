package com.markodevcic.newsreader.articles

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.markodevcic.newsreader.R
import com.markodevcic.newsreader.articledetails.ArticleDetailsActivity
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.CATEGORIES_TO_RES_MAP
import com.markodevcic.newsreader.injection.Injector
import com.markodevcic.newsreader.util.KEY_CATEGORIES
import com.squareup.picasso.Picasso
import io.realm.OrderedRealmCollection
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_article_details.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject


class ArticlesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ArticlesView {

	@Inject
	lateinit var presenter: ArticlesPresenter

	@Inject
	lateinit var sharedPrefs: SharedPreferences

	private var adapter: ArticlesAdapter? = null

	private val job = Job()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		Injector.appComponent.inject(this)
		presenter.bind(this)

		setContentView(R.layout.activity_main)
		setSupportActionBar(toolbar)

		articlesView.setHasFixedSize(true)
		articlesView.setItemViewCacheSize(20)
		articlesView.isDrawingCacheEnabled = true
		articlesView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
		articlesView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
		articlesView.layoutManager = LinearLayoutManager(this@ArticlesActivity)
		articlesView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrollStateChanged(recyclerView: RecyclerView?, scrollState: Int) {
				super.onScrollStateChanged(recyclerView, scrollState)
				val picasso = Picasso.with(this@ArticlesActivity.applicationContext)
				if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
					picasso.resumeTag(ArticlesViewHolder.TAG)
				} else {
					picasso.pauseTag(ArticlesViewHolder.TAG)
				}
			}
		})
		val fab = findViewById(R.id.fab) as FloatingActionButton
		fab.setOnClickListener { view ->

		}

		val toggle = ActionBarDrawerToggle(
				this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawerLayout.addDrawerListener(toggle)
		toggle.syncState()

		val menu = navigationView.menu
		menu.findItem(R.id.nav_unread).isChecked = true
		val selectedCategories = sharedPrefs.getStringSet(KEY_CATEGORIES, null)
		selectedCategories?.forEach { cat ->
			menu.add(R.id.groupCategories, cat.hashCode(), Menu.NONE,
					getString(CATEGORIES_TO_RES_MAP[cat] ?: throw IllegalStateException("unkown category"))).apply {
				icon = getDrawable(R.drawable.ic_category)
				setActionView(R.layout.menu_counter)
				isCheckable = true
				actionView.tag = cat
			}
		}

		navigationView.setNavigationItemSelectedListener(this)
		loadArticles(null)
		syncUnreadCount()
	}

	private fun loadArticles(category: String?) {
		launch(UI + job) {
			val articles = if (category == null) presenter.getAllArticles()
			else presenter.getArticlesInCategory(category)
			if (adapter == null) {
				adapter = ArticlesAdapter(articles as OrderedRealmCollection<Article>)
				articlesView.adapter = adapter
			} else {
				adapter?.onDataChanged(articles as RealmResults<Article>)
			}
		}
	}

	private fun syncUnreadCount() {
		val menu = navigationView.menu
		val unreadCount = presenter.syncUnreadCount()
		var totalUnread = 0L
		for ((k, v) in unreadCount) {
			val textCount = menu.findItem(k.hashCode()).actionView as TextView
			textCount.text = "$v"
			totalUnread += v
		}
		val actionView = menu.findItem(R.id.nav_unread).actionView as TextView
		actionView.text = "$totalUnread"
	}

	override fun onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
			drawerLayout.closeDrawer(GravityCompat.START)
		} else {
			super.onBackPressed()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		val id = item.itemId


		if (id == R.id.action_settings) {
			return true
		}

		return super.onOptionsItemSelected(item)
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean {

		val id = item.itemId
		if (id == R.id.nav_unread) {
			loadArticles(null)
		} else {
			val category = item.actionView.tag.toString()
			supportActionBar?.title = getString(CATEGORIES_TO_RES_MAP[category] ?: 0)
			loadArticles(category)
		}
		drawerLayout.closeDrawer(GravityCompat.START)

		return true
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == REQUEST_ARTICLE_READ && resultCode == Activity.RESULT_OK) {
			launch(UI + job) {
				presenter.onArticleRead(data?.getStringExtra(ArticleDetailsActivity.KEY_ARTICLE_URL) ?: "")
				syncUnreadCount()
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		presenter.close()
		job.cancel()
		articlesView.clearOnScrollListeners()
	}

	companion object {
		const val REQUEST_ARTICLE_READ = 1231
	}
}
