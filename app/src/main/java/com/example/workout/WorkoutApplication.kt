package com.example.workout

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.workout.di.Interactors
import com.example.workout.domain.interactors.InteractorsFactoryImpl
import com.example.workout.domain.network.APIFactoryImpl
import com.example.workout.domain.repositories.RepositoryFactoryImpl
import com.example.workout.presentation.ViewModelFactory
import com.example.workout.presentation.ViewModels
import com.example.workout.services.ServicesFactoryImpl

class WorkoutApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        initializeFactories()
    }

    private fun initializeFactories() {
        val servicesFactory = ServicesFactoryImpl(this)
        val apiFactory = APIFactoryImpl(BuildConfig.API_URL, servicesFactory)
        val repositoryFactory = RepositoryFactoryImpl(apiFactory = apiFactory)
        Interactors.factory = InteractorsFactoryImpl(repositoryFactory)
        ViewModels.factory = ViewModelFactory()
    }
}
