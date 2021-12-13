package com.intercorp.android.challenge.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.intercorp.android.challenge.R
import com.intercorp.android.challenge.presentation.viewmodel.LoginViewModel
import com.intercorp.android.challenge.utils.showLoading
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by sharedViewModel()

    private val callbackManager = CallbackManager.Factory.create()

    private val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(exception: FirebaseException) {

            when (exception) {
                is FirebaseAuthInvalidCredentialsException ->
                    processError("Invalid Request")

                is FirebaseTooManyRequestsException ->
                    processError("Has occurred an error from our side")
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            showLoading()
            loginViewModel.storedVerificationId = verificationId
            findNavController().navigate(R.id.action_loginFragment_to_validateLoginSmsFragment)
        }
    }

    companion object {
        const val ERROR_MESSAGE_KEY = "message"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel.loginLiveData.observe(this, ::processLiveData)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Firebase.auth.signOut()
        setupViews()
    }

    private fun processLiveData(state: LoginViewModel.StateLogin) {
        when (state) {
            is LoginViewModel.StateLogin.Loading -> showLoading()
            is LoginViewModel.StateLogin.Success -> processSuccessLogin()
            is LoginViewModel.StateLogin.Failure -> processError(state.errorMessage)
        }
    }

    private fun processError(errorMessage: String) {
        findNavController().navigate(
            R.id.action_loginFragment_to_errorFragment,
            bundleOf(ERROR_MESSAGE_KEY to errorMessage)
        )
    }

    private fun setupViews() {
        validateInput()
        textWatcher()
        buttonFacebook.setPermissions("email")
        buttonFacebook.fragment = this
        buttonFacebook.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult) {
                    handleLoginUserByFacebook(result)
                }

                override fun onCancel() {
                    processError("The login was cancelled")
                }

                override fun onError(error: FacebookException?) {
                    processError(error?.localizedMessage ?: "An error was occurred Error")
                }
            })

        buttonPhoneNumberLogin.setOnClickListener {
            showLoading()
            PhoneAuthProvider.verifyPhoneNumber(
                getOptions("${phoneNumberLoginLayout.prefixText}${phoneNumberLoginEditText.text.toString()}")
            )
        }
    }

    private fun validateInput() {
        buttonPhoneNumberLogin.isEnabled =
            with(phoneNumberLoginEditText.text) {
                this?.isNotEmpty() == true && this.toString().length == 10
            }
    }

    private fun textWatcher() {
        phoneNumberLoginEditText.doAfterTextChanged { validateInput() }
    }

    private fun getOptions(phone: String): PhoneAuthOptions =
        PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phone)
            .setTimeout(5L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callback)
            .build()


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        loginViewModel.loginByPhoneNumber(credential)
    }

    private fun handleLoginUserByFacebook(result: LoginResult) {
        loginViewModel.loginByFacebook(result)
    }

    private fun processSuccessLogin() {
        findNavController().navigate(R.id.action_loginFragment_to_formDataUserFragment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}