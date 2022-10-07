package com.github.dome91.komictagger.core.book.impl

import com.github.dome91.komictagger.core.book.BookArchiveFormat
import com.github.dome91.komictagger.core.book.BookFormat
import com.github.dome91.komictagger.core.book.BookMetadataFetchRequest
import com.github.dome91.komictagger.core.coreTest
import com.github.dome91.komictagger.core.dsl.BookArchiveLoader.Companion.loadBookArchive
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ComicVineBookMetadataFetcherTest {

	@Test
	fun `test ads`() = coreTest {
		val path = loadBookArchive {  }
		val request = BookMetadataFetchRequest("Deadly Class", BookFormat.VOLUME)
		val bookReaderWriter = bookReaderWriterFacade.get(BookArchiveFormat.CBZ, path)
		ComicVineBookMetadataFetcher().fetch(request, bookReaderWriter)
	}
}