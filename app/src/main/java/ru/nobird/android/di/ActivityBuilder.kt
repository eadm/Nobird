package ru.nobird.android.di

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import ru.nobird.android.ui.login.LoginActivity
import ru.nobird.android.ui.login.LoginActivityComponent

@Module
abstract class ActivityBuilder {

    @Binds
    @IntoMap
    @ActivityKey(LoginActivity::class)
    abstract fun bindLoginActivity(builder: LoginActivityComponent.Builder): AndroidInjector.Factory<out Activity>

}