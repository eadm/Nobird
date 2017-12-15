package ru.nobird.android.ui

import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Created by lytr777 on 15/12/2017.
 */
class DefaultWebViewClient(
        private val urlLoadingListener: (String) -> Boolean
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean = urlLoadingListener.invoke(url)
}
