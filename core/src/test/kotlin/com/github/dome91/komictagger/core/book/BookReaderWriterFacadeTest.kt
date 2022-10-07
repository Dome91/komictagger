package com.github.dome91.komictagger.core.book

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.dome91.komictagger.core.coreTest
import com.github.dome91.komictagger.core.dsl.BookArchiveGenerator.Companion.generateBookArchive
import com.github.dome91.komictagger.core.dsl.BookArchiveLoader.Companion.loadBookArchive
import com.github.dome91.komictagger.core.dsl.BookMetadataBuilder.Companion.bookMetadata
import com.github.dome91.komictagger.core.shouldHaveValue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.util.zip.ZipInputStream

internal class BookReaderWriterFacadeTest {

	@Test
	fun `returns true if ComicInfo file is in archive`() = coreTest {
		val path = loadBookArchive { }
		val bookReaderWriter = bookReaderWriterFacade.get(BookArchiveFormat.CBZ, path)
		bookReaderWriter.hasComicInfo().shouldBeTrue()
	}

	@Test
	fun `returns false if ComicInfo file is in archive`() = coreTest {
		val path = loadBookArchive { hasComicInfo = false }
		val bookReaderWriter = bookReaderWriterFacade.get(BookArchiveFormat.CBZ, path)
		bookReaderWriter.hasComicInfo().shouldBeFalse()
	}

	@Test
	fun `writes ComicInfo file`() = coreTest {
		val path = generateBookArchive { }
		val bookMetadata = bookMetadata { }

		val bookReaderWriter = bookReaderWriterFacade.get(BookArchiveFormat.CBZ, path)
		bookReaderWriter.writeComicInfo(bookMetadata)

		bookStorage.retrieve(path) {
			val stream = ZipInputStream(it)
			stream.nextEntry!!.name shouldBe "cover.jpg"

			val entry = stream.nextEntry!!
			entry.name shouldBe "ComicInfo.xml"
			val comicInfo = xmlMapper.readValue<ComicInfo>(stream)
			comicInfo.title shouldHaveValue "title"
			comicInfo.series shouldHaveValue "series"
		}
	}

	@Test
	fun `returns cover`() = coreTest {
		val path = loadBookArchive {  }

		val bookReaderWriter = bookReaderWriterFacade.get(BookArchiveFormat.CBZ, path)
		val cover = bookReaderWriter.readCover()
		cover.shouldNotBeNull()
		cover.height shouldBe 4048
		cover.width shouldBe 3036
	}

}