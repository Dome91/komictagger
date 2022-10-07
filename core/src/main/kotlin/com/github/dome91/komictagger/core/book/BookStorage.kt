package com.github.dome91.komictagger.core.book

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

interface BookStorage {
	fun store(path: BookPath, inputStream: InputStream)
	fun <R> retrieve(path: BookPath, block: (InputStream) -> R): R
	fun append(path: BookPath, block: (OutputStream) -> Unit)
}

class FileSystemBookStorage: BookStorage {
	override fun store(path: BookPath, inputStream: InputStream) {
		TODO("Not yet implemented")
	}

	override fun <R> retrieve(path: BookPath, block: (InputStream) -> R): R {
		TODO("Not yet implemented")
	}

	override fun append(path: BookPath, block: (OutputStream) -> Unit) {
		TODO("Not yet implemented")
	}
}

class InMemoryBookStorage: BookStorage {

	private val store = HashMap<BookPath, ByteArray>()

	override fun store(path: BookPath, inputStream: InputStream) {
		store[path] = inputStream.readAllBytes()
	}

	override fun <R> retrieve(path: BookPath, block: (InputStream) -> R): R {
		return store[path]!!.inputStream().use(block)
	}

	override fun append(path: BookPath, block: (OutputStream) -> Unit) {
		val bytes = store[path]!!
		val stream = ByteArrayOutputStream(bytes.size)
		stream.write(bytes)
		stream.use(block)
		val result = stream.toByteArray()
		store[path] = result
	}

}