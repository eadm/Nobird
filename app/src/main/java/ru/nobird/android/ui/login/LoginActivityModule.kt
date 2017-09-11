package ru.nobird.android.ui.login

import dagger.Module
import dagger.Provides

@Module
class LoginActivityModule {

    @Provides
    fun provideLoginView(activity: LoginActivity) : LoginView = activity

    @Provides
    fun provideLoginPresenter() : LoginPresenter = object : LoginPresenter {}
}