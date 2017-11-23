package com.markodevcic.newsreader.settings


import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.markodevcic.newsreader.R
import com.markodevcic.newsreader.extensions.apply
import com.markodevcic.newsreader.injection.Injector
import com.markodevcic.newsreader.util.KEY_DELETE_DAYS
import kotlinx.android.synthetic.main.activity_settings.*
import javax.inject.Inject

class SettingsActivity : AppCompatActivity() {

	@Inject
	lateinit var sharedPrefs: SharedPreferences

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_settings)
		Injector.appComponent.inject(this)
		window.statusBarColor = Color.TRANSPARENT
		settingsClose.setOnClickListener { finish() }
		val deleteDays = sharedPrefs.getInt(KEY_DELETE_DAYS, 3)
		when (deleteDays) {
			3 -> radioBtn3Days.isChecked = true
			5 -> radioBtn5Days.isChecked = true
			7 -> radioBtn7Days.isChecked = true
		}

		radioGroup.setOnCheckedChangeListener { group, checkedId ->
			val radioBtn = group.findViewById(checkedId)
			sharedPrefs.apply {
				putInt(KEY_DELETE_DAYS, radioBtn.tag.toString().toInt())
			}
		}
	}
}
