package com.markodevcic.newsreader.storage

import com.markodevcic.newsreader.data.Source

class SourcesRepository : RepositoryBase<Source>() {
	override val clazz: Class<Source>
		get() = Source::class.java

	override val primaryKey: String
		get() = "id"
}