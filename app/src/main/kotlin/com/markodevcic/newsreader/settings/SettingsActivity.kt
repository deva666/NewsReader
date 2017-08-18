package com.markodevcic.newsreader.settings


import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.markodevcic.newsreader.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_settings)
		window.statusBarColor = Color.TRANSPARENT
		settingsClose.setOnClickListener { finish() }
	}
}
