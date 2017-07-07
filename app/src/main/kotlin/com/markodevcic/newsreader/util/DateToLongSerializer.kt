package com.markodevcic.newsreader.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.text.SimpleDateFormat


class DateToLongSerializer : JsonDeserializer<Long>() {

	override fun deserialize(jsonparser: JsonParser,
							 deserializationcontext: DeserializationContext): Long {

		val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
		val date = jsonparser.text
		return format.parse(date).time
	}

}