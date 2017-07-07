package com.markodevcic.newsreader.extensions

import android.content.SharedPreferences

inline fun SharedPreferences.editorApply(action: SharedPreferences.Editor.() -> Unit) {
	val editor = this.edit()
	action.invoke(editor)
	editor.apply()
}

inline fun SharedPreferences.editorCommit(action: SharedPreferences.Editor.() -> Unit) {
	val editor = this.edit()
	action.invoke(editor)
	editor.commit()
}