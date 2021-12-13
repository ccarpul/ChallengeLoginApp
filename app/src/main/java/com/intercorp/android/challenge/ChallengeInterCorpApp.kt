package com.intercorp.android.challenge

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.intercorp.android.challenge.di.DependenciesModuleLogin
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ChallengeInterCorpApp : Application() {

    companion object {
        lateinit var mApp: ChallengeInterCorpApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        mApp = this
        startKoin {
            androidContext(mApp)
            modules(listOf(DependenciesModuleLogin))
        }
    }
}