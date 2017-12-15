package ru.nobird.android.ui.login

import ru.nobird.android.core.container.ContainerFactory
import javax.inject.Inject

/**
 * Created by lytr777 on 15/12/2017.
 */
class LoginContainerFactory
@Inject
constructor(
        private val loginPresenter: LoginPresenter
) : ContainerFactory<LoginContainer> {
    override fun create() = LoginContainer(
            loginPresenter
    )
}