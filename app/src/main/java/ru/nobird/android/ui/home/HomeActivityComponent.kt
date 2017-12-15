package ru.nobird.android.ui.home

import dagger.Subcomponent
import dagger.android.AndroidInjector

/**
 * Created by lytr777 on 15/12/2017.
 */
@Subcomponent(modules = arrayOf(HomeActivityModule::class))
interface HomeActivityComponent : AndroidInjector<HomeActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<HomeActivity>()

}