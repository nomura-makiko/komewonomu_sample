package com.aquajusmin.sakewonomu.model.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aquajusmin.sakewonomu.constants.Constants

@Entity(tableName = Constants.TABLE_KURA_INFO)
data class KuraInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "yomigana")
    val yomigana: String,

    @ColumnInfo(name="prefectures")
    val prefectures: String,
)
