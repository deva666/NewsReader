package com.markodevcic.newsreader

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.widget.CheckBox
import com.markodevcic.newsreader.articles.ArticlesActivity
import com.markodevcic.newsreader.data.CATEGORIES_TO_RES_MAP
import com.markodevcic.newsreader.extensions.showToast
import com.markodevcic.newsreader.extensions.startActivity
import com.markodevcic.newsreader.injection.Injector
import kotlinx.android.synthetic.main.activity_startup.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class StartupActivity : AppCompatActivity(), StartupView {

	@Inject
	lateinit var presenter: StartupPresenter

	private val job = Job()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Injector.appComponent.inject(this)
		presenter.bind(this)

		if (presenter.canOpenMainView) {
			startMainView()
		} else {
			setContentView(R.layout.activity_startup)
			for ((key, resId) in CATEGORIES_TO_RES_MAP) {
				val checkBox = LayoutInflater.from(this).inflate(R.layout.item_category, categoriesHost, false) as CheckBox
				checkBox.tag = key
				checkBox.setText(resId)
				checkBox.setOnCheckedChangeListener { box, checked ->
					presenter.onCategoryChanging(box.tag.toString(), checked)
				}
				categoriesHost.addView(checkBox)
			}
			saveCategoriesBtn.setOnClickListener {
				launch(UI + job) {
					val dialog = showProgressDialog()
					try {
						presenter.downloadInitialAsync()
					} catch (fail: Throwable) {
						Log.e("Sync", fail.message, fail)
						showToast("An error occurred while downloading news")
					} finally {
						dialog.dismiss()
					}
				}
			}
		}
	}

	private fun showProgressDialog() = ProgressDialog.show(this, "Downloading sources", "", true, false)

	override fun showNoCategorySelected() {
		showToast("Please choose at least one category")
	}

	override fun startMainView() {
		startActivity<ArticlesActivity>()
	}

	override fun onDestroy() {
		super.onDestroy()
		job.cancel()
	}
}
