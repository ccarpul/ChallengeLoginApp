package com.intercorp.android.challenge.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.intercorp.android.challenge.R
import com.intercorp.android.challenge.presentation.viewmodel.LoginViewModel
import com.intercorp.android.challenge.utils.hideLoading
import com.intercorp.android.challenge.utils.showLoading
import kotlinx.android.synthetic.main.fragment_validate_login_sms.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ValidateLoginSmsFragment: Fragment() {

    private val loginViewModel: LoginViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel.loginLiveData.observe(this, ::processLiveData)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_validate_login_sms, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideLoading()
        buttonVerifyCodeLogin.setOnClickListener {
            val code = inputCodeSmsEditText.text.toString()
            if(code.isNotEmpty()) {
                showLoading()
                val credential = PhoneAuthProvider.getCredential(
                    loginViewModel.storedVerificationId,
                    code
                )
                signInWithPhoneAuthCredential(credential)
            } else inputCodeSmsEditText.error = getString(R.string.disclaimer_field_code_sms_empty)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        loginViewModel.loginByPhoneNumber(credential)
    }

    private fun processLiveData(state: LoginViewModel.StateLogin) {
        when (state) {
            is LoginViewModel.StateLogin.Loading -> showLoading()
            is LoginViewModel.StateLogin.Success -> findNavController()
                    .navigate(R.id.action_validateLoginSmsFragment_to_formDataUserFragment)
            is LoginViewModel.StateLogin.Failure -> findNavController()
                .navigate(
                    R.id.action_validateLoginSmsFragment_to_errorFragment,
                bundleOf(LoginFragment.ERROR_MESSAGE_KEY to state.errorMessage)
            )
        }
    }
}