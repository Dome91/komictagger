package com.github.dome91.komictagger.core.book

import com.github.dome91.komictagger.core.ID
import com.github.dome91.komictagger.core.ValueObject
import java.io.File
import java.util.*

@JvmInline
value class BookID(override val value: String) : ID<String> {
	companion object {
		fun new() = BookID(UUID.randomUUID().toString())
	}
}

@JvmInline
value class BookPath(override val value: String) : ValueObject<String> {
	fun filenameWithoutExtension(): String {
		return File(value).nameWithoutExtension
	}
}

enum class BookArchiveFormat {
	CBZ
}

enum class BookFormat {
	ISSUE, VOLUME
}

class Book(val id: BookID, val path: BookPath, val archiveFormat: BookArchiveFormat, val metadata: BookMetadata?) {
	companion object {
		fun new(path: BookPath, archiveBookFormat: BookArchiveFormat, metadata: BookMetadata?): Book {
			return Book(BookID.new(), path, archiveBookFormat, metadata)
		}
	}
}

@JvmInline
value class Title(override val value: String) : ValueObject<String>

@JvmInline
value class Number(override val value: Int) : ValueObject<Int>

@JvmInline
value class Count(override val value: Int) : ValueObject<Int>

@JvmInline
value class Series(override val value: String) : ValueObject<String>

@JvmInline
value class Volume(override val value: Int) : ValueObject<Int>

@JvmInline
value class Summary(override val value: String) : ValueObject<String>

@JvmInline
value class Year(override val value: Int) : ValueObject<Int>

@JvmInline
value class Month(override val value: Int) : ValueObject<Int>

@JvmInline
value class Day(override val value: Int) : ValueObject<Int>

@JvmInline
value class Writer(override val value: String) : ValueObject<String>

@JvmInline
value class Penciller(override val value: String) : ValueObject<String>

@JvmInline
value class Inker(override val value: String) : ValueObject<String>

@JvmInline
value class Colorist(override val value: String) : ValueObject<String>

@JvmInline
value class Letterer(override val value: String) : ValueObject<String>

@JvmInline
value class CoverArtist(override val value: String) : ValueObject<String>

@JvmInline
value class Editor(override val value: String) : ValueObject<String>

@JvmInline
value class Publisher(override val value: String) : ValueObject<String>

@JvmInline
value class Imprint(override val value: String) : ValueObject<String>

@JvmInline
value class Genre(override val value: String) : ValueObject<String>

@JvmInline
value class Web(override val value: String) : ValueObject<String>

@JvmInline
value class PageCount(override val value: Int) : ValueObject<Int>

@JvmInline
value class LanguageISO(override val value: String) : ValueObject<String>

@JvmInline
value class Format(override val value: String) : ValueObject<String>

class BookMetadata(
	val title: Title?,
	val series: Series?,
	val number: Number?,
	val count: Count?,
	val volume: Volume?,
	val summary: Summary?,
	val year: Year?,
	val month: Month?,
	val day: Day?,
	val writer: Writer?,
	val penciller: Penciller?,
	val inker: Inker?,
	val colorist: Colorist?,
	val letterer: Letterer?,
	val coverArtist: CoverArtist?,
	val editor: Editor?,
	val publisher: Publisher?,
	val imprint: Imprint?,
	val genre: Genre?,
	val web: Web?,
	val pageCount: PageCount?,
	val languageISO: LanguageISO?,
	val format: Format?
)