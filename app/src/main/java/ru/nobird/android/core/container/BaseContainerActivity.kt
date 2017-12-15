package ru.nobird.android.core.container

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import ru.nobird.android.core.loader.ContainerLoader

/**
 * Created by lytr777 on 14/12/2017.
 */
abstract class BaseContainerActivity<C : Container> : AppCompatActivity() {
    private companion object {
        private const val LOADER_ID = 127
    }

    protected var container: C? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loader = supportLoaderManager.getLoader<C>(LOADER_ID)
        if (loader == null) {
            initLoader()
        } else {
            onContainer((loader as ContainerLoader<C>).container)
        }
    }

    private fun initLoader() {
        supportLoaderManager.initLoader(LOADER_ID, null, object : LoaderManager.LoaderCallbacks<C> {
            override fun onLoadFinished(loader: Loader<C>?, data: C) {
                onContainer(data)
            }

            override fun onLoaderReset(loader: Loader<C>?) {
                container = null
            }

            override fun onCreateLoader(id: Int, args: Bundle?): Loader<C> =
                    ContainerLoader(this@BaseContainerActivity, getContainerFactory())

        })
    }

    @CallSuper
    protected open fun onContainer(container: C?) {
        this.container = container
    }

    protected abstract fun getContainerFactory(): ContainerFactory<C>
}