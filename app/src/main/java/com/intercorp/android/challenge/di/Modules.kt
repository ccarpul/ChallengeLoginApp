package com.intercorp.android.challenge.di

import com.intercorp.android.challenge.data.LoginRepository
import com.intercorp.android.challenge.presentation.viewmodel.LoginViewModel
import org.koin.android.viewmodel.dsl.viewModel

import org.koin.dsl.module

val DependenciesModuleLogin = module {
    viewModel { LoginViewModel(get()) }
    single { LoginRepository() }
}