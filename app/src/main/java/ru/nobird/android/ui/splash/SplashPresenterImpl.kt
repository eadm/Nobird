package ru.nobird.android.ui.splash

import android.util.Log
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
    init {
        Log.d(javaClass.canonicalName, "$this init")
    }

    private var currentId: Long = 0

    override fun attachView(view: SplashView) {
        super.attachView(view)
        Log.d(javaClass.canonicalName, "$this attachView")

        currentId = SharedPreferenceHelper.getInstance().getLong(SharedPreferenceHelper.CURRENT_ACCOUNT_ID)
        if (currentId != 0L)
            onLogin()
        else
            getAuthURL()
    }

    private val disposable = CompositeDisposable()

    private fun onLogin() = view?.onSuccess()

    private fun onError() = view?.onNetworkError()

    private fun onUrlSet(url: String) = view?.setUrl(url)

    override fun getAuthURL() {
        view?.onLoading()

        disposable.add(Single.create<String> { s -> s.onSuccess(TwitterMgr.getInstance().authURL) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onUrlSet(it) }, { this.onError() }))
    }

    override fun authWithCode(code: String) {
        view?.onLoading()

        disposable.add(Observable.fromCallable({ TwitterMgr.getInstance().authSuccess(code) })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onLogin() }, { this.onError() }))
    }

    override fun destroy() {}
}