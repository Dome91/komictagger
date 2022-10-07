package com.github.dome91.komictagger.core.library

import com.github.dome91.komictagger.core.ID
import com.github.dome91.komictagger.core.ValueObject

@JvmInline
value class LibraryID(override val value: String): ID<String>

@JvmInline
value class LibraryPath(override val value: String): ValueObject<String>

class Library(val id: LibraryID, val path: LibraryPath) {
}