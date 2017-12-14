package ru.nobird.android.ui.login

import android.os.Bundle
import dagger.android.AndroidInjection
import ru.nobird.android.core.presenter.BasePresenterActivity
import javax.inject.Inject


class LoginActivity : BasePresenterActivity<LoginPresenter, LoginView>(), LoginView {

    @Inject
    lateinit var loginPresenterFactory: LoginPresenterFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this) // Important to inject before super call
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        presenter?.attachView(this)
    }

    override fun onStop() {
        presenter?.detachView(this)
        super.onStop()
    }

    override fun getPresenterFactory() = loginPresenterFactory
}