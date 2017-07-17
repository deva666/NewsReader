package com.markodevcic.newsreader.articles

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
import android.view.animation.Animation
import android.widget.TextView
import com.markodevcic.newsreader.R
import com.markodevcic.newsreader.articledetails.ArticleDetailsActivity
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.CATEGORIES_TO_RES_MAP
import com.markodevcic.newsreader.injection.Injector
import com.markodevcic.newsreader.util.KEY_CATEGORIES
import com.squareup.picasso.Picasso
import io.realm.OrderedRealmCollection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
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

	private var selectedCategory: String? = null

	private val job = Job()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		Injector.appComponent.inject(this)
		presenter.bind(this)

		setContentView(R.layout.activity_main)
		setSupportActionBar(toolbar)

		setupArticlesView()

		btnMarkAllRead.setOnClickListener {
			val adapterCopy = adapter ?: return@setOnClickListener
			presenter.markItemsReadAsync(adapterCopy.articles.toTypedArray())
			syncUnreadCount()
		}

		val toggle = ActionBarDrawerToggle(
				this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawerLayout.addDrawerListener(toggle)
		toggle.syncState()
		navigationView.setNavigationItemSelectedListener(this)

		val menu = setupMenuItems()
		val selectedId: Int = checkSelectedMenuItem(savedInstanceState)
		val menuItem = menu.findItem(selectedId)
		onNavigationItemSelected(menuItem)

		syncUnreadCount()
	}

	private fun setupArticlesView() {
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
					picasso.resumeTag(ArticlesViewHolder)
				} else {
					picasso.pauseTag(ArticlesViewHolder)
				}
			}
		})
	}

	private fun setupMenuItems(): Menu {
		val menu = navigationView.menu
		val selectedCategories = sharedPrefs.getStringSet(KEY_CATEGORIES, null)
		selectedCategories?.forEach { cat ->
			menu.add(R.id.groupCategories, cat.hashCode(), Menu.NONE,
					getString(CATEGORIES_TO_RES_MAP[cat] ?: throw IllegalStateException("unknown category"))).apply {
				icon = getDrawable(R.drawable.ic_category)
				setActionView(R.layout.menu_counter)
				isCheckable = true
				actionView.tag = cat
			}
		}
		return menu
	}

	private fun checkSelectedMenuItem(savedInstanceState: Bundle?): Int {
		var selectedId: Int = R.id.nav_unread
		if (savedInstanceState?.containsKey(KEY_CATEGORY) ?: false) {
			val cat = savedInstanceState?.getString(KEY_CATEGORY)
			if (cat != null) {
				selectedId = cat.hashCode()
			}
		}
		return selectedId
	}

	private fun syncUnreadCount() {
		val menu = navigationView.menu
		val unreadCount = presenter.syncUnreadCount()
		var totalUnread = 0L
		for ((k, v) in unreadCount) {
			val textCount = menu.findItem(k.hashCode()).actionView as TextView
			textCount.text = if (v > 0) v.toString() else ""
			totalUnread += v
		}
		val actionView = menu.findItem(R.id.nav_unread).actionView as TextView
		actionView.text = if (totalUnread > 0) totalUnread.toString() else ""
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
		return when (id) {
			R.id.action_refresh -> {
				launch(UI + job) {
					val refreshMenu = toolbar.findViewById(R.id.action_refresh)
					val animator = startRotatingAnimation(refreshMenu)
					presenter.syncCategoryAsync(selectedCategory)
					loadArticles()
					refreshMenu.clearAnimation()
					animator.cancel()
					refreshMenu.rotation = 0f
					noItemsText.visibility = View.GONE
					syncUnreadCount()
				}
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun startRotatingAnimation(refreshMenu: View): ObjectAnimator {
		val animator = ObjectAnimator
				.ofFloat(refreshMenu, "rotation", refreshMenu.rotation + 360)
		animator.duration = 1000L
		animator.repeatCount = Animation.INFINITE
		animator.start()
		return animator
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		val id = item.itemId
		if (id == R.id.nav_unread) {
			item.isChecked = true
			selectedCategory = null
			supportActionBar?.title = getString(R.string.app_name)
		} else {
			val category = item.actionView.tag.toString()
			selectedCategory = category
			supportActionBar?.title = getString(CATEGORIES_TO_RES_MAP[category] ?: throw IllegalStateException("unknow category"))
		}
		loadArticles()
		drawerLayout.closeDrawer(GravityCompat.START)
		return true
	}

	private fun loadArticles() {
		launch(UI + job) {
			val articles = presenter.getArticlesInCategoryAsync(selectedCategory)
			if (adapter == null) {
				adapter = ArticlesAdapter(articles as OrderedRealmCollection<Article>)
				articlesView.adapter = adapter
			} else {
				adapter?.onDataChanged(articles as OrderedRealmCollection<Article>)
			}
			if (articles.isEmpty()) {
				noItemsText.visibility = View.VISIBLE
			} else {
				noItemsText.visibility = View.GONE
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == REQUEST_ARTICLE_READ && resultCode == Activity.RESULT_OK) {
			launch(UI + job) {
				presenter.markArticleReadAsync(data?.getStringExtra(ArticleDetailsActivity.KEY_ARTICLE_URL) ?: "")
				syncUnreadCount()
			}
		}
	}

	override fun onSaveInstanceState(outState: Bundle?) {
		super.onSaveInstanceState(outState)
		outState?.putString(KEY_CATEGORY, selectedCategory)
	}

	override fun onDestroy() {
		super.onDestroy()
		presenter.close()
		job.cancel()
		articlesView.clearOnScrollListeners()
	}

	companion object {
		private const val KEY_CATEGORY = "KEY_CATEGORY"
		const val REQUEST_ARTICLE_READ = 1231
	}
}
