package com.example.workout.domain.common

sealed class Response<out V, out E : BaseError> {
    abstract val error: E

    abstract val value: V

    data class Success<out V>(override val value: V) : Response<V, Nothing>() {
        override val error: Nothing
            get() = throw IllegalStateException("Это успех")
    }

    data class Failure<out E : BaseError>(override val error: E) : Response<Nothing, E>() {

        override val value: Nothing
            get() = throw IllegalStateException("Это ошибка")
    }

    companion object {
        fun <E : BaseError> failure(error: E) = Failure(error)

        fun <V> success(value: V) = Success(value)
    }
}

inline fun <V, E : BaseError, RV> Response<V, E>.mapSuccess(body: (V) -> RV): Response<RV, E> {
    return when (this) {
        is Response.Success -> Response.success(body(value))
        is Response.Failure -> Response.failure(error)
    }
}
