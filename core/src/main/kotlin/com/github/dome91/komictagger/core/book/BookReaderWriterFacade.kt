package com.github.dome91.komictagger.core.book

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import net.lingala.zip4j.io.inputstream.ZipInputStream
import net.lingala.zip4j.model.LocalFileHeader
import java.awt.image.BufferedImage
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.imageio.ImageIO

class BookReaderWriterFacade(private val bookStorage: BookStorage, private val xmlMapper: XmlMapper) {

	fun get(format: BookArchiveFormat, path: BookPath): BookReaderWriter = when (format) {
		BookArchiveFormat.CBZ -> CBZBookReaderWriter(path, bookStorage, xmlMapper)
	}
}

interface BookReaderWriter {
	fun hasComicInfo(): Boolean
	fun writeComicInfo(metadata: BookMetadata)
	fun readCover(): BufferedImage?
}

private class CBZBookReaderWriter(
	private val path: BookPath,
	private val bookStorage: BookStorage,
	private val xmlMapper: XmlMapper
) : BookReaderWriter {

	override fun hasComicInfo() = bookStorage.retrieve(path) {
		ZipInputStream(it).use { stream ->
			var fileHeader: LocalFileHeader? = stream.nextEntry
			while (fileHeader != null) {
				if (fileHeader.fileName == "ComicInfo.xml") {
					return@retrieve true
				}
				fileHeader = stream.nextEntry
			}
		}

		false
	}

	override fun writeComicInfo(metadata: BookMetadata) {
		val comicInfo = ComicInfo.from(metadata)
		val bytes = xmlMapper.writeValueAsBytes(comicInfo)
		bookStorage.append(path) {
			val stream = ZipOutputStream(it)
			val entry = ZipEntry("ComicInfo.xml")
			entry.size = bytes.size.toLong()
			stream.putNextEntry(entry)
			stream.write(bytes)
			stream.closeEntry()
		}
	}

	override fun readCover(): BufferedImage? {
		val headers = mutableListOf<String>()

		bookStorage.retrieve(path) {
			val stream = ZipInputStream(it)
			var fileHeader: LocalFileHeader? = stream.nextEntry
			while (fileHeader != null) {
				if (fileHeader.fileName.endsWith("jpg")) headers.add(fileHeader.fileName)
				fileHeader = stream.nextEntry
			}
		}

		headers.sort()
		val coverFilename = headers.first()
		return bookStorage.retrieve(path) {
			val stream = ZipInputStream(it)
			var fileHeader: LocalFileHeader? = stream.nextEntry
			while (fileHeader != null) {
				if (fileHeader!!.fileName == coverFilename) {
					return@retrieve ImageIO.read(stream)
				}
				fileHeader = stream.nextEntry
			}

			null
		}
	}
}

@JacksonXmlRootElement
data class ComicInfo(val title: Title?, val series: Series?) {
	companion object {
		fun from(metadata: BookMetadata) = with(metadata) {
			ComicInfo(title, series)
		}
	}
}
