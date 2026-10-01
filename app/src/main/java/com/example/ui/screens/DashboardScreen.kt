package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    onRefreshLiveProbes: () -> Unit,
    onNavigateSection: (DevOpsSection) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val budgetPercentUnder = ((1.0 - (metrics.monthly_cost / uiState.selectedEnvironment.monthlyBudgetLimit)) * 100)
        .toInt()
        .coerceIn(-100, 99)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
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
                    .height(156.dp)
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
                                    DeepCharcoalBg.copy(alpha = 0.95f),
                                    DeepCharcoalBg.copy(alpha = 0.82f),
                                    DeepCharcoalBg.copy(alpha = 0.50f)
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
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
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
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldDim)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "LIVE TELEMETRY: ${if (uiState.isWebSocketLive) "ACTIVE" else "PAUSED"}",
                                color = EmeraldSafe,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Cost, Security & AI Control Plane",
                            color = HighContrastWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Live Cloud Probes • Gemini AI Copilot • Zero-Trust RBAC • SHA-256 Audit & Backups",
                            color = MutedSlate,
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
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
                Column {
                    Text(
                        text = "Central Console: Interactive Resource Provisioning Area",
                        color = HighContrastWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Execute CI/CD pipelines, provision Terraform modules, consult AI Copilot, or teardown resources with RBAC & auto-backup protection.",
                        color = MutedSlate,
                        fontSize = 13.sp
                    )
                }

                OutlinedTextField(
                    value = uiState.commitShaInput,
                    onValueChange = onCommitShaChange,
                    label = { Text("Target Git Commit SHA (Synced with GitHub API)", color = MutedSlate) },
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
                        .fillMaxWidth()
                        .testTag("commit_sha_input")
                )

                // Clean FlowRow of Action Buttons (zero text wrapping or height mismatch!)
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
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }

                    OutlinedButton(
                        onClick = { onNavigateSection(DevOpsSection.AI_COPILOT) },
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, ElectricCyan),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("open_ai_copilot_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ask AI Copilot",
                            color = ElectricCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
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
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DeepCharcoalBg)
                                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = mod.moduleName,
                                        color = HighContrastWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = String.format(Locale.US, "$%.2f/mo", mod.monthlyCostUsd),
                                        color = if (mod.status == "TERMINATED") MutedSlate else ElectricCyan,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = JetBrainsMonoFontFamily
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${mod.provider} • ${mod.replicas}x • ${mod.cpuCores} vCPU / ${mod.memoryGb}GB",
                                        color = MutedSlate,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (mod.status == "TERMINATED") CrimsonDanger.copy(alpha = 0.2f)
                                                else EmeraldDim
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
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

        // 4. Real-World Live Cloud Probes & Device Hardware Telemetry Card
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("real_world_probes_dashboard_card")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = EmeraldSafe,
                        modifier = Modifier.size(20.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Real-World Cloud Probes & Device Kernel Telemetry",
                            color = HighContrastWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        uiState.realDeviceTelemetry?.let { hw ->
                            Text(
                                text = "${hw.deviceModel} (SDK ${hw.androidSdkInt}) • ${hw.cpuCores} Cores • RAM: ${hw.ramUsagePercent}% used (${hw.systemAvailableRamMb}MB free) • JVM Heap: ${hw.jvmUsedMemoryMb}MB",
                                color = ElectricCyan,
                                fontSize = 11.sp,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                        }
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CyanActionButton(
                        text = "Ping Live Cloud Endpoints",
                        onClick = onRefreshLiveProbes,
                        isLoading = uiState.isProbingLiveEndpoints,
                        icon = Icons.Default.Refresh,
                        testTag = "dashboard_ping_cloud_button"
                    )
                    OutlinedButton(
                        onClick = { onNavigateSection(DevOpsSection.SETTINGS) },
                        border = BorderStroke(1.dp, ElectricCyan),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "GitHub Sync & Live Links",
                            color = ElectricCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }

                uiState.liveEndpointProbes.forEach { probe ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DeepCharcoalBg)
                            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                            .clickable { openExternalUrl(context, probe.url) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = probe.name,
                                color = HighContrastWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = probe.liveDetail,
                                color = if (probe.isHealthy) EmeraldSafe else AmberWarning,
                                fontSize = 11.sp,
                                fontFamily = JetBrainsMonoFontFamily,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${probe.latencyMs}ms",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JetBrainsMonoFontFamily
                        )
                    }
                }
            }
        }

        // 5. Real-Time WebSocket Telemetry Feed
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
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
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = uiState.webSocketEndpointStatus,
                                color = MutedSlate,
                                fontSize = 11.sp,
                                fontFamily = JetBrainsMonoFontFamily,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

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
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldSafe)
                                    )
                                    Text(
                                        text = "[${evt.timestampFormatted}] ${evt.eventType}",
                                        color = ElectricCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "${evt.latencyMs}ms",
                                    color = EmeraldSafe,
                                    fontSize = 11.sp,
                                    fontFamily = JetBrainsMonoFontFamily
                                )
                            }
                            Text(
                                text = evt.payload,
                                color = HighContrastWhite,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 6. Enterprise Reliability, Audit Logging, Automated Backups & Stress Benchmark Shortcuts
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
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${recentAuditLogs.size} SHA-256 logs • ${backups.size} backups",
                        color = MutedSlate,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                            text = "Stress Benchmark",
                            color = HighContrastWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Async DB p99: ${uiState.benchmarkResult.p99LatencyMs}ms",
                        color = MutedSlate,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
