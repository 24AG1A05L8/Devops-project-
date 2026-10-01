package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.model.DevOpsSection
import com.example.ui.components.CompactSectionTabStrip
import com.example.ui.components.DevOpsSidebar
import com.example.ui.components.DevOpsTopNavbar
import com.example.ui.screens.BlueprintExplorerScreen
import com.example.ui.screens.ClusterHealthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FinOpsAnalyticsScreen
import com.example.ui.screens.InfrastructureScreen
import com.example.ui.screens.PipelinesScreen
import com.example.ui.screens.SecurityRbacAuditScreen
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.CrimsonDim
import com.example.ui.theme.DeepCharcoalBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanDim
import com.example.ui.theme.HighContrastWhite
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCardSurface
import com.example.ui.viewmodel.DevOpsViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DevOpsCoreApp()
            }
        }
    }
}

@Composable
fun DevOpsCoreApp(
    viewModel: DevOpsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val metrics by viewModel.currentMetricsSummary.collectAsStateWithLifecycle()
    val costSim by viewModel.costSimulationContract.collectAsStateWithLifecycle()
    val iacModules by viewModel.allIaCModules.collectAsStateWithLifecycle()
    val pipelines by viewModel.allPipelines.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val backups by viewModel.backupSnapshots.collectAsStateWithLifecycle()
    val complianceControls by viewModel.complianceControls.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // BackHandler for secondary screens returning to Dashboard
    BackHandler(enabled = uiState.activeSection != DevOpsSection.DASHBOARD) {
        viewModel.selectSection(DevOpsSection.DASHBOARD)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCharcoalBg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        val isCompact = maxWidth < 760.dp

        val mainContent: @Composable () -> Unit = {
            Column(modifier = Modifier.fillMaxSize()) {
                // 1. Global Navigation Top-Bar (Fixed 64px style with Env Dropdown, RBAC Role, and System Health Badge)
                DevOpsTopNavbar(
                    selectedEnvironment = uiState.selectedEnvironment,
                    onEnvironmentSelected = viewModel::selectEnvironment,
                    currentRole = uiState.currentRole,
                    onRoleSelected = viewModel::selectRbacRole,
                    isCompact = isCompact,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )

                // Compact quick-navigation tab strip on handheld screens
                if (isCompact) {
                    CompactSectionTabStrip(
                        activeSection = uiState.activeSection,
                        onSectionSelected = viewModel::selectSection
                    )
                    HorizontalDivider(thickness = 1.dp, color = SlateBorder)
                }

                // Live Status / RBAC Policy Alert Banner
                AnimatedVisibility(visible = uiState.statusBannerMessage != null) {
                    val msg = uiState.statusBannerMessage ?: ""
                    val isErr = uiState.isBannerError
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isErr) CrimsonDim else ElectricCyanDim)
                            .border(
                                width = 1.dp,
                                color = if (isErr) CrimsonDanger else ElectricCyan,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("status_feedback_banner"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (isErr) Icons.Default.Warning else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (isErr) CrimsonDanger else ElectricCyan
                            )
                            Text(
                                text = msg,
                                color = HighContrastWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        IconButton(onClick = viewModel::dismissBanner) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss Banner",
                                tint = HighContrastWhite
                            )
                        }
                    }
                }

                // 2. Main Body Area (Sidebar on Wide Screens + Active Section Content)
                Row(modifier = Modifier.fillMaxSize()) {
                    if (!isCompact) {
                        DevOpsSidebar(
                            activeSection = uiState.activeSection,
                            onSectionSelected = viewModel::selectSection
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    ) {
                        when (uiState.activeSection) {
                            DevOpsSection.DASHBOARD -> DashboardScreen(
                                uiState = uiState,
                                metrics = metrics,
                                costSim = costSim,
                                iacModules = iacModules,
                                recentAuditLogs = auditLogs,
                                backups = backups,
                                onTriggerPipeline = viewModel::triggerPipeline,
                                onDeployInfra = viewModel::deploySimulatedInfrastructure,
                                onTeardownResources = viewModel::teardownResources,
                                onOptimizeFinOps = viewModel::restoreOptimizedBaseline,
                                onCommitShaChange = viewModel::updateCommitSha,
                                onToggleWebSocket = viewModel::toggleWebSocketLive,
                                onNavigateSection = viewModel::selectSection
                            )

                            DevOpsSection.INFRASTRUCTURE -> InfrastructureScreen(
                                uiState = uiState,
                                costSim = costSim,
                                iacModules = iacModules,
                                onReplicaDeltaChange = viewModel::updateProposedReplicaDelta,
                                onToggleSpotOptimization = viewModel::toggleSpotOptimization,
                                onDeployInfra = viewModel::deploySimulatedInfrastructure,
                                onTeardownResources = viewModel::teardownResources
                            )

                            DevOpsSection.PIPELINES -> PipelinesScreen(
                                uiState = uiState,
                                pipelines = pipelines,
                                onCommitShaChange = viewModel::updateCommitSha,
                                onBranchChange = viewModel::updateBranch,
                                onTriggerPipeline = viewModel::triggerPipeline
                            )

                            DevOpsSection.CLUSTER_HEALTH -> ClusterHealthScreen(
                                uiState = uiState,
                                onRunHighLoadBenchmark = viewModel::runHighLoadBenchmark
                            )

                            DevOpsSection.FINOPS -> FinOpsAnalyticsScreen(
                                uiState = uiState,
                                metrics = metrics,
                                iacModules = iacModules,
                                onOptimizeFinOps = viewModel::restoreOptimizedBaseline
                            )

                            DevOpsSection.SECURITY_RBAC -> SecurityRbacAuditScreen(
                                uiState = uiState,
                                auditLogs = auditLogs,
                                backups = backups,
                                complianceControls = complianceControls,
                                onSelectRole = viewModel::selectRbacRole,
                                onRunComplianceScan = viewModel::runComplianceAuditScan,
                                onCreateBackup = viewModel::createManualBackup,
                                onRestoreBackup = viewModel::restoreSnapshot,
                                onToggleAutoBackup = viewModel::toggleAutoBackup,
                                onAuditSearchChange = viewModel::updateAuditSearchQuery
                            )

                            DevOpsSection.BLUEPRINT -> BlueprintExplorerScreen(
                                uiState = uiState,
                                metrics = metrics,
                                costSim = costSim
                            )
                        }
                    }
                }
            }
        }

        if (isCompact) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = SlateCardSurface
                    ) {
                        DevOpsSidebar(
                            activeSection = uiState.activeSection,
                            onSectionSelected = { section ->
                                viewModel.selectSection(section)
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                }
            ) {
                Surface(
                    color = DeepCharcoalBg,
                    modifier = Modifier.fillMaxSize()
                ) {
                    mainContent()
                }
            }
        } else {
            Surface(
                color = DeepCharcoalBg,
                modifier = Modifier.fillMaxSize()
            ) {
                mainContent()
            }
        }
    }
}
