package ru.nobird.android.ui.home

import ru.nobird.android.core.container.ContainerFactory
import javax.inject.Inject

/**
 * Created by lytr777 on 15/12/2017.
 */
class HomeContainerFactory
@Inject
constructor(
        private val homePresenter: HomePresenter
) : ContainerFactory<HomeContainer> {
    override fun create() = HomeContainer(
            homePresenter
    )
}