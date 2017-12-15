package ru.nobird.android.ui.splash

import dagger.Module
import dagger.Provides

/**
 * Created by lytr777 on 15/12/2017.
 */
@Module
class SplashActivityModule {

    @Provides
    fun provideSplashPresenter(): SplashPresenter = SplashPresenterImpl()

}