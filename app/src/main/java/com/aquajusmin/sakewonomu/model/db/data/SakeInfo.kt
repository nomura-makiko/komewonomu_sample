package com.aquajusmin.sakewonomu.model.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aquajusmin.sakewonomu.constants.Constants

@Entity(tableName = Constants.TABLE_SAKE_INFO)
data class SakeInfo(

 @PrimaryKey(autoGenerate = true)
 val id: Long = 0L,

 @ColumnInfo(name = "name")
 val name: String,

 @ColumnInfo(name="picture_path")
 val picturePath: String,

 @ColumnInfo(name="start_point")
 val startPoint: Int,

 @ColumnInfo(name="kura_id")
 val kuraId: Long,

 @ColumnInfo(name="sake_rank")
 val sakeRank: Int,

 @ColumnInfo(name="juicy_point")
 val juicyPoint: Int,

 @ColumnInfo(name="sweet_point")
 val sweetPoint: Int,

 @ColumnInfo(name="rich_point")
 val richPoint: Int,

 @ColumnInfo(name="scent_point")
 val scentPoint: Int,

 @ColumnInfo(name="memo")
 val memo: String,

 @ColumnInfo(name="sake_type")
 val sakeType: Long,

 @ColumnInfo(name="place")
 val place: String,

 @ColumnInfo(name="register_date")
 val registerDate: String,
)
