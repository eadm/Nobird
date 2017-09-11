package ru.nobird.android.ui.login

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection


class LoginActivity : AppCompatActivity(), LoginView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
    }
}