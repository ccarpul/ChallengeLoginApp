package com.intercorp.android.challenge.utils

import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.intercorp.android.challenge.presentation.MainActivity

fun validator(editText: Array<out TextInputEditText>) = editText.all { it.text?.isNotEmpty() ?: true }

fun Fragment.showLoading() = (activity as MainActivity).showLoading()

fun Fragment.hideLoading() = (activity as MainActivity).hideLoading()

fun watcher( vararg editText: TextInputEditText, action: (editText: Array<out TextInputEditText>) -> Unit) {
    editText.forEach {
        it.doAfterTextChanged { action(editText) }
    }
}

