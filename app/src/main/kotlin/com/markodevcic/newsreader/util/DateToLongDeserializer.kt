package com.markodevcic.newsreader.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.text.SimpleDateFormat
import java.util.*


class DateToLongDeserializer : JsonDeserializer<Long>() {

	override fun deserialize(jsonparser: JsonParser,
							 deserializationcontext: DeserializationContext): Long {

		val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
		val date = jsonparser.text ?: return Date().time
		return format.parse(date).time
	}

}