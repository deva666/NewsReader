package com.markodevcic.newsreader

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.Toast
import com.markodevcic.newsreader.articles.MainActivity
import com.markodevcic.newsreader.data.availableCategories
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
		
		if (presenter.hasCategoriesSelected()) {
			startMainView()
		} else {
			setContentView(R.layout.activity_startup)
			for ((key, resId) in availableCategories()) {
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
						presenter.syncCategories()
					} catch (fail: Throwable) {
						Toast.makeText(this@StartupActivity, "An error occurred while downloading news sources", Toast.LENGTH_SHORT).show()
					} finally {
						dialog.dismiss()
					}
				}
			}
		}
	}

	private fun showProgressDialog() = ProgressDialog.show(this, "Downloading sources", "", true, false)

	override fun showNoCategorySelected() {
		Toast.makeText(this, "Please choose at least one category", Toast.LENGTH_SHORT).show()
	}

	override fun startMainView() {
		startActivity<MainActivity>()
	}

	override fun onDestroy() {
		super.onDestroy()
		job.cancel()
	}
}
