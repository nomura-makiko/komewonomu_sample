package com.aquajusmin.sakewonomu

import android.app.Application
import androidx.room.Room
import com.aquajusmin.sakewonomu.model.KomeWoNomuDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KomeWoNomuApplication: Application() {
    companion object {
        lateinit var database: KomeWoNomuDatabase
    }

    override fun onCreate() {
        super.onCreate()
        // database
        database = Room.databaseBuilder(
            applicationContext,
            KomeWoNomuDatabase::class.java,
            "kome_wo_nomu_database"
        ).build()
    }
}
