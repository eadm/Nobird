package ru.nobird.android.ui.home

import android.os.Bundle
import dagger.android.AndroidInjection
import ru.nobird.android.core.container.BaseContainerActivity
import javax.inject.Inject

/**
 * Created by lytr777 on 15/12/2017.
 */
class HomeActivity : BaseContainerActivity<HomeContainer>(), HomeView {

    @Inject
    lateinit var homeContainerFactory: HomeContainerFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this) // Important to inject before super call
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        container?.homePresenter?.attachView(this)
    }

    override fun onStop() {
        container?.homePresenter?.detachView(this)
        super.onStop()
    }

    override fun getContainerFactory() = homeContainerFactory
}