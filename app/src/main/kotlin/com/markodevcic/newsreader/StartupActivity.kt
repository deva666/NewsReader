package com.markodevcic.newsreader

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.widget.CheckBox
import com.markodevcic.newsreader.api.ApiFactory
import com.markodevcic.newsreader.data.KEY_CATEGORIES
import com.markodevcic.newsreader.data.availableCategories
import com.markodevcic.newsreader.extensions.editorApply
import com.markodevcic.newsreader.extensions.editorCommit
import com.markodevcic.newsreader.extensions.startActivity
import com.markodevcic.newsreader.injection.Injector
import com.markodevcic.newsreader.sync.SyncService
import kotlinx.android.synthetic.main.activity_startup.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class StartupActivity : AppCompatActivity() {

	@Inject
	lateinit var prefs: SharedPreferences

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Injector.appComponent.inject(this)
		
		prefs.editorCommit {
			remove(KEY_CATEGORIES)
		}
		if (prefs.contains(KEY_CATEGORIES)) {
			this.startActivity<MainActivity>()
		} else {
			setContentView(R.layout.activity_startup)
			for ((key, resId) in availableCategories()) {
				val checkBox = LayoutInflater.from(this).inflate(R.layout.item_category, categoriesHost, false) as CheckBox
				checkBox.tag = key
				checkBox.setText(resId)
				checkBox.setOnCheckedChangeListener { box, checked ->
					val categorySet = prefs.getStringSet(KEY_CATEGORIES, setOf())
					if (checked) {
						prefs.editorApply {
							putStringSet(KEY_CATEGORIES, categorySet + box.tag.toString())
						}
					} else {
						prefs.editorApply {
							putStringSet(KEY_CATEGORIES, categorySet - box.tag.toString())
						}
					}
				}
				categoriesHost.addView(checkBox)
			}
			saveCategoriesBtn.setOnClickListener {
				launch(UI) {
					val syncService = SyncService(ApiFactory.create())
					Log.d("SYNC", "Started")
					try {
						syncService.downloadSources(prefs.getStringSet(KEY_CATEGORIES, setOf()))
					} catch(e: Exception) {
						Log.e("SYNC", e.message, e)
					}
					Log.d("SYNC", "Ended")
				}
//				if (prefs.getStringSet(KEY_CATEGORIES, setOf()).isEmpty()) {
//					Toast.makeText(this, "Please choose at least one category", Toast.LENGTH_SHORT).show()
//				} else {
//					this.startActivity<MainActivity>()
//				}
			}
		}
	}

}
