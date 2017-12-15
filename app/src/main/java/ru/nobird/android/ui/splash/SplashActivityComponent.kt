package ru.nobird.android.ui.splash

import dagger.Subcomponent
import dagger.android.AndroidInjector

/**
 * Created by lytr777 on 15/12/2017.
 */
@Subcomponent(modules = arrayOf(SplashActivityModule::class))
interface SplashActivityComponent : AndroidInjector<SplashActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SplashActivity>()

}