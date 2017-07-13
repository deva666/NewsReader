package com.markodevcic.newsreader.articles

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.markodevcic.newsreader.R
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.extensions.find
import com.squareup.picasso.Picasso

class ArticlesViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

	private val image = view.find<ImageView>(R.id.item_article_image)
	private val title = view.find<TextView>(R.id.item_article_title)
	private val description = view.find<TextView>(R.id.item_article_description)

	fun bind(article: Article) {
		title.text = article.title
		description.text = article.description
		Picasso.with(view.context)
				.load(article.urlToImage)
				.into(image)
	}

}