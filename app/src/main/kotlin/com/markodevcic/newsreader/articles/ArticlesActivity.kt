package com.markodevcic.newsreader.articles

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.markodevcic.newsreader.R
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.extensions.find
import com.markodevcic.newsreader.injection.Injector
import com.markodevcic.newsreader.storage.Repository
import io.realm.OrderedRealmCollection
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class ArticlesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ArticlesView {

	@Inject
	lateinit var articleRepository: Repository<Article>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		Injector.appComponent.inject(this)


		setContentView(R.layout.activity_main)
		val toolbar = findViewById(R.id.toolbar) as Toolbar
		setSupportActionBar(toolbar)

		val recyclerView = find<RecyclerView>(R.id.articlesView)
		recyclerView.isNestedScrollingEnabled = false
		recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
		launch(UI) {
			val articles = articleRepository.getAll()
			val adapter = ArticlesAdapter(articles as OrderedRealmCollection<Article>)
			recyclerView.adapter = adapter
			recyclerView.layoutManager = LinearLayoutManager(this@ArticlesActivity)
		}

		val fab = findViewById(R.id.fab) as FloatingActionButton
		fab.setOnClickListener { view ->
			Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show()
		}

		val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
		val toggle = ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawer.setDrawerListener(toggle)
		toggle.syncState()

		val navigationView = findViewById(R.id.nav_view) as NavigationView
		navigationView.setNavigationItemSelectedListener(this)
	}

	override fun onBackPressed() {
		val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START)
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

		if (id == R.id.nav_camera) {
		} else if (id == R.id.nav_gallery) {

		} else if (id == R.id.nav_slideshow) {

		} else if (id == R.id.nav_manage) {

		} else if (id == R.id.nav_share) {

		} else if (id == R.id.nav_send) {

		}

		val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
		drawer.closeDrawer(GravityCompat.START)
		return true
	}
}
