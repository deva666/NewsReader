package com.markodevcic.newsreader


interface Presenter<T : BaseView> {

	fun bind(view: T)
}