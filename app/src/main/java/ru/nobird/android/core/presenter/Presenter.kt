package ru.nobird.android.core.presenter

interface Presenter<in V> {
    fun attachView(view: V)
    fun detachView(view: V)
    fun destroy()
}