package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DevOpsDao {

    // --- Pipeline Runs ---
    @Query("SELECT * FROM pipeline_runs WHERE environment = :environment ORDER BY timestamp DESC")
    fun observePipelinesByEnv(environment: String): Flow<List<PipelineRunEntity>>

    @Query("SELECT * FROM pipeline_runs ORDER BY timestamp DESC LIMIT 50")
    fun observeAllPipelines(): Flow<List<PipelineRunEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPipelineRun(run: PipelineRunEntity): Long

    @Update
    suspend fun updatePipelineRun(run: PipelineRunEntity)

    @Query("SELECT COUNT(*) FROM pipeline_runs")
    suspend fun getPipelineCount(): Int

    // --- IaC Modules ---
    @Query("SELECT * FROM iac_modules WHERE environment = :environment ORDER BY id ASC")
    fun observeIaCModulesByEnv(environment: String): Flow<List<IaCModuleEntity>>

    @Query("SELECT * FROM iac_modules ORDER BY id ASC")
    fun observeAllIaCModules(): Flow<List<IaCModuleEntity>>

    @Query("SELECT * FROM iac_modules WHERE environment = :environment ORDER BY id ASC")
    suspend fun getIaCModulesByEnvSync(environment: String): List<IaCModuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIaCModules(modules: List<IaCModuleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIaCModule(module: IaCModuleEntity): Long

    @Update
    suspend fun updateIaCModule(module: IaCModuleEntity)

    @Query("DELETE FROM iac_modules WHERE environment = :environment")
    suspend fun deleteIaCModulesByEnv(environment: String)

    // --- Audit Logs ---
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 200")
    fun observeAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLogsBatch(logs: List<AuditLogEntity>)

    @Query("DELETE FROM audit_logs WHERE id NOT IN (SELECT id FROM audit_logs ORDER BY timestamp DESC LIMIT 250)")
    suspend fun pruneOldAuditLogs()

    // --- Backup Snapshots ---
    @Query("SELECT * FROM backup_snapshots ORDER BY createdAt DESC")
    fun observeBackupSnapshots(): Flow<List<BackupSnapshotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackupSnapshot(snapshot: BackupSnapshotEntity): Long

    @Update
    suspend fun updateBackupSnapshot(snapshot: BackupSnapshotEntity)

    // --- Compliance Controls ---
    @Query("SELECT * FROM compliance_controls ORDER BY id ASC")
    fun observeComplianceControls(): Flow<List<ComplianceControlEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplianceControls(controls: List<ComplianceControlEntity>)

    @Update
    suspend fun updateComplianceControl(control: ComplianceControlEntity)
}
