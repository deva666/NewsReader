package com.markodevcic.newsreader.articles

import android.app.Activity
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.Spanned
import android.text.style.StrikethroughSpan
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
		if (!article.isUnread) {
			title.setTextColor(ContextCompat.getColor(view.context, android.R.color.secondary_text_dark))
			description.setTextColor(ContextCompat.getColor(view.context, android.R.color.secondary_text_dark))
		}
//		else {
//			title.text = article.title
//		}

		category.text = view.context.getText(CATEGORIES_TO_RES_MAP[article.category] ?: throw IllegalStateException("unknown category"))
		date.text = formatDate(article.publishedAt)
		Picasso.with(view.context)
				.load(article.urlToImage)
				.into(image)
		view.setOnClickListener {
			val activity = view.context as Activity
			val intent = Intent(activity, ArticleDetailsActivity::class.java)
			intent.putExtra(ArticleDetailsActivity.KEY_ARTICLE_URL, article.url)
			activity.startActivityForResult(intent, ArticlesActivity.REQUEST_ARTICLE_READ)
		}
	}

	private fun formatDate(time: Long): String {
		val date = Date(time)
		val dateFormat = android.text.format.DateFormat.getDateFormat(view.context)
		return dateFormat.format(date)
	}

	private fun setSpan(textView: TextView, text: String) {
		textView.setText(text, TextView.BufferType.SPANNABLE)
		val spannable = textView.text as Spannable
		spannable.setSpan(StrikethroughSpan(), 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
	}
}