package com.aquajusmin.sakewonomu.home

import android.net.Uri

data class SakeInfoCard(
    val date: String = "",
    val sakeName: String = "",
    val imageUri: Uri,
    val memo: String = "",
    val starPoint: Int = 0,
) {
}