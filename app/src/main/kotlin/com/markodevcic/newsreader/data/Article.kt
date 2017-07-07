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
	lateinit var author: String
	lateinit var description: String
	lateinit var urlToImage: String
	lateinit var category: String
	@JsonDeserialize(using = DateToLongSerializer::class)
	var publishedAt: Long = 0

	@JsonIgnore
	var isUnread = true
}