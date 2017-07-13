package com.markodevcic.newsreader.data

import com.fasterxml.jackson.annotation.JsonIgnore
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Source : RealmObject() {

	@PrimaryKey
	lateinit var id: String
	lateinit var name: String
	lateinit var description: String
	lateinit var url: String
	lateinit var category: String
	lateinit var language: String
	lateinit var country: String

	@JsonIgnore
	var lastSyncDate = 0L
}