package ru.nobird.android.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import dagger.android.AndroidInjection
import ru.nobird.android.R
import ru.nobird.android.data.twitter.TwitterMgr


class LoginActivity : AppCompatActivity() {
    private lateinit var authWebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
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

        if (savedInstanceState == null) {
            authWebView.loadUrl(authUrl)
        } else {
            authWebView.restoreState(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        authWebView.saveState(outState)
    }
}