package com.example.workout.domain.common

sealed class BaseError(open val message: String) {

    sealed class Api(message: String) : BaseError(message = message) {
        object NoConnection : Api(message = "Интернета нету -.-")

        object Timeout : Api(message = "Долго не грузит :с")

        class RequestFail(val reason: String) : Api(message = "Упси ...")

        class NotFound(val reason: String) : Api(message = "Пу-пу-пууу")
}

    sealed class App(message: String) : BaseError(message = message) {
        class General(message: String) : App(message = message)
    }

    val techReason: String
        get() = when (this) {
            is Api.NotFound -> reason
            is Api.RequestFail -> reason
            else -> message
        }
}
