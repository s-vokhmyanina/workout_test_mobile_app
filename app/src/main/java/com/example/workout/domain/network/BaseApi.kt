package com.example.workout.domain.network

import android.util.Log
import com.example.workout.domain.common.BaseError
import com.example.workout.domain.network.services.ConnectionState
import com.example.workout.domain.network.services.ReachabilityService
import com.example.workout.domain.common.Response
import retrofit2.HttpException
import java.net.SocketTimeoutException

open class BaseApi(private val reachabilityService: ReachabilityService) {

    suspend fun <T : Any> request(call: suspend () -> T): Response<T, BaseError.Api> {
        if (reachabilityService.currentState == ConnectionState.NO_CONNECTION) {

            Log.e(LOG_TAG, "Нет инета")
            return Response.failure(BaseError.Api.NoConnection)
        }

        return try {
            Response.success(call.invoke())
        } catch (error: Throwable) {
            Log.e(LOG_TAG, "Ошибка API: ${error.message}")
            val cause = error.cause ?: error

            val resultError = when (error) {
                is SocketTimeoutException -> BaseError.Api.Timeout
                is HttpException -> when (error.code()) {
                    404 -> BaseError.Api.NotFound(error.message ?: cause.message ?: "")
                    else -> BaseError.Api.RequestFail(
                        reason = error.message ?: cause.message ?: "",
                    )
                }
                else -> BaseError.Api.RequestFail(
                    reason = error.message ?: cause.message ?: ""
                )
            }
            Response.failure(resultError)
        }
    }

    companion object {
        private const val LOG_TAG = "BaseApi"
    }
}
