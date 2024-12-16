package com.aquajusmin.sakewonomu.util

class ExtensionFunctions {

    fun String?.ifNullOrEmpty(ifEmptyFunction: () -> String): String {
        return if (this != null && isNotEmpty()) return this
        else ifEmptyFunction()
    }
}