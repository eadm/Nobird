package ru.nobird.android.di

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import ru.nobird.android.ui.splash.SplashActivity
import ru.nobird.android.ui.splash.SplashActivityComponent

@Module
abstract class ActivityBuilder {

    @Binds
    @IntoMap
    @ActivityKey(SplashActivity::class)
    abstract fun bindSplashActivity(builder: SplashActivityComponent.Builder): AndroidInjector.Factory<out Activity>

}