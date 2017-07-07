package com.markodevcic.newsreader.data

class ArticleResponse {
	lateinit var status: String
	lateinit var source: String
	lateinit var sortBy: String
	lateinit var articles: List<Article>
}