package com.markodevcic.newsreader.articles

import android.app.Activity
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.markodevcic.newsreader.R
import com.markodevcic.newsreader.articledetails.ArticleDetailsActivity
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.CATEGORIES_TO_RES_MAP
import com.markodevcic.newsreader.extensions.find
import com.squareup.picasso.Picasso
import java.util.*

class ArticlesViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

	private val image = view.find<ImageView>(R.id.item_article_image)
	private val title = view.find<TextView>(R.id.item_article_title)
	private val description = view.find<TextView>(R.id.item_article_description)
	private val category = view.find<TextView>(R.id.item_article_category)
	private val date = view.find<TextView>(R.id.item_article_date)

	fun bind(article: Article) {
		description.text = article.description
		title.text = article.title
		val isUnread = article.isUnread
		if (!isUnread) {
			title.setTextColor(ContextCompat.getColor(view.context, android.R.color.secondary_text_dark))
			description.setTextColor(ContextCompat.getColor(view.context, android.R.color.secondary_text_dark))
			category.setTextColor(ContextCompat.getColor(view.context, android.R.color.secondary_text_dark))
			date.setTextColor(ContextCompat.getColor(view.context, android.R.color.secondary_text_dark))
		} else {
			title.setTextColor(ContextCompat.getColor(view.context, android.R.color.primary_text_light))
			description.setTextColor(ContextCompat.getColor(view.context, android.R.color.primary_text_light))
			category.setTextColor(ContextCompat.getColor(view.context, android.R.color.tertiary_text_light))
			date.setTextColor(ContextCompat.getColor(view.context, android.R.color.tertiary_text_light))
		}

		category.text = view.context.getText(CATEGORIES_TO_RES_MAP[article.category]!!)
		date.text = formatDate(article.publishedAt ?: Date().time)
		val imageUrl = article.urlToImage
		if (imageUrl != null && imageUrl.isNotBlank()) {
			Picasso.with(view.context)
					.load(article.urlToImage)
					.centerCrop()
					.tag(TAG)
					.fit()
					.placeholder(R.drawable.ic_image)
					.error(R.drawable.ic_image)
					.into(image)
		} else {
			image.setImageDrawable(view.context.getDrawable(R.drawable.ic_image))
		}
		view.setOnClickListener {
			val activity = view.context as Activity
			val intent = Intent(activity, ArticleDetailsActivity::class.java)
			intent.putExtra(ArticleDetailsActivity.KEY_ARTICLE_URL, article.url)
			activity.startActivityForResult(intent, ArticlesActivity.REQUEST_ARTICLE_READ)
		}
	}

	private fun formatDate(time: Long): String {
		val date = Date(time)
		val dateFormat = android.text.format.DateFormat.getMediumDateFormat(view.context)
		return dateFormat.format(date)
	}

	companion object TAG
}