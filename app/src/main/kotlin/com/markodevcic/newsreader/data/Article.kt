package com.markodevcic.newsreader.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.markodevcic.newsreader.util.DateToLongSerializer
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
	@JsonDeserialize(using = DateToLongSerializer::class)
	var publishedAt: Long? = null

	@JsonIgnore
	var isUnread = true
}