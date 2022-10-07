package com.github.dome91.komictagger.core.book

import org.slf4j.LoggerFactory
import java.io.FileInputStream

class AnalyzeBook(
	private val books: Books,
	private val bookArchiveFormatIdentifier: BookArchiveFormatIdentifier,
	private val bookReaderWriterFacade: BookReaderWriterFacade,
	private val bookMetadataFetcher: BookMetadataFetcher
) {

	operator fun invoke(path: BookPath) {
		val bookFormat = analyzeBookFormat(path)
		if(bookFormat == null) {
			log.info("Skipping ${path}. Unsupported Book Format")
			return
		}

		val bookReaderWriter = bookReaderWriterFacade.get(bookFormat, path)
		if(bookReaderWriter.hasComicInfo()) {
			log.info("Skipping ${path.value}. ComicInfo.xml found")
			return
		}

		val request = BookMetadataFetchRequest.from(path)
		val bookMetadata = bookMetadataFetcher.fetch(request, bookReaderWriter)
		if(bookMetadata != null) {
			log.info("Found match for ${path.value}")
			bookReaderWriter.writeComicInfo(bookMetadata)
		} else {
			log.info("Found no match for ${path.value}")
		}

		val book = Book.new(path, bookFormat, bookMetadata)
		books.upsertByPath(book)
	}

	private fun analyzeBookFormat(path: BookPath): BookArchiveFormat? {
		return FileInputStream(path.value).use(bookArchiveFormatIdentifier::identify)
	}

	companion object {
		private val log = LoggerFactory.getLogger(AnalyzeBook::class.java)
	}
}

interface BookMetadataFetcher {
	fun fetch(request: BookMetadataFetchRequest, bookReaderWriter: BookReaderWriter): BookMetadata?
}

data class BookMetadataFetchRequest(val searchTerm: String, val format: BookFormat?) {
	companion object {

		private val VOLUME_REGEX = Regex("(v|Volume|volume|Vol|vol).*\\d{1,5}")
		private val ISSUE_REGEX = Regex("(#|Issue|issue|Iss|iss).*\\d{1,5}")

		fun from(path: BookPath): BookMetadataFetchRequest {
			val filename = path.filenameWithoutExtension()

			val bookFormat = if(VOLUME_REGEX.containsMatchIn(filename)) {
				BookFormat.VOLUME
			} else if(ISSUE_REGEX.containsMatchIn(filename)) {
				BookFormat.ISSUE
			} else {
				null
			}

			val searchTerm = when(bookFormat) {
				BookFormat.VOLUME -> VOLUME_REGEX.replace(filename, "")
				BookFormat.ISSUE -> ISSUE_REGEX.replace(filename, "")
				null -> filename
			}

			return BookMetadataFetchRequest(searchTerm.trim(), bookFormat)
		}
	}
}
