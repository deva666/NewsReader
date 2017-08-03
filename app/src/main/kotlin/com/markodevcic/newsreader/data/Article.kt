package com.markodevcic.newsreader.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.markodevcic.newsreader.util.DateToLongDeserializer
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Article : RealmObject() {

	@PrimaryKey
	lateinit var url: String
	lateinit var title: String
	var author: String? = null
	var description: String? = null
	var urlToImage: String? = null
	lateinit var category: String
	@JsonDeserialize(using = DateToLongDeserializer::class)
	var publishedAt: Long? = null

	@JsonIgnore
	var isUnread = true
}