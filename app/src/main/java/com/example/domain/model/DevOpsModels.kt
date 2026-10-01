package com.example.domain.model

enum class EnvironmentType(
    val displayName: String,
    val apiSlug: String,
    val monthlyBudgetLimit: Double,
    val baseDeployFreqPerDay: Int,
    val baseSuccessRate: Double
) {
    PRODUCTION("Production", "production", 1000.0, 14, 99.4),
    STAGING("Staging", "staging", 500.0, 22, 98.8),
    DEVELOPMENT("Development", "development", 300.0, 36, 97.2)
}

enum class DevOpsSection(val title: String, val testTag: String) {
    DASHBOARD("Dashboard", "nav_dashboard"),
    INFRASTRUCTURE("Infrastructure (IaC)", "nav_infrastructure"),
    PIPELINES("CI/CD Pipelines", "nav_pipelines"),
    CLUSTER_HEALTH("Cluster Health", "nav_cluster"),
    FINOPS("FinOps Analytics", "nav_finops"),
    SECURITY_RBAC("Security, RBAC & Audit", "nav_security_rbac"),
    BLUEPRINT("Blueprint & API", "nav_blueprint")
}

enum class Permission(val code: String, val label: String) {
    TRIGGER_PIPELINE("pipeline:trigger", "Trigger CI/CD Pipelines"),
    PROVISION_INFRA("infra:provision", "Deploy & Scale IaC Modules"),
    TEARDOWN_INFRA("infra:teardown", "Teardown Cloud Resources"),
    OPTIMIZE_FINOPS("finops:optimize", "Apply FinOps Right-Sizing"),
    MANAGE_BACKUPS("backup:manage", "Create & Restore State Snapshots"),
    REMEDIATE_COMPLIANCE("security:remediate", "Execute Security Remediation"),
    STRESS_TEST_CLUSTER("cluster:benchmark", "Run High-Load Cluster Benchmark")
}

enum class RbacRole(
    val roleName: String,
    val badgeCode: String,
    val clearanceLevel: String,
    val permissions: Set<Permission>
) {
    PLATFORM_ADMIN(
        roleName = "Platform Admin",
        badgeCode = "ROOT-SRE",
        clearanceLevel = "Level 5 • Full Access",
        permissions = Permission.entries.toSet()
    ),
    SECOPS_ENGINEER(
        roleName = "SecOps Engineer",
        badgeCode = "SEC-OPS",
        clearanceLevel = "Level 4 • Security & Audit",
        permissions = setOf(
            Permission.TRIGGER_PIPELINE,
            Permission.REMEDIATE_COMPLIANCE,
            Permission.MANAGE_BACKUPS,
            Permission.STRESS_TEST_CLUSTER
        )
    ),
    FINOPS_ANALYST(
        roleName = "FinOps Analyst",
        badgeCode = "FIN-OPS",
        clearanceLevel = "Level 3 • Cost & Quota",
        permissions = setOf(
            Permission.OPTIMIZE_FINOPS,
            Permission.MANAGE_BACKUPS
        )
    ),
    READ_ONLY_AUDITOR(
        roleName = "Read-Only Auditor",
        badgeCode = "AUDITOR",
        clearanceLevel = "Level 1 • Compliance View",
        permissions = emptySet()
    );

    fun can(permission: Permission): Boolean = permissions.contains(permission)
}

// --- Part 3: Core API Endpoint Contracts ---
data class MetricsSummaryContract(
    val monthly_cost: Double = 395.00,
    val cost_status: String = "optimized",
    val security_pass: Boolean = true,
    val critical_vulnerabilities: Int = 0,
    val deployment_frequency_per_day: Int = 14,
    val pipeline_success_rate_percentage: Double = 99.4
)

data class PipelineTriggerRequestContract(
    val environment: String = "staging",
    val commit_sha: String = "a1b2c3d4e5f6g7h8i9j0"
)

data class PipelineTriggerResponseContract(
    val pipeline_id: String = "job_9983471",
    val status: String = "initiated",
    val timestamp: String = "2026-10-01T10:32:00Z"
)

data class CostSimulationContract(
    val current_infra_cost: Double = 395.00,
    val proposed_infra_cost: Double = 420.00,
    val cost_difference: Double = 25.00,
    val budget_violation: Boolean = false
)

// --- Real-Time WebSocket & Cluster Telemetry Models ---
data class WebSocketTelemetryEvent(
    val id: String,
    val channel: String, // ws://telemetry/k8s, ws://security/cve, ws://finops/billing, ws://gitops/sync
    val eventType: String,
    val payload: String,
    val latencyMs: Int,
    val severity: String, // OK, WARN, CRIT
    val timestampFormatted: String
)

data class ClusterNodeMetric(
    val nodeId: String,
    val zone: String,
    val instanceType: String,
    val role: String,
    val cpuUsagePercent: Int,
    val memoryUsagePercent: Int,
    val activePods: Int,
    val maxPods: Int,
    val networkMbps: Int,
    val status: String // Ready, Scaling, Cordoned
)

data class LoadBenchmarkResult(
    val isRunning: Boolean = false,
    val concurrentWorkers: Int = 64,
    val totalTransactionsProcessed: Int = 0,
    val throughputOpsPerSec: Int = 0,
    val p95LatencyMs: Double = 1.8,
    val p99LatencyMs: Double = 3.4,
    val memoryEfficiencyScore: Int = 99,
    val lastRunSummary: String = "Ready — Zero bottlenecks detected"
)
