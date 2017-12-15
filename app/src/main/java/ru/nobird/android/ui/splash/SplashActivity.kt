package ru.nobird.android.ui.splash

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import ru.nobird.android.databinding.ActivitySplashBinding
import android.os.Bundle
import android.util.Log
import android.view.View
import dagger.android.AndroidInjection
import ru.nobird.android.R
import ru.nobird.android.core.container.BaseContainerActivity
import ru.nobird.android.data.SharedPreferenceHelper
import ru.nobird.android.data.twitter.TwitterMgr
import ru.nobird.android.ui.home.HomeActivity
import ru.nobird.android.ui.login.LoginActivity
import javax.inject.Inject

/**
 * Created by lytr777 on 15/12/2017.
 */
class SplashActivity : BaseContainerActivity<SplashContainer>(), SplashView {

    companion object {
        val LOGIN_REQUEST_CODE = 231
    }

    @Inject
    lateinit var splashContainerFactory: SplashContainerFactory

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this) // Important to inject before super call
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        Log.d(javaClass.canonicalName, container.toString())
    }

    private fun showLoginScreen(url: Uri) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.data = url
        startActivityForResult(intent, LOGIN_REQUEST_CODE)
    }

    private fun showHomeScreen() {
        startActivity(Intent(this, HomeActivity::class.java))
    }

    override fun onStart() {
        super.onStart()
        container?.splashPresenter?.attachView(this)
    }

    override fun onStop() {
        container?.splashPresenter?.detachView(this)
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    container?.splashPresenter?.authWithCode(it.data.getQueryParameter(TwitterMgr.OAUTH_VERIFIER))
                }
            } else
                onNetworkError()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun setUrl(url: String) = showLoginScreen(Uri.parse(url))

    override fun onSuccess() = showHomeScreen()

    override fun onLoading() {
        binding.accountFragmentProcessing.visibility = View.VISIBLE
    }

    override fun onNetworkError() {
        Log.d(javaClass.canonicalName, "Network error")
    }

    override fun getContainerFactory() = splashContainerFactory
}
