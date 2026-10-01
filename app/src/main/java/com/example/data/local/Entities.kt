package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pipeline_runs")
data class PipelineRunEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pipelineId: String,
    val environment: String,
    val commitSha: String,
    val branch: String,
    val triggeredByRole: String,
    val status: String, // INITIATED, RUNNING, PASSED, FAILED, ROLLED_BACK
    val currentStage: String,
    val progressPercent: Int,
    val cveDetected: Int,
    val durationSeconds: Int,
    val logsSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "iac_modules")
data class IaCModuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val moduleName: String,
    val provider: String, // AWS EKS, AWS RDS, Cloudflare, ArgoCD GitOps, Redis Cluster
    val environment: String,
    val status: String, // PROVISIONED, SCALING, DRIFT_DETECTED, TERMINATED
    val replicas: Int,
    val cpuCores: Int,
    val memoryGb: Int,
    val monthlyCostUsd: Double,
    val terraformVersion: String,
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val category: String, // PIPELINE, IAC, SECURITY, RBAC, BACKUP, FINOPS
    val actorRole: String,
    val environment: String,
    val severity: String, // INFO, WARNING, CRITICAL
    val details: String,
    val sha256Hash: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "backup_snapshots")
data class BackupSnapshotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val snapshotCode: String,
    val environment: String,
    val triggerType: String, // AUTOMATED_SCHEDULE, MANUAL_SNAPSHOT, PRE_CHANGE_GUARD
    val modulesCount: Int,
    val totalMonthlyCostSnapshot: Double,
    val sizeKb: Int,
    val sha256Checksum: String,
    val statePayloadJson: String,
    val status: String, // VERIFIED, RESTORED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "compliance_controls")
data class ComplianceControlEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val framework: String, // SOC2 Type II, ISO 27001, CIS K8s v1.8, NIST 800-53
    val controlCode: String,
    val title: String,
    val status: String, // COMPLIANT, WARNING, REMEDIATED
    val severity: String, // HIGH, MEDIUM, LOW
    val description: String,
    val lastCheckedAt: Long = System.currentTimeMillis()
)
