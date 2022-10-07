package com.github.dome91.komictagger.core

import java.util.*

interface ValueObject<T> {
    val value: T
}

interface ID<T>: ValueObject<T>