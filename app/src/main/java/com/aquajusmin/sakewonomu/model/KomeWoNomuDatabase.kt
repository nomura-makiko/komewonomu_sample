package com.aquajusmin.sakewonomu.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aquajusmin.sakewonomu.model.dao.KuraInfoDao
import com.aquajusmin.sakewonomu.model.dao.SakeInfoDao
import com.aquajusmin.sakewonomu.model.db.data.KuraInfo
import com.aquajusmin.sakewonomu.model.db.data.SakeInfo

@Database(entities = [SakeInfo::class, KuraInfo::class,], version = 1, exportSchema = false)
abstract class KomeWoNomuDatabase: RoomDatabase() {

    abstract fun sakeInfoDao(): SakeInfoDao
    abstract fun kuraInfoDao(): KuraInfoDao

    companion object {
        @Volatile
        private var INSTANCE: KomeWoNomuDatabase? = null
        fun getDatabases(context: Context): KomeWoNomuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KomeWoNomuDatabase::class.java,
                    "kome_wo_nomu_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
