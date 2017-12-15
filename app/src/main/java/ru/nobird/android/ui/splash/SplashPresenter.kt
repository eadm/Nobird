package ru.nobird.android.ui.splash

import ru.nobird.android.core.presenter.Presenter

/**
 * Created by lytr777 on 15/12/2017.
 */
interface SplashPresenter : Presenter<SplashView> {
    fun getAuthURL()
    fun authWithCode(code: String)
}