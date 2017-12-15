package ru.nobird.android.ui.splash

/**
 * Created by lytr777 on 15/12/2017.
 */
interface SplashView {
    fun setUrl(url: String)
    fun onLoading()
    fun onSuccess()
    fun onNetworkError()
}