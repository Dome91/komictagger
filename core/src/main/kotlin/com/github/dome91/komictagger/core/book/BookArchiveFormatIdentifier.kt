package com.github.dome91.komictagger.core.book

import org.apache.tika.Tika
import org.apache.tika.mime.MediaType
import java.io.InputStream

class BookArchiveFormatIdentifier  {

	private val tika = Tika()

	fun identify(stream: InputStream): BookArchiveFormat? = when (tika.detect(stream)) {
		MediaType.APPLICATION_ZIP.toString() -> BookArchiveFormat.CBZ
		else -> null
	}
}