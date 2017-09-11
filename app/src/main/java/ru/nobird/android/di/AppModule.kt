package ru.nobird.android.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.nobird.android.ui.login.LoginActivityComponent
import javax.inject.Singleton

@Module
(subcomponents = arrayOf(LoginActivityComponent::class))
abstract class AppModule {

    @Provides
    @Singleton
    fun provideContext(app: Application) : Context = app

}
