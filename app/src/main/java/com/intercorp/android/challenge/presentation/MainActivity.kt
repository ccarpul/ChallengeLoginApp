package com.intercorp.android.challenge.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.facebook.FacebookSdk
import com.intercorp.android.challenge.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BaseActivity {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FacebookSdk.fullyInitialize()
    }

    override fun showLoading() {
        progressBar.isVisible = true
        navHostFragment.alpha = 0.1F
    }

    override fun hideLoading() {
        progressBar.isVisible = false
        navHostFragment.alpha = 1F
    }
}