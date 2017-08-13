package com.markodevcic.newsreader


interface Presenter<in T : BaseView> {

	fun bind(view: T)
}