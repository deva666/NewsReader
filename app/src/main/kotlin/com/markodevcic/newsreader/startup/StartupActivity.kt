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
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class StartupActivity : BaseCategoriesActivity(), StartupView {

	@Inject
	override lateinit var presenter: StartupPresenter

	override val categoriesViewGroup: ViewGroup
		get() = categoriesHost

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
				val dialog = showProgressDialog()

				presenter.downloadSourcesAsync()
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe({}, { fail ->
							Log.e("Sync", fail.message, fail)
							dialog.dismiss()
							showToast(getString(R.string.error_download_sources))
						}, {
							dialog.dismiss()
							startMainView()
						})
			}
			presenter.onStartCategorySelect()
		} else {
			startMainView()
		}
	}

	private fun showProgressDialog() = ProgressDialog.show(this, getString(R.string.downloading_sources), "", true, false)

	override fun startMainView() {
		startActivity<ArticlesActivity>()
	}
}
