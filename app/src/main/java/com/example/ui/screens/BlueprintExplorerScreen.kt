package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.BlueprintTemplates
import com.example.domain.model.CostSimulationContract
import com.example.domain.model.MetricsSummaryContract
import com.example.ui.theme.DeepCharcoalBg
import com.example.ui.theme.ElectricCyan
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
fun BlueprintExplorerScreen(
    uiState: DevOpsUiState,
    metrics: MetricsSummaryContract,
    costSim: CostSimulationContract,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val files = BlueprintTemplates.allFiles
    var selectedFileIndex by remember { mutableIntStateOf(0) }
    var copiedFeedback by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Live Part 3 Core API Endpoint Contracts Inspector
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("api_contracts_inspector_card")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Part 3: Live FastAPI OpenAPI Endpoint Contracts",
                    color = HighContrastWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Real-time JSON payloads generated from active ${uiState.selectedEnvironment.displayName} Room Database state:",
                    color = MutedSlate,
                    fontSize = 12.sp
                )

                val summaryJson = """
// 1. GET /api/v1/metrics/summary
{
  "monthly_cost": ${String.format(Locale.US, "%.2f", metrics.monthly_cost)},
  "cost_status": "${metrics.cost_status}",
  "security_pass": ${metrics.security_pass},
  "critical_vulnerabilities": ${metrics.critical_vulnerabilities},
  "deployment_frequency_per_day": ${metrics.deployment_frequency_per_day},
  "pipeline_success_rate_percentage": ${metrics.pipeline_success_rate_percentage}
}

// 2. POST /api/v1/pipeline/trigger
{
  "pipeline_id": "${uiState.lastPipelineTriggerResponse.pipeline_id}",
  "status": "${uiState.lastPipelineTriggerResponse.status}",
  "timestamp": "${uiState.lastPipelineTriggerResponse.timestamp}"
}

// 3. GET /api/v1/infrastructure/cost-simulation
{
  "current_infra_cost": ${String.format(Locale.US, "%.2f", costSim.current_infra_cost)},
  "proposed_infra_cost": ${String.format(Locale.US, "%.2f", costSim.proposed_infra_cost)},
  "cost_difference": ${String.format(Locale.US, "%.2f", costSim.cost_difference)},
  "budget_violation": ${costSim.budget_violation}
}
                """.trimIndent()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepCharcoalBg)
                        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = summaryJson,
                        color = EmeraldSafe,
                        fontSize = 12.sp,
                        fontFamily = JetBrainsMonoFontFamily
                    )
                }
            }
        }

        // 2. Full-Stack Enterprise Blueprint Source Code Explorer (React + FastAPI + GitOps)
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("blueprint_code_explorer_card")
        ) {
            val activeFile = files[selectedFileIndex]
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "devops-corporate-platform/ Reference Codebase",
                            color = HighContrastWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Production React + TypeScript + Tailwind, Python FastAPI, and Kubernetes GitOps files",
                            color = MutedSlate,
                            fontSize = 12.sp
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText(activeFile.path, activeFile.code))
                            copiedFeedback = "Copied ${activeFile.path}!"
                        },
                        border = BorderStroke(1.dp, ElectricCyan),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("copy_blueprint_code_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Code",
                            tint = ElectricCyan
                        )
                        Text(
                            text = copiedFeedback ?: "Copy File",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    files.forEachIndexed { idx, artifact ->
                        val selected = idx == selectedFileIndex
                        Box(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selected) ElectricCyan else DeepCharcoalBg)
                                .border(1.dp, if (selected) ElectricCyan else SlateBorder, RoundedCornerShape(6.dp))
                                .clickable {
                                    selectedFileIndex = idx
                                    copiedFeedback = null
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("blueprint_file_tab_$idx"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = artifact.path,
                                color = if (selected) DeepCharcoalBg else HighContrastWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                        }
                    }
                }

                Text(
                    text = "${activeFile.language} — ${activeFile.description}",
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
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
                        text = activeFile.code,
                        color = HighContrastWhite,
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMonoFontFamily
                    )
                }
            }
        }
    }
}
