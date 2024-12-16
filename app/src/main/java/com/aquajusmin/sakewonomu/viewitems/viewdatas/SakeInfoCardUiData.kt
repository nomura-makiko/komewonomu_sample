package com.aquajusmin.sakewonomu.viewitems.viewdatas

import android.graphics.Bitmap
import android.net.Uri
import com.aquajusmin.sakewonomu.constants.Constants
import com.aquajusmin.sakewonomu.util.KomeWoNoMuUtil

data class SakeInfoCardUiData(
    val id: Long = 0L,
    val date: String = "",
    val sakeName: String = "",
    val kuraName: String = "",
    val prefectures: String = "",
    val starPoint: Int = 0,
    val starPointText: String = KomeWoNoMuUtil.getPointLabel(starPoint),
    val picture: Uri? = null,
    val memo: String = "",
    // chart用
    val juicyPoint: Int = Constants.INVALID_POINT_VALUE,
    val sweetPoint: Int = Constants.INVALID_POINT_VALUE,
)
