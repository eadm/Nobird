package ru.nobird.android.ui.splash

import ru.nobird.android.core.container.ContainerFactory
import javax.inject.Inject

/**
 * Created by lytr777 on 15/12/2017.
 */
class SplashContainerFactory
@Inject
constructor(
        private val splashPresenter: SplashPresenter
) : ContainerFactory<SplashContainer> {
    override fun create() = SplashContainer(
            splashPresenter
    )
}