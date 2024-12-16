package com.aquajusmin.sakewonomu.constants

object Constants {

    const val SAKE_NO_NAME = "名前なしでお酒"

    // Fragmentデータ受け渡し
    const val KEY_INPUT_TYPE = "InputType"

    const val KEY_INFO_LIST_TYPE = "SakeInfoListType"
    const val VALUE_INFO_LIST_TYPE_CHART = "Chart"
    const val VALUE_INFO_LIST_TYPE_LIST = "List"

    const val KEY_SAKE_INFO_DATA = "SakeInfoData"
    const val KEY_SAKE_INFO_ID = "SakeInfoId"

    const val KEY_KURA_INFO_ID = "KuraInfoId"

    // DB
    const val TABLE_SAKE_INFO = "sake_info_table"
    const val TABLE_KURA_INFO = "kura_info_table"

    // shared preferences
    const val SHARED_PREFERENCES_KEY = "kome_wo_nomu_shared_preferences"
    const val PREFERENCES_LOCK_KURA_ID = "shared_preferences_lock_kura_id"
    const val PREFERENCES_LOCK_PLACE_NAME = "shared_preferences_lock_place_name"
    const val PREFERENCES_CONTINUE_FLAG = "shared_preferences_continue_flag"

    // request keys
    const val REQUEST_CODE_PERMISSION = 1

    // invalid point value
    const val INVALID_POINT_VALUE = -99

    const val MAX_BUBBLE_SIZE = 10F
}
