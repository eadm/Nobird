package ru.nobird.android.ui.home

import dagger.Module
import dagger.Provides

/**
 * Created by lytr777 on 15/12/2017.
 */
@Module
class HomeActivityModule {

    @Provides
    fun provideHomePresenter(): HomePresenter = HomePresenterImpl()

}