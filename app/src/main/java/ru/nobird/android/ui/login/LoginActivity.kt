package ru.nobird.android.ui.login

import android.os.Bundle
import dagger.android.AndroidInjection
import ru.nobird.android.core.presenter.BaseContainerActivity
import javax.inject.Inject


class LoginActivity : BaseContainerActivity<LoginContainer>(), LoginView {

    @Inject
    lateinit var loginContainerFactory: LoginContainerFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this) // Important to inject before super call
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        container?.loginPresenter?.attachView(this)
    }

    override fun onStop() {
        container?.loginPresenter?.detachView(this)
        super.onStop()
    }

    override fun getContainerFactory() = loginContainerFactory
}