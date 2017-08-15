package com.markodevcic.newsreader.extensions

import android.view.View
import android.view.ViewGroup
import java.util.*

fun ViewGroup.iterator(): Iterator<View> {
	return ViewGroupIterator(this)
}

private class ViewGroupIterator(private val viewGroup: ViewGroup) : Iterator<View> {
	private val childCount = viewGroup.childCount
	private var currentIndex = 0

	override fun hasNext(): Boolean  {
		checkModification()
		return currentIndex < viewGroup.childCount
	}

	override fun next(): View {
		checkModification()
		return viewGroup.getChildAt(currentIndex++)
	}

	private fun checkModification() {
		if (childCount != viewGroup.childCount) {
			throw ConcurrentModificationException("Viewgroup was modified while iterating")
		}
	}
}