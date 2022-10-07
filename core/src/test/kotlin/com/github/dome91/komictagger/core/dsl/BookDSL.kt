package com.github.dome91.komictagger.core.dsl

import com.github.dome91.komictagger.core.TestConfiguration
import com.github.dome91.komictagger.core.book.*
import com.github.dome91.komictagger.core.book.Number
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class BookArchiveLoader(
	var bookArchiveFormat: BookArchiveFormat = BookArchiveFormat.CBZ,
	var hasComicInfo: Boolean = true
) {

	companion object {
		fun TestConfiguration.loadBookArchive(block: BookArchiveLoader.() -> Unit): BookPath {
			val loader = BookArchiveLoader()
			block(loader)
			val path = when (loader.bookArchiveFormat) {
				BookArchiveFormat.CBZ -> if (loader.hasComicInfo) {
					BookPath("/books/Deadly Class Vol. 1.cbz")
				} else {
					BookPath("/books/untagged.cbz")
				}
			}

			this.javaClass.getResourceAsStream(path.value)!!.use { bookStorage.store(path, it) }
			return path
		}
	}
}

class BookArchiveGenerator(
	var bookArchiveFormat: BookArchiveFormat = BookArchiveFormat.CBZ,
) {

	companion object {
		fun TestConfiguration.generateBookArchive(block: BookArchiveGenerator.() -> Unit): BookPath {
			val generator = BookArchiveGenerator()
			block(generator)
			val path = when (generator.bookArchiveFormat) {
				BookArchiveFormat.CBZ -> BookPath("/books/book.cbz")
			}

			val bytes = this.javaClass.getResourceAsStream("/books/cover.jpg")!!.readAllBytes()
			val stream = ByteArrayOutputStream()
			ZipOutputStream(stream).use {
				val entry = ZipEntry("cover.jpg")
				it.putNextEntry(entry)
				it.write(bytes)
				it.closeEntry()
				bookStorage.store(path, stream.toByteArray().inputStream())
			}

			return path
		}
	}
}

class BookMetadataBuilder(
	var title: String = "title",
	var series: String = "series",
	var number: Int = 1,
	var count: Int = 2,
	var volume: Int = 3,
	var summary: String = "summary",
	var year: Int = 2000,
	var month: Int = 8,
	var day: Int = 20,
	var writer: String = "writer",
	var penciller: String = "penciller",
	var inker: String = "inker",
	var colorist: String = "colorist",
	var letterer: String = "letterer",
	var coverArtist: String = "coverArtist",
	var editor: String = "editor",
	var publisher: String = "publisher",
	var imprint: String = "imprint",
	var genre: String = "genre",
	var web: String = "web",
	var pageCount: Int = 20,
	var languageISO: String = "en",
	var format: String = "format"
) {

	fun build() = BookMetadata(
		Title(title),
		Series(series),
		Number(number),
		Count(count),
		Volume(volume),
		Summary(summary),
		Year(year),
		Month(month),
		Day(day),
		Writer(writer),
		Penciller(penciller),
		Inker(inker),
		Colorist(colorist),
		Letterer(letterer),
		CoverArtist(coverArtist),
		Editor(editor),
		Publisher(publisher),
		Imprint(imprint),
		Genre(genre),
		Web(web),
		PageCount(pageCount),
		LanguageISO(languageISO),
		Format(format)
	)

	companion object {
		fun bookMetadata(block: BookMetadataBuilder.() -> Unit): BookMetadata {
			val builder = BookMetadataBuilder()
			block(builder)
			return builder.build()
		}
	}
}