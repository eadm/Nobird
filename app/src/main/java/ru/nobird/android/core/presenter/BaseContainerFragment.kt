package ru.nobird.android.core.presenter

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import ru.nobird.android.core.loader.ContainerLoader

/**
 * Created by lytr777 on 15/12/2017.
 */
abstract class BaseContainerFragment<C : Container> : Fragment() {
    private companion object {
        private const val LOADER_ID = 127
    }

    protected var container: C? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loader = loaderManager.getLoader<C>(LOADER_ID)
        if (loader == null) {
            initLoader()
        } else {
            onContainer((loader as ContainerLoader<C>).container)
        }
    }

    private fun initLoader() {
        loaderManager.initLoader(LOADER_ID, null, object : LoaderManager.LoaderCallbacks<C> {
            override fun onLoadFinished(loader: Loader<C>?, data: C) {
                onContainer(data)
            }

            override fun onLoaderReset(loader: Loader<C>?) {
                container = null
            }

            override fun onCreateLoader(id: Int, args: Bundle?): Loader<C> =
                    ContainerLoader(this@BaseContainerFragment.context!!, getContainerFactory())

        })
    }

    @CallSuper
    protected open fun onContainer(container: C?) {
        this.container = container
    }

    protected abstract fun getContainerFactory(): ContainerFactory<C>
}