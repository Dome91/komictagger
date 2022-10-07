package com.github.dome91.komictagger.core

import com.github.dome91.komictagger.core.book.BookStorage
import com.github.dome91.komictagger.core.book.Books
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

interface TestConfiguration {
	val books: Books
	val bookStorage: BookStorage
}

inline infix fun <reified T> ValueObject<T>?.shouldHaveValue(expected: T) {
	this.shouldNotBeNull()
	value shouldBe expected
}