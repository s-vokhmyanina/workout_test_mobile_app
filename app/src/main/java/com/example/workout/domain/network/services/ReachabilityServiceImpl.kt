package com.example.workout.domain.network.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class ReachabilityServiceImpl(context: Context) : ReachabilityService {
    private var _state: ConnectionState? = null

    override val currentState: ConnectionState
        get() =
            _state ?: getConnectionState(
                connectivityManager.getNetworkCapabilities(
                    connectivityManager.activeNetwork
                )
            )

    private var connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private fun getConnectionState(capabilities: NetworkCapabilities?): ConnectionState =
        with(capabilities) {
            when {
                this?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> ConnectionState.HAS_CONNECTION
                this?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> ConnectionState.HAS_CONNECTION
                this?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> ConnectionState.HAS_CONNECTION
                else -> ConnectionState.NO_CONNECTION
            }
        }
}
