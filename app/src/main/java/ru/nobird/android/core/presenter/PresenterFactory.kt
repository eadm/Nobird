package ru.nobird.android.core.presenter

interface PresenterFactory<out P : Presenter<*>> {
    fun create() : P
}