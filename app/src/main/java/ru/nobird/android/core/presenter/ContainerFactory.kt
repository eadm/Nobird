package ru.nobird.android.core.presenter

/**
 * Created by lytr777 on 15/12/2017.
 */
interface ContainerFactory<out C : Container> {
    fun create() : C
}