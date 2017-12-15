package ru.nobird.android.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import dagger.android.AndroidInjection
import ru.nobird.android.R
import ru.nobird.android.core.container.BaseContainerActivity
import ru.nobird.android.data.twitter.TwitterMgr
import ru.nobird.android.ui.DefaultWebViewClient
import javax.inject.Inject


class LoginActivity : BaseContainerActivity<LoginContainer>(), LoginView {
    private lateinit var authWebView: WebView

    @Inject
    lateinit var loginContainerFactory: LoginContainerFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this) // Important to inject before super call
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val authUrl = intent.data.toString()

        authWebView = findViewById(R.id.auth_web_view)
        authWebView.webViewClient = DefaultWebViewClient({ url: String ->
            if (url.startsWith(TwitterMgr.CALLBACK)) {
                if (url.contains("success")) {
                    val uri = Uri.parse(url)
                    this@LoginActivity.setResult(android.app.Activity.RESULT_OK, Intent().setData(uri))
                } else {
                    TwitterMgr.getInstance().authFailure()
                    this@LoginActivity.setResult(android.app.Activity.RESULT_CANCELED)
                }
                this@LoginActivity.finish()
            }
            false
        })
        authWebView.loadUrl(authUrl)
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