package ru.nobird.android.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.nobird.android.ui.home.HomeActivityComponent
import ru.nobird.android.ui.login.LoginActivityComponent
import ru.nobird.android.ui.splash.SplashActivity
import ru.nobird.android.ui.splash.SplashActivityComponent
import javax.inject.Singleton

@Module
(subcomponents = arrayOf(
        LoginActivityComponent::class,
        SplashActivityComponent::class,
        HomeActivityComponent::class
))
abstract class AppModule {

    @Provides
    @Singleton
    fun provideContext(app: Application): Context = app

}
