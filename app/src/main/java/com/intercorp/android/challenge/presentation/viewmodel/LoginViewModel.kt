package com.intercorp.android.challenge.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.intercorp.android.challenge.data.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: LoginRepository) : ViewModel() {

    var storedVerificationId = ""
    private var uid = ""

    private val _loginLiveData: MutableLiveData<StateLogin> = MutableLiveData()
    val loginLiveData: LiveData<StateLogin> = _loginLiveData

    private val _persistenceLiveData: MutableLiveData<StatePersistence> = MutableLiveData()
    val persistenceLiveData: LiveData<StatePersistence> = _persistenceLiveData

    sealed class StateLogin {
        object Success: StateLogin()
        data class Failure(val errorMessage: String): StateLogin()
        object Loading : StateLogin()
    }

    sealed class StatePersistence {
        object Success: StatePersistence()
        data class Failure(val errorMessage: String): StatePersistence()
    }

    fun loginByPhoneNumber(credential: PhoneAuthCredential) {

        _loginLiveData.value = StateLogin.Loading
        viewModelScope.launch {
            Firebase.auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    when {
                        task.isSuccessful -> {
                            uid = task.result?.user?.uid.orEmpty()
                            _loginLiveData.value = StateLogin.Success
                        }
                        task.exception is FirebaseAuthInvalidCredentialsException -> {
                            _loginLiveData.value = StateLogin.Failure(
                                (task.exception as? FirebaseAuthInvalidCredentialsException)?.localizedMessage.orEmpty()
                            )
                        }
                        else -> StateLogin.Failure(task.exception?.localizedMessage.orEmpty())
                    }
                }
        }
    }

    fun loginByFacebook(result: LoginResult) {

        _loginLiveData.value = StateLogin.Loading
        FacebookAuthProvider.getCredential(result.accessToken.token).let {
            FirebaseAuth.getInstance()
                .signInWithCredential(it)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        uid = task.result?.user?.uid.orEmpty()
                        _loginLiveData.value = StateLogin.Success
                    }
                }.addOnFailureListener {  exception ->
                    _loginLiveData.value = StateLogin.Failure(exception.localizedMessage ?: "Error")
                }
        }
    }

    fun saveData(data: Map<String, String>) {
        viewModelScope.launch {
            repository.saveData(uid, data).addOnSuccessListener {
                _persistenceLiveData.value = StatePersistence.Success
            }.addOnFailureListener {
                _persistenceLiveData.value =
                    StatePersistence.Failure("An error occurred while trying to create user")
            }.addOnCanceledListener {
                _persistenceLiveData.value = StatePersistence.Failure("Register cancelled")
            }
        }
    }
}