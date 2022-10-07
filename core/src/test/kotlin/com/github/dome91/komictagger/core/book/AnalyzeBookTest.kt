package com.github.dome91.komictagger.core.book

import com.github.dome91.komictagger.core.book.BookFormat.ISSUE
import com.github.dome91.komictagger.core.book.BookFormat.VOLUME
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class AnalyzeBookTest {

	@Test
	fun `builds metadata fetch request`() {
		val verify = { path: BookPath, searchTerm: String, format: BookFormat ->
			val request = BookMetadataFetchRequest.from(path)
			request.searchTerm shouldBe searchTerm
			request.format shouldBe format
		}

		verify(BookPath("/path/Deadly Class Vol. 1.cbz"), "Deadly Class", VOLUME)
		verify(BookPath("/path/Deadly Class Volume 001.cbz"), "Deadly Class", VOLUME)
		verify(BookPath("/path/Deadly Class volume 21.cbz"), "Deadly Class", VOLUME)
		verify(BookPath("/path/Deadly Class #1.cbz"), "Deadly Class", ISSUE)
		verify(BookPath("/path/Deadly Class Iss 001.cbz"), "Deadly Class", ISSUE)
		verify(BookPath("/path/Deadly Class issue 21.cbz"), "Deadly Class", ISSUE)
	}
}