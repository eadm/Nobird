package ru.nobird.android.ui.splash

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.nobird.android.core.presenter.PresenterBase
import ru.nobird.android.data.SharedPreferenceHelper
import ru.nobird.android.data.twitter.TwitterMgr

/**
 * Created by lytr777 on 15/12/2017.
 */
class SplashPresenterImpl : SplashPresenter, PresenterBase<SplashView>() {
    private var currentId: Long = 0

    override fun attachView(view: SplashView) {
        super.attachView(view)

        currentId = SharedPreferenceHelper.getInstance().getLong(SharedPreferenceHelper.CURRENT_ACCOUNT_ID)
        if (currentId != 0L) {
            onLogin()
        } else {
            fetchAuthURL()
        }
    }

    private val disposable = CompositeDisposable()

    private fun onLogin() = view?.onSuccess()

    private fun onError() = view?.onNetworkError()

    private fun fetchAuthURL() {
        view?.onLoading()

        disposable.add(Single.fromCallable({ TwitterMgr.getInstance().authURL })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ view?.showLoginScreen(it) }, { this.onError() }))
    }

    override fun authWithCode(code: String) {
        view?.onLoading()

        disposable.add(Observable.fromCallable({ TwitterMgr.getInstance().authSuccess(code) })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onLogin() }, { this.onError() }))
    }

    override fun detachView(view: SplashView) {
        super.detachView(view)
        disposable.clear()
    }

    override fun destroy() {}
}