package com.aquajusmin.sakewonomu.model.repository.data

import android.os.Parcel
import android.os.Parcelable

data class KuraInfoData(
    val id: Long = 0L,
    val name: String = "",
    val yomigana: String = "",
    val prefectures: String = "",
): Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(yomigana)
        parcel.writeString(prefectures)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<KuraInfoData> {
        override fun createFromParcel(parcel: Parcel): KuraInfoData {
            return KuraInfoData(
                parcel.readLong(),
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
            )
        }

        override fun newArray(size: Int): Array<KuraInfoData?> {
            return arrayOfNulls(size)
        }
    }
}
