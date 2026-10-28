package com.example.qaphela.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ScanEntity::class,
        ScanResultEntity::class,
        BlocklistEntity::class,
        UserReportEntity::class,
        LanguageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun scanDao(): ScanDao
    abstract fun blocklistDao(): BlocklistDao
    abstract fun userReportDao(): UserReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "qaphela_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val database = getDatabase(context)
                            CoroutineScope(Dispatchers.IO).launch {
                                seedBlocklist(database.blocklistDao())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedBlocklist(dao: BlocklistDao) {
            val seedItems = listOf(
                BlocklistEntity(
                    indicator = "capitec-login-security-update.xyz",
                    indicatorType = "URL",
                    reason = "Demonstration Sample: Phishing domain mimicking Capitec Bank login portal.",
                    source = "Qaphela Threat Feed (Sample)"
                ),
                BlocklistEntity(
                    indicator = "sars-e-filing-tax-refund-claim.online",
                    indicatorType = "URL",
                    reason = "Demonstration Sample: Fraudulent tax refund scam site targeting SARS taxpayers.",
                    source = "Qaphela Threat Feed (Sample)"
                ),
                BlocklistEntity(
                    indicator = "fnb-secure-verify-update.site",
                    indicatorType = "URL",
                    reason = "Demonstration Sample: Credential harvesting portal impersonating First National Bank.",
                    source = "Qaphela Threat Feed (Sample)"
                ),
                BlocklistEntity(
                    indicator = "sapo-parcel-delivery-fee.info",
                    indicatorType = "URL",
                    reason = "Demonstration Sample: SA Post Office parcel fee delivery scam site.",
                    source = "Qaphela Threat Feed (Sample)"
                ),
                BlocklistEntity(
                    indicator = "+27600123456",
                    indicatorType = "PHONE",
                    reason = "Demonstration Sample: Flagged SMS sender sending fake bank OTP requests.",
                    source = "Community Flag (Sample)"
                ),
                BlocklistEntity(
                    indicator = "+27829988776",
                    indicatorType = "PHONE",
                    reason = "Demonstration Sample: Reported sender for fake job interview deposit demands.",
                    source = "Community Flag (Sample)"
                ),
                BlocklistEntity(
                    indicator = "absa-online-banking-upgrade.tech",
                    indicatorType = "URL",
                    reason = "Demonstration Sample: Phishing URL collecting online banking passwords.",
                    source = "Qaphela Threat Feed (Sample)"
                )
            )
            dao.insertAll(seedItems)
        }
    }
}
