package ru.nobird.android.ui.home

import android.util.Log
import ru.nobird.android.core.presenter.PresenterBase

/**
 * Created by lytr777 on 15/12/2017.
 */
class HomePresenterImpl : HomePresenter, PresenterBase<HomeView>() {
    init {
        Log.d(javaClass.canonicalName, "$this init")
    }

    override fun attachView(view: HomeView) {
        super.attachView(view)
        Log.d(javaClass.canonicalName, "$this attachView")
    }

    override fun destroy() {}
}