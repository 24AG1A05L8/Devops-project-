package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.AuditLogEntity
import com.example.data.local.BackupSnapshotEntity
import com.example.data.local.IaCModuleEntity
import com.example.domain.model.CostSimulationContract
import com.example.domain.model.DevOpsSection
import com.example.domain.model.MetricsSummaryContract
import com.example.ui.components.CyanActionButton
import com.example.ui.components.DangerOutlineButton
import com.example.ui.components.DevOpsMetricCard
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.DeepCharcoalBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanDim
import com.example.ui.theme.EmeraldDim
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.HighContrastWhite
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.VioletAccent
import com.example.ui.viewmodel.DevOpsUiState
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    uiState: DevOpsUiState,
    metrics: MetricsSummaryContract,
    costSim: CostSimulationContract,
    iacModules: List<IaCModuleEntity>,
    recentAuditLogs: List<AuditLogEntity>,
    backups: List<BackupSnapshotEntity>,
    onTriggerPipeline: () -> Unit,
    onDeployInfra: () -> Unit,
    onTeardownResources: () -> Unit,
    onOptimizeFinOps: () -> Unit,
    onCommitShaChange: (String) -> Unit,
    onToggleWebSocket: (Boolean) -> Unit,
    onNavigateSection: (DevOpsSection) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val budgetPercentUnder = ((1.0 - (metrics.monthly_cost / uiState.selectedEnvironment.monthlyBudgetLimit)) * 100)
        .toInt()
        .coerceIn(-100, 99)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Enterprise Command Center Hero Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            color = SlateCardSurface,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dashboard_hero_banner")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_devops_hero),
                    contentDescription = "DevOps Core Enterprise Control Plane Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    DeepCharcoalBg.copy(alpha = 0.94f),
                                    DeepCharcoalBg.copy(alpha = 0.80f),
                                    DeepCharcoalBg.copy(alpha = 0.45f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ElectricCyanDim)
                                .border(1.dp, ElectricCyan, RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ENV: ${uiState.selectedEnvironment.displayName.uppercase(Locale.US)}",
                                color = ElectricCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldDim)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "WS STREAM: ${if (uiState.isWebSocketLive) "LIVE" else "PAUSED"}",
                                color = EmeraldSafe,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Cost & Security Control Plane",
                            color = HighContrastWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Zero-Trust RBAC (${uiState.currentRole.roleName}) • Real-Time WebSocket Telemetry • Async Room Audit & Backups",
                            color = MutedSlate,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 2. Main Metric Cards (3-Column Adaptive Dashboard Grid matching Part 1 Section 3)
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val isThreeColumn = maxWidth >= 680.dp
            val formattedCost = String.format(Locale.US, "$%.2f", metrics.monthly_cost)
            val costSubLabel = if (budgetPercentUnder >= 0) {
                "$budgetPercentUnder% under budget limit"
            } else {
                "${-budgetPercentUnder}% over budget limit"
            }

            if (isThreeColumn) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    DevOpsMetricCard(
                        title = "Projected Monthly Cost",
                        bigValue = formattedCost,
                        bigValueColor = if (metrics.cost_status == "optimized") EmeraldSafe else CrimsonDanger,
                        subLabel = costSubLabel,
                        subLabelColor = if (metrics.cost_status == "optimized") EmeraldSafe else CrimsonDanger,
                        badgeText = metrics.cost_status.uppercase(Locale.US),
                        testTag = "metric_card_finops",
                        modifier = Modifier.weight(1f)
                    )
                    DevOpsMetricCard(
                        title = "Pipeline Security Status",
                        bigValue = if (metrics.security_pass) "Passed" else "Alert",
                        bigValueColor = if (metrics.security_pass) EmeraldSafe else CrimsonDanger,
                        subLabel = "${metrics.critical_vulnerabilities} Critical CVEs detected",
                        subLabelColor = MutedSlate,
                        badgeText = "ZERO-CVE",
                        testTag = "metric_card_security",
                        modifier = Modifier.weight(1f)
                    )
                    DevOpsMetricCard(
                        title = "Deployment Frequency",
                        bigValue = "${metrics.deployment_frequency_per_day} / Day",
                        bigValueColor = ElectricCyan,
                        subLabel = "Success rate: ${metrics.pipeline_success_rate_percentage}%",
                        subLabelColor = EmeraldSafe,
                        badgeText = "ELITE DORA",
                        testTag = "metric_card_dora",
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    DevOpsMetricCard(
                        title = "Projected Monthly Cost",
                        bigValue = formattedCost,
                        bigValueColor = if (metrics.cost_status == "optimized") EmeraldSafe else CrimsonDanger,
                        subLabel = costSubLabel,
                        subLabelColor = if (metrics.cost_status == "optimized") EmeraldSafe else CrimsonDanger,
                        badgeText = metrics.cost_status.uppercase(Locale.US),
                        testTag = "metric_card_finops",
                        modifier = Modifier.fillMaxWidth()
                    )
                    DevOpsMetricCard(
                        title = "Pipeline Security Status",
                        bigValue = if (metrics.security_pass) "Passed" else "Alert",
                        bigValueColor = if (metrics.security_pass) EmeraldSafe else CrimsonDanger,
                        subLabel = "${metrics.critical_vulnerabilities} Critical CVEs detected",
                        subLabelColor = MutedSlate,
                        badgeText = "ZERO-CVE",
                        testTag = "metric_card_security",
                        modifier = Modifier.fillMaxWidth()
                    )
                    DevOpsMetricCard(
                        title = "Deployment Frequency",
                        bigValue = "${metrics.deployment_frequency_per_day} / Day",
                        bigValueColor = ElectricCyan,
                        subLabel = "Success rate: ${metrics.pipeline_success_rate_percentage}%",
                        subLabelColor = EmeraldSafe,
                        badgeText = "ELITE DORA",
                        testTag = "metric_card_dora",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 3. Central Console: Interactive Resource Provisioning & Pipeline Control Area
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("central_provisioning_console")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Central Console: Interactive Resource Provisioning Area",
                            color = HighContrastWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Execute CI/CD pipelines, provision Terraform modules, or teardown ephemeral resources with RBAC & auto-backup protection.",
                            color = MutedSlate,
                            fontSize = 13.sp
                        )
                    }
                }

                // Target Commit & Cost Delta Summary Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.commitShaInput,
                        onValueChange = onCommitShaChange,
                        label = { Text("Target Git Commit SHA", color = MutedSlate) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = HighContrastWhite,
                            unfocusedTextColor = HighContrastWhite,
                            focusedContainerColor = DeepCharcoalBg,
                            unfocusedContainerColor = DeepCharcoalBg
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("commit_sha_input")
                    )
                }

                // Primary & Secondary Actionable Interactive Buttons (Part 1 Section 4)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CyanActionButton(
                        text = "Trigger Pipeline",
                        onClick = onTriggerPipeline,
                        isLoading = uiState.isPipelineRunning,
                        icon = Icons.Default.PlayArrow,
                        testTag = "trigger_pipeline_button"
                    )

                    CyanActionButton(
                        text = "Deploy Infra ($${String.format(Locale.US, "%.0f", costSim.proposed_infra_cost)}/mo)",
                        onClick = onDeployInfra,
                        isLoading = uiState.isDeployingInfra,
                        icon = Icons.Default.CloudUpload,
                        testTag = "deploy_infra_button"
                    )

                    DangerOutlineButton(
                        text = "Teardown Resources",
                        onClick = onTeardownResources,
                        isLoading = uiState.isTearingDown,
                        testTag = "teardown_resources_button"
                    )

                    OutlinedButton(
                        onClick = onOptimizeFinOps,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, EmeraldSafe),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("optimize_finops_button")
                    ) {
                        Text(
                            text = "Reset Optimized ($395)",
                            color = EmeraldSafe,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Active IaC Modules Quick Grid
                val envModules = iacModules.filter { it.environment == uiState.selectedEnvironment.displayName }
                if (envModules.isNotEmpty()) {
                    Text(
                        text = "ACTIVE INFRASTRUCTURE MODULES (${envModules.size})",
                        color = MutedSlate,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        envModules.forEach { mod ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DeepCharcoalBg)
                                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = mod.moduleName,
                                        color = HighContrastWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = JetBrainsMonoFontFamily
                                    )
                                    Text(
                                        text = "${mod.provider} • ${mod.replicas} replicas • ${mod.cpuCores} vCPU / ${mod.memoryGb}GB",
                                        color = MutedSlate,
                                        fontSize = 12.sp
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "$%.2f/mo", mod.monthlyCostUsd),
                                        color = if (mod.status == "TERMINATED") MutedSlate else ElectricCyan,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = JetBrainsMonoFontFamily
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (mod.status == "TERMINATED") CrimsonDanger.copy(alpha = 0.2f)
                                                else EmeraldDim
                                            )
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = mod.status,
                                            color = if (mod.status == "TERMINATED") CrimsonDanger else EmeraldSafe,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Real-Time WebSocket Telemetry Feed + Enterprise Reliability & Security Quick Cards
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("websocket_telemetry_card")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Real-Time WebSocket Event Stream",
                                color = HighContrastWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.webSocketEndpointStatus,
                                color = MutedSlate,
                                fontSize = 12.sp,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (uiState.isWebSocketLive) "LIVE" else "PAUSED",
                            color = if (uiState.isWebSocketLive) EmeraldSafe else AmberWarning,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = uiState.isWebSocketLive,
                            onCheckedChange = onToggleWebSocket,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DeepCharcoalBg,
                                checkedTrackColor = EmeraldSafe
                            ),
                            modifier = Modifier.testTag("websocket_live_switch")
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepCharcoalBg)
                        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.telemetryStream.take(5).forEach { evt ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldSafe)
                                )
                                Text(
                                    text = "[${evt.timestampFormatted}]",
                                    color = MutedSlate,
                                    fontSize = 11.sp,
                                    fontFamily = JetBrainsMonoFontFamily
                                )
                                Text(
                                    text = evt.eventType,
                                    color = ElectricCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = JetBrainsMonoFontFamily
                                )
                                Text(
                                    text = evt.payload,
                                    color = HighContrastWhite,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = "${evt.latencyMs}ms",
                                color = EmeraldSafe,
                                fontSize = 11.sp,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                        }
                    }
                }
            }
        }

        // 5. Enterprise Reliability, Audit Logging, Automated Backups & Stress Benchmark Shortcuts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                onClick = { onNavigateSection(DevOpsSection.SECURITY_RBAC) },
                color = SlateCardSurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier
                    .weight(1f)
                    .testTag("quick_card_audit_rbac")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = VioletAccent)
                        Text(
                            text = "RBAC & Audit Trail",
                            color = HighContrastWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${recentAuditLogs.size} SHA-256 verified logs • ${backups.size} state backups ready",
                        color = MutedSlate,
                        fontSize = 12.sp
                    )
                }
            }

            Surface(
                onClick = { onNavigateSection(DevOpsSection.CLUSTER_HEALTH) },
                color = SlateCardSurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier
                    .weight(1f)
                    .testTag("quick_card_cluster_load")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = EmeraldSafe)
                        Text(
                            text = "High-Load Benchmark",
                            color = HighContrastWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Async DB p99: ${uiState.benchmarkResult.p99LatencyMs}ms • 4 K8s Nodes Ready",
                        color = MutedSlate,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
