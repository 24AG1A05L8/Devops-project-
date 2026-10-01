package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PipelineRunEntity::class,
        IaCModuleEntity::class,
        AuditLogEntity::class,
        BackupSnapshotEntity::class,
        ComplianceControlEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DevOpsDatabase : RoomDatabase() {
    abstract fun devOpsDao(): DevOpsDao

    companion object {
        @Volatile
        private var INSTANCE: DevOpsDatabase? = null

        fun getInstance(context: Context): DevOpsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DevOpsDatabase::class.java,
                    "devops_core_enterprise.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
