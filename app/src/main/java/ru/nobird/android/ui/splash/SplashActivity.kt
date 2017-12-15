package ru.nobird.android.ui.splash

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import ru.nobird.android.databinding.ActivitySplashBinding
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import dagger.android.AndroidInjection
import ru.nobird.android.MainScreen
import ru.nobird.android.R
import ru.nobird.android.core.container.BaseContainerActivity
import ru.nobird.android.data.twitter.TwitterMgr
import ru.nobird.android.ui.LoginActivity
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
    }

    private fun showLoginScreen(url: Uri) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.data = url
        startActivityForResult(intent, LOGIN_REQUEST_CODE)
    }

    private fun showHomeScreen() {
        val intent = Intent(this, MainScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
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
            } else {
                onNetworkError()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showLoginScreen(url: String) = showLoginScreen(Uri.parse(url))

    override fun onSuccess() = showHomeScreen()

    override fun onLoading() {
        binding.accountFragmentProcessing.visibility = View.VISIBLE
    }

    override fun onNetworkError() = Snackbar.make(binding.root, R.string.error_twitter_api, Snackbar.LENGTH_LONG).show()

    override fun getContainerFactory() = splashContainerFactory
}
