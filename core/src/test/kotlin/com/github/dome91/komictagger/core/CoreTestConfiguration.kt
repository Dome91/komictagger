package com.github.dome91.komictagger.core

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.dome91.komictagger.core.book.*


class CoreTestConfiguration : TestConfiguration {
	override val books: Books = InMemoryBooks()
	override val bookStorage: BookStorage = InMemoryBookStorage()

	val xmlMapper = XmlMapper.xmlBuilder()
		.addModule(
			KotlinModule.Builder()
				.configure(KotlinFeature.StrictNullChecks, true)
				.build()
		)
		.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
		.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true)
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		.build()

	val bookReaderWriterFacade = BookReaderWriterFacade(bookStorage, xmlMapper)
}

fun coreTest(block: CoreTestConfiguration.() -> Unit) {
	val configuration = CoreTestConfiguration()
	block(configuration)
}