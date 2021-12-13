package com.intercorp.android.challenge.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.intercorp.android.challenge.R
import com.intercorp.android.challenge.presentation.viewmodel.LoginViewModel
import com.intercorp.android.challenge.utils.hideLoading
import com.intercorp.android.challenge.utils.showLoading
import com.intercorp.android.challenge.utils.validator
import com.intercorp.android.challenge.utils.watcher
import kotlinx.android.synthetic.main.fragment_form_data_user.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class FormDataUserFragment : Fragment() {

    private val loginViewModel: LoginViewModel by sharedViewModel()

    companion object {
        const val NAME_KEY = "name"
        const val LAST_NAME = "last-name"
        const val AGE = "age"
        const val BORN_DATE = "born-date"
        const val COLLECTION_FIRESTONE_USERS = "users/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel.persistenceLiveData.observe(this, ::processLiveData)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_form_data_user, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textWatcher()
        hideLoading()

        createUserButton.setOnClickListener {

            showLoading()
            val data = mapOf(
                NAME_KEY to nameUserEditText.text.toString(),
                LAST_NAME to lastNameUserEditText.text.toString(),
                AGE to ageUserEditText.text.toString(),
                BORN_DATE to bornDateUserEditText.text.toString()
            )
            saveData(data)
        }
    }

    private fun textWatcher() {
        createUserButton.isEnabled = false
        watcher(
            nameUserEditText,
            lastNameUserEditText,
            ageUserEditText,
            bornDateUserEditText
        ) {
            createUserButton.isEnabled = validator(it)
        }
    }

    private fun saveData(data: Map<String, String>) {
        loginViewModel.saveData(data)
    }

    private fun processLiveData(state: LoginViewModel.StatePersistence) {
        when (state) {
            is LoginViewModel.StatePersistence.Success ->
                findNavController().navigate(R.id.action_formDataUserFragment_to_successFragment)
            is LoginViewModel.StatePersistence.Failure ->
                findNavController().navigate(
                    R.id.action_formDataUserFragment_to_errorFragment,
                    bundleOf(LoginFragment.ERROR_MESSAGE_KEY to state.errorMessage)
                )
        }
    }
}