package ru.nobird.android.core.loader

import android.content.Context
import android.support.v4.content.Loader

import ru.nobird.android.core.presenter.Container
import ru.nobird.android.core.presenter.ContainerFactory

/**
 * Created by lytr777 on 15/12/2017.
 */
class ContainerLoader<C : Container>(
        appContext: Context,
        private val containerFactory: ContainerFactory<C>
) : Loader<C>(appContext) {

    var container: C? = null
        private set

    override fun onStartLoading() {
        if (container != null) {
            deliverResult(container)
            return
        }

        forceLoad()
    }

    override fun onForceLoad() {
        container = containerFactory.create()
        deliverResult(container)
    }

    override fun onReset() {
        container = null
    }
}