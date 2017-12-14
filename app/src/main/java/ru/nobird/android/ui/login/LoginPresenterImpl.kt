package ru.nobird.android.ui.login

import android.util.Log
import ru.nobird.android.core.presenter.PresenterBase

class LoginPresenterImpl : LoginPresenter, PresenterBase<LoginView>() {
    init {
        Log.d(javaClass.canonicalName, "init")
    }

    override fun attachView(view: LoginView) {
        super.attachView(view)
        Log.d(javaClass.canonicalName, "attachView")
    }

    override fun destroy() {}
}