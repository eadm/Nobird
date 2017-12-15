package ru.nobird.android.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.nobird.android.ui.splash.SplashActivityComponent
import javax.inject.Singleton

@Module
(subcomponents = arrayOf(
        SplashActivityComponent::class
))
abstract class AppModule {

    @Provides
    @Singleton
    fun provideContext(app: Application): Context = app

}
