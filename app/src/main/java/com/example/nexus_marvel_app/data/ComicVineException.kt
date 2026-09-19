package com.example.nexus_marvel_app.data

/** Normalized error carrying a user-friendly (pt-BR) message. */
class ComicVineException(
    message: String,
    val statusCode: Int? = null,
    cause: Throwable? = null,
) : Exception(message, cause)
