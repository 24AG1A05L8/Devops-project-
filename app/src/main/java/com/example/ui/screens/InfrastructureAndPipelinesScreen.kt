package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.IaCModuleEntity
import com.example.data.local.PipelineRunEntity
import com.example.data.repository.DevOpsRepository
import com.example.domain.model.BlueprintTemplates
import com.example.domain.model.CostSimulationContract
import com.example.ui.components.CyanActionButton
import com.example.ui.components.DangerOutlineButton
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
import com.example.ui.viewmodel.DevOpsUiState
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InfrastructureScreen(
    uiState: DevOpsUiState,
    costSim: CostSimulationContract,
    iacModules: List<IaCModuleEntity>,
    onReplicaDeltaChange: (Int) -> Unit,
    onToggleSpotOptimization: (Boolean) -> Unit,
    onDeployInfra: () -> Unit,
    onTeardownResources: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val envModules = iacModules.filter { it.environment == uiState.selectedEnvironment.displayName }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. FinOps Infrastructure Cost Simulation Engine (Part 3 Contract #3: GET /api/v1/infrastructure/cost-simulation)
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("cost_simulation_card")
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
                            text = "Terraform & FinOps Cost Simulation Engine",
                            color = HighContrastWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Endpoint Contract: GET /api/v1/infrastructure/cost-simulation",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontFamily = JetBrainsMonoFontFamily
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (costSim.budget_violation) CrimsonDanger.copy(alpha = 0.2f) else EmeraldDim)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (costSim.budget_violation) "BUDGET VIOLATION" else "WITHIN QUOTA",
                            color = if (costSim.budget_violation) CrimsonDanger else EmeraldSafe,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Live JSON Payload Preview matching Part 3 Contract #3
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepCharcoalBg)
                        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = """
{
  "current_infra_cost": ${String.format(Locale.US, "%.2f", costSim.current_infra_cost)},
  "proposed_infra_cost": ${String.format(Locale.US, "%.2f", costSim.proposed_infra_cost)},
  "cost_difference": ${String.format(Locale.US, "%.2f", costSim.cost_difference)},
  "budget_violation": ${costSim.budget_violation}
}
                        """.trimIndent(),
                        color = EmeraldSafe,
                        fontSize = 12.sp,
                        fontFamily = JetBrainsMonoFontFamily
                    )
                }

                // Interactive Controls to Simulate Scaling Before Confirming
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Replica Scaling Delta (${if (uiState.proposedReplicaDelta >= 0) "+${uiState.proposedReplicaDelta}" else uiState.proposedReplicaDelta} nodes)",
                            color = HighContrastWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Each node replica adds $25.00/mo to cluster compute spend",
                            color = MutedSlate,
                            fontSize = 12.sp
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledIconButton(
                            onClick = { onReplicaDeltaChange(uiState.proposedReplicaDelta - 1) },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = DeepCharcoalBg,
                                contentColor = HighContrastWhite
                            ),
                            modifier = Modifier.testTag("replica_minus_button")
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease Replicas")
                        }
                        Text(
                            text = "${uiState.proposedReplicaDelta}",
                            color = ElectricCyan,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JetBrainsMonoFontFamily,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        FilledIconButton(
                            onClick = { onReplicaDeltaChange(uiState.proposedReplicaDelta + 1) },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = DeepCharcoalBg,
                                contentColor = HighContrastWhite
                            ),
                            modifier = Modifier.testTag("replica_plus_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase Replicas")
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AWS Spot / Graviton3 Savings Plan (-$45.00/mo)",
                            color = HighContrastWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Shift stateless worker pools to mixed-instance Spot capacity",
                            color = MutedSlate,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = uiState.spotOptimizationEnabled,
                        onCheckedChange = onToggleSpotOptimization,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DeepCharcoalBg,
                            checkedTrackColor = ElectricCyan
                        ),
                        modifier = Modifier.testTag("spot_optimization_switch")
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CyanActionButton(
                        text = "Deploy Infra Plan",
                        onClick = onDeployInfra,
                        isLoading = uiState.isDeployingInfra,
                        icon = Icons.Default.CloudUpload,
                        testTag = "iac_deploy_button"
                    )
                    DangerOutlineButton(
                        text = "Teardown Resources",
                        onClick = onTeardownResources,
                        isLoading = uiState.isTearingDown,
                        testTag = "iac_teardown_button"
                    )
                }
            }
        }

        // 2. Terraform Modules in Current Environment
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Active Terraform Modules • ${uiState.selectedEnvironment.displayName}",
                    color = HighContrastWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                envModules.forEach { mod ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DeepCharcoalBg)
                            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "module.${mod.moduleName}",
                                color = ElectricCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${mod.provider} • Terraform ${mod.terraformVersion} • ${mod.replicas} replicas (${mod.cpuCores} vCPU / ${mod.memoryGb}GB)",
                                color = MutedSlate,
                                fontSize = 12.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = String.format(Locale.US, "$%.2f/mo", mod.monthlyCostUsd),
                                color = HighContrastWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                            Text(
                                text = mod.status,
                                color = if (mod.status == "TERMINATED") CrimsonDanger else EmeraldSafe,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 3. GitOps Declarative Manifests Preview (gitops/deployment.yaml & service.yaml)
        val gitopsFiles = BlueprintTemplates.allFiles.filter { it.path.startsWith("gitops/") }
        gitopsFiles.forEach { file ->
            Surface(
                color = SlateCardSurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = file.path,
                        color = ElectricCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JetBrainsMonoFontFamily
                    )
                    Text(
                        text = file.description,
                        color = MutedSlate,
                        fontSize = 12.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DeepCharcoalBg)
                            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = file.code,
                            color = HighContrastWhite,
                            fontSize = 11.sp,
                            fontFamily = JetBrainsMonoFontFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PipelinesScreen(
    uiState: DevOpsUiState,
    pipelines: List<PipelineRunEntity>,
    onCommitShaChange: (String) -> Unit,
    onBranchChange: (String) -> Unit,
    onTriggerPipeline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Trigger Pipeline Control & Part 3 Contract #2 Inspector
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("pipeline_trigger_card")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "CI/CD Automation & Security Execution Engine",
                    color = HighContrastWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Endpoint Contract: POST /api/v1/pipeline/trigger",
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontFamily = JetBrainsMonoFontFamily
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.commitShaInput,
                        onValueChange = onCommitShaChange,
                        label = { Text("Commit SHA", color = MutedSlate) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = HighContrastWhite,
                            unfocusedTextColor = HighContrastWhite,
                            focusedContainerColor = DeepCharcoalBg,
                            unfocusedContainerColor = DeepCharcoalBg
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = uiState.branchInput,
                        onValueChange = onBranchChange,
                        label = { Text("Git Branch", color = MutedSlate) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = HighContrastWhite,
                            unfocusedTextColor = HighContrastWhite,
                            focusedContainerColor = DeepCharcoalBg,
                            unfocusedContainerColor = DeepCharcoalBg
                        ),
                        modifier = Modifier.width(150.dp)
                    )
                }

                CyanActionButton(
                    text = "Trigger Pipeline (${uiState.selectedEnvironment.displayName})",
                    onClick = onTriggerPipeline,
                    isLoading = uiState.isPipelineRunning,
                    icon = Icons.Default.PlayArrow,
                    testTag = "pipelines_screen_trigger_button"
                )

                // Request & Response Contract Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepCharcoalBg)
                        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    val resp = uiState.lastPipelineTriggerResponse
                    Text(
                        text = """
// Response Payload (200 OK):
{
  "pipeline_id": "${resp.pipeline_id}",
  "status": "${resp.status}",
  "timestamp": "${resp.timestamp}"
}
                        """.trimIndent(),
                        color = ElectricCyan,
                        fontSize = 12.sp,
                        fontFamily = JetBrainsMonoFontFamily
                    )
                }
            }
        }

        // 2. Persisted Room DB Pipeline Runs History
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Pipeline Execution History (${pipelines.size} runs persisted in Room DB)",
                    color = HighContrastWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                pipelines.forEach { run ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DeepCharcoalBg)
                            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = run.pipelineId,
                                    color = ElectricCyan,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = JetBrainsMonoFontFamily
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ElectricCyanDim)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = run.environment,
                                        color = ElectricCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (run.status == "PASSED") EmeraldDim else ElectricCyanDim
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = run.status,
                                    color = if (run.status == "PASSED") EmeraldSafe else AmberWarning,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = run.currentStage,
                            color = HighContrastWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        LinearProgressIndicator(
                            progress = { run.progressPercent / 100f },
                            color = if (run.status == "PASSED") EmeraldSafe else ElectricCyan,
                            trackColor = SlateBorder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )

                        Text(
                            text = run.logsSummary,
                            color = MutedSlate,
                            fontSize = 11.sp,
                            fontFamily = JetBrainsMonoFontFamily
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Commit: ${run.commitSha.take(10)} (${run.branch}) • Actor: ${run.triggeredByRole}",
                                color = MutedSlate,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${run.durationSeconds}s • ${DevOpsRepository.formatTimestamp(run.timestamp)}",
                                color = MutedSlate,
                                fontSize = 11.sp,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                        }
                    }
                }
            }
        }
    }
}
