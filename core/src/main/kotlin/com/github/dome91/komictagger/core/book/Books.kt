package com.github.dome91.komictagger.core.book

interface Books {
	fun upsertByPath(book: Book)
}

class InMemoryBooks : Books {

	private val store = HashMap<BookID, Book>()

	override fun upsertByPath(book: Book) {
		store.replaceAll { _, existingBook ->
			if (existingBook.path == book.path) {
				book
			} else {
				existingBook
			}
		}
	}

}