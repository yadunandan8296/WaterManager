package com.watermanager.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.watermanager.data.model.Tenant
import com.watermanager.data.model.WaterLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Tenant::class, WaterLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tenantDao(): TenantDao
    abstract fun waterLogDao(): WaterLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "water_manager_db"
                )
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Pre-populate with default tenants
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDefaultTenants(database.tenantDao())
                }
            }
        }

        private suspend fun populateDefaultTenants(dao: TenantDao) {
            val defaultTenants = listOf(
                Tenant(name = "Rahul Sharma", phone = "9876543210"),
                Tenant(name = "Priya Verma", phone = "9123456780"),
                Tenant(name = "Arjun Reddy", phone = "9012345678"),
                Tenant(name = "Sneha Iyer", phone = "9988776655"),
                Tenant(name = "Imran Khan", phone = "9090909090")
            )
            defaultTenants.forEach { dao.insert(it) }
        }
    }
}
