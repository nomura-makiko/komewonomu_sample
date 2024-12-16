package com.aquajusmin.sakewonomu.model.repository.data

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.StringRes
import com.aquajusmin.sakewonomu.R

data class SakeInfoData(
    val id: Long = 0L,
    val name: String = "",
    val picturePath: String? = "",
    val startPoint: Int = 0,
    val kuraId: Long = 0L,
    val sakeRank: Int = 0,
    val juicyPoint: Int = 0,
    val sweetPoint: Int = 0,
    val richPoint: Int = 0,
    val scentPoint: Int = 0,
    val memo: String = "",
    val sakeType: Long = 0L,
    val place: String = "",
    val registerDate: String = "",
): Parcelable {
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(name)
        dest.writeString(picturePath)
        dest.writeInt(startPoint)
        dest.writeLong(kuraId)
        dest.writeInt(sakeRank)
        dest.writeInt(juicyPoint)
        dest.writeInt(sweetPoint)
        dest.writeInt(richPoint)
        dest.writeInt(scentPoint)
        dest.writeString(memo)
        dest.writeLong(sakeType)
        dest.writeString(place)
        dest.writeString(registerDate)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SakeInfoData> = object: Parcelable.Creator<SakeInfoData> {
            override fun createFromParcel(source: Parcel): SakeInfoData {
                return SakeInfoData(
                    source.readLong() ?: 0L,
                    source.readString() ?: "",
                    source.readString() ?: "",
                    source.readInt() ?: 0,
                    source.readLong() ?: 0L,
                    source.readInt() ?: 0,
                    source.readInt() ?: 0,
                    source.readInt() ?: 0,
                    source.readInt() ?: 0,
                    source.readInt() ?: 0,
                    source.readString() ?: "",
                    source.readLong() ?: 0L,
                    source.readString() ?: "",
                    source.readString() ?: "",
                )
            }

            override fun newArray(size: Int): Array<SakeInfoData?>  = arrayOfNulls(size)
        }

        private const val SAKE_RANK_JUNDAI = 1
        private const val SAKE_RANK_DAIGIN = 2
        private const val SAKE_RANK_JUNGIN = 3
        private const val SAKE_RANK_JUNMAI = 4
        private const val SAKE_RANK_GINJOU = 5
        private const val SAKE_RANK_TOKUJUN = 6
        private const val SAKE_RANK_HONJOUZOU = 7
        private const val SAKE_RANK_OTHER = 8

        private const val SAKE_TYPE_NONE = 0x0000000000000000L
        private const val SAKE_TYPE_NAMA = 0x0000000000000001L
        private const val SAKE_TYPE_GENSYU = 0x0000000000000010L
        private const val SAKE_TYPE_NIGORI = 0x0000000000000100L
        private const val SAKE_TYPE_KIMOTO = 0x0000000000001000L
        private const val SAKE_TYPE_SIZUKU = 0x0000000000010000L
        private const val SAKE_TYPE_HAPPOU = 0x0000000000100000L
        private const val SAKE_TYPE_YAMAHAI = 0x0000000001000000L
        private const val SAKE_TYPE_KOSYU = 0x0000000010000000L
        private const val SAKE_TYPE_KIJOUSYU = 0x0000000100000000L
        private const val SAKE_TYPE_HIYAOROSHI = 0x0000001000000000L

        fun allSakeType() = listOf(
            SakeType.None,
            SakeType.Nama,
            SakeType.Gensyu,
            SakeType.Nigori,
            SakeType.Kimoto,
            SakeType.Sizuku,
            SakeType.Happou,
            SakeType.Yamahai,
            SakeType.Kosyu,
            SakeType.Kijousyu,
            SakeType.Hiyaoroshi
        )

        fun allSakeRank() = listOf(
            SakeRank.JunDai,
            SakeRank.Daigin,
            SakeRank.JunGin,
            SakeRank.Junmai,
            SakeRank.Ginjou,
            SakeRank.TokuJun,
            SakeRank.Honjouzou,
            SakeRank.Other,
        )
    }

    sealed class SakeRank(@StringRes val resId: Int, val value: Int) {
        object JunDai: SakeRank(R.string.sake_rank_junmai_daigin, SAKE_RANK_JUNDAI)
        object Daigin: SakeRank(R.string.sake_rank_dai_ginjou, SAKE_RANK_DAIGIN)
        object JunGin: SakeRank(R.string.sake_rank_junmai_ginjou, SAKE_RANK_JUNGIN)
        object Junmai: SakeRank(R.string.sake_rank_junmai, SAKE_RANK_JUNMAI)
        object Ginjou: SakeRank(R.string.sake_rank_ginjou, SAKE_RANK_GINJOU)
        object TokuJun: SakeRank(R.string.sake_rank_tokubetu_junmai, SAKE_RANK_TOKUJUN)
        object Honjouzou: SakeRank(R.string.sake_rank_honjouzou, SAKE_RANK_HONJOUZOU)
        object Other: SakeRank(R.string.sake_rank_other, SAKE_RANK_OTHER)
    }

    sealed class SakeType(@StringRes val resId: Int, val value: Long) {
        object None: SakeType(R.string.blank, SAKE_TYPE_NONE)
        object Nama: SakeType(R.string.sake_type_nama, SAKE_TYPE_NAMA)
        object Gensyu: SakeType(R.string.sake_type_gensyu, SAKE_TYPE_GENSYU)
        object Nigori: SakeType(R.string.sake_type_nigori, SAKE_TYPE_NIGORI)
        object Kimoto: SakeType(R.string.sake_type_kimoto, SAKE_TYPE_KIMOTO)
        object Sizuku: SakeType(R.string.sake_type_sizuku_tori, SAKE_TYPE_SIZUKU)
        object Happou: SakeType(R.string.sake_type_happou, SAKE_TYPE_HAPPOU)
        object Yamahai: SakeType(R.string.sake_type_yamahai, SAKE_TYPE_YAMAHAI)
        object Kosyu: SakeType(R.string.sake_type_kosyu, SAKE_TYPE_KOSYU)
        object Kijousyu: SakeType(R.string.sake_type_kijousyu, SAKE_TYPE_KIJOUSYU)
        object Hiyaoroshi: SakeType(R.string.sake_type_hiyaorosi, SAKE_TYPE_HIYAOROSHI)
    }
}
