package com.intercorp.android.challenge.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.intercorp.android.challenge.R
import com.intercorp.android.challenge.utils.hideLoading
import kotlinx.android.synthetic.main.fragment_error.*

class ErrorFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_error, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideLoading()

        arguments?.let {
            title.text = it.getString(LoginFragment.ERROR_MESSAGE_KEY)
        }
    }
}