package com.markodevcic.newsreader.extensions

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.view.View

@Suppress("UNCHECKED_CAST")
fun <T : View> Activity.find(id: Int): T {
	return this.findViewById(id) as T
}

@Suppress("UNCHECKED_CAST")
fun <T : View> Fragment.find(id: Int): T {
	return this.view.findViewById(id) as T
}

inline fun <reified T: Activity> Context.startActivity() =
		this.startActivity(newIntent<T>())

inline fun <reified T : Context> Context.newIntent(): Intent =
		Intent(this, T::class.java)
