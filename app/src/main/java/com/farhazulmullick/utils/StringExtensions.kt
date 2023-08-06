package com.farhazulmullick.utils

val String.Companion.EMPTY by lazy { "" }

val String?.value get() = this ?: String.EMPTY