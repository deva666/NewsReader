package com.markodevcic.newsreader.startup

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.CheckBox
import com.markodevcic.newsreader.R
import com.markodevcic.newsreader.articles.ArticlesActivity
import com.markodevcic.newsreader.categories.BaseCategoriesActivity
import com.markodevcic.newsreader.extensions.iterator
import com.markodevcic.newsreader.extensions.showToast
import com.markodevcic.newsreader.extensions.startActivity
import com.markodevcic.newsreader.injection.Injector
import kotlinx.android.synthetic.main.layout_categories.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class StartupActivity : BaseCategoriesActivity(), StartupView {

	@Inject
	override lateinit var presenter: StartupPresenter

	override val categoriesViewGroup: ViewGroup
		get() = categoriesHost

	private val job = Job()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Injector.appComponent.inject(this)
		presenter.bind(this)

		if (!presenter.canOpenMainView) {
			setContentView(R.layout.activity_startup)
			setSupportActionBar(toolbar)
			supportActionBar?.title = getString(R.string.app_name)
			fillCategories()

			categoriesHost.iterator().asSequence()
					.filter { v -> v is CheckBox }
					.map { v -> v as CheckBox }
					.first().isChecked = true

			saveCategoriesBtn.setOnClickListener {
				launch(UI + job) {
					val dialog = showProgressDialog()
					try {
						presenter.downloadSourcesAsync()
					} catch (fail: Throwable) {
						Log.e("Sync", fail.message, fail)
						dialog.dismiss()
						showToast("An error occurred while downloading sources")
					} finally {
						dialog.dismiss()
					}
				}
			}
			presenter.onStartCategorySelect()
		} else {
			startMainView()
		}
	}

	private fun showProgressDialog() = ProgressDialog.show(this, "Downloading sources", "", true, false)

	override fun startMainView() {
		startActivity<ArticlesActivity>()
	}

	override fun onDestroy() {
		super.onDestroy()
		job.cancel()
	}
}
