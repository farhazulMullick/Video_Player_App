package com.farhazulmullick.utils

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.toastS(message: String?) {
    if (activity != null) requireActivity().toastS(message)
}

fun Fragment.toastL(message: String?) {
    if (activity != null) requireActivity().toastL(message)
}

fun Fragment.toastS(stringResId: Int?) = stringResId?.let {
    if (activity != null) requireActivity().toastS(it)
}

fun Fragment.toastL(stringResId: Int?) = stringResId?.let {
    if (activity != null) requireActivity().toastS(it)
}

fun Context.toastS(message: String?) {
    if (!message.isNullOrEmpty()) Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.toastL(message: String?) {
    if (!message.isNullOrEmpty()) Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.toastL(stringResId: Int) {
    Toast.makeText(this, stringResId, Toast.LENGTH_LONG).show()
}

fun Context.toastS(stringResId: Int) {
    Toast.makeText(this, stringResId, Toast.LENGTH_SHORT).show()
}
