package ru.nobird.android.ui.login

import ru.nobird.android.core.presenter.PresenterFactory
import javax.inject.Inject

class LoginPresenterFactory
@Inject
constructor(): PresenterFactory<LoginPresenter> {
    override fun create() = LoginPresenterImpl()
}