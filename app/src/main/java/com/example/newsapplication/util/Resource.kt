package com.example.newsapplication.util

// This code defines a sealed class called Resource that's commonly used to represent the state
// of a network request or an operation that might result in success, error, or loading states.
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
        class Success<T>(data: T) : Resource<T>(data)
        class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
        class Loading<T>: Resource<T>()
}