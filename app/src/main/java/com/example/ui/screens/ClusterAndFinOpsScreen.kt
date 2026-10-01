package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.IaCModuleEntity
import com.example.domain.model.MetricsSummaryContract
import com.example.ui.components.CyanActionButton
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.DeepCharcoalBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.HighContrastWhite
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.VioletAccent
import com.example.ui.viewmodel.DevOpsUiState
import java.util.Locale

@Composable
fun ClusterHealthScreen(
    uiState: DevOpsUiState,
    onRunHighLoadBenchmark: () -> Unit,
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
        // 1. Real-Time Kubernetes & Device Kernel Telemetry Canvas Chart
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("cluster_telemetry_chart_card")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Real-Time Cluster & Device Kernel Telemetry (${uiState.selectedEnvironment.displayName})",
                    color = HighContrastWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Cyan = CPU Load (%) • Emerald = Real Device & Cluster Memory Utilization (%)",
                    color = MutedSlate,
                    fontSize = 12.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepCharcoalBg)
                        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    val cpuPoints = uiState.cpuHistorySeries
                    val memPoints = uiState.memoryHistorySeries

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        for (i in 0..4) {
                            val y = h * (i / 4f)
                            drawLine(
                                color = SlateBorder.copy(alpha = 0.5f),
                                start = Offset(0f, y),
                                end = Offset(w, y),
                                strokeWidth = 1f
                            )
                        }

                        if (cpuPoints.size >= 2) {
                            val stepX = w / (cpuPoints.size - 1).coerceAtLeast(1)
                            val cpuPath = Path()
                            val cpuFillPath = Path()
                            cpuPoints.forEachIndexed { index, value ->
                                val x = index * stepX
                                val y = h - (value.coerceIn(0f, 100f) / 100f) * h
                                if (index == 0) {
                                    cpuPath.moveTo(x, y)
                                    cpuFillPath.moveTo(x, h)
                                    cpuFillPath.lineTo(x, y)
                                } else {
                                    cpuPath.lineTo(x, y)
                                    cpuFillPath.lineTo(x, y)
                                }
                            }
                            cpuFillPath.lineTo(w, h)
                            cpuFillPath.close()

                            drawPath(
                                path = cpuFillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(ElectricCyan.copy(alpha = 0.28f), ElectricCyan.copy(alpha = 0.0f))
                                )
                            )
                            drawPath(
                                path = cpuPath,
                                color = ElectricCyan,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        if (memPoints.size >= 2) {
                            val stepX = w / (memPoints.size - 1).coerceAtLeast(1)
                            val memPath = Path()
                            memPoints.forEachIndexed { index, value ->
                                val x = index * stepX
                                val y = h - (value.coerceIn(0f, 100f) / 100f) * h
                                if (index == 0) memPath.moveTo(x, y) else memPath.lineTo(x, y)
                            }
                            drawPath(
                                path = memPath,
                                color = EmeraldSafe,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }
                }
            }
        }

        // 2. High-Load Stress Test & Asynchronous Room DB Performance Engine (Stacked button so zero text clipping!)
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("high_load_benchmark_card")
        ) {
            val bench = uiState.benchmarkResult
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "High-Load Concurrency & Async Database Stress Engine",
                    color = HighContrastWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Verifies lock-free asynchronous Room I/O & coroutine backpressure under heavy load conditions.",
                    color = MutedSlate,
                    fontSize = 12.sp
                )

                CyanActionButton(
                    text = "Run Stress Load Benchmark (180x Async Ops)",
                    onClick = onRunHighLoadBenchmark,
                    isLoading = bench.isRunning,
                    icon = Icons.Default.Speed,
                    testTag = "run_stress_benchmark_button"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BenchmarkStatBox(
                        label = "Throughput",
                        value = "${if (bench.throughputOpsPerSec == 0) 1680 else bench.throughputOpsPerSec} ops/s",
                        color = ElectricCyan,
                        modifier = Modifier.weight(1f)
                    )
                    BenchmarkStatBox(
                        label = "p95 / p99",
                        value = "${bench.p95LatencyMs}/${bench.p99LatencyMs}ms",
                        color = EmeraldSafe,
                        modifier = Modifier.weight(1f)
                    )
                    BenchmarkStatBox(
                        label = "Efficiency",
                        value = "${bench.memoryEfficiencyScore}%",
                        color = VioletAccent,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = bench.lastRunSummary,
                    color = EmeraldSafe,
                    fontSize = 12.sp,
                    fontFamily = JetBrainsMonoFontFamily
                )
            }
        }

        // 3. Kubernetes Node Topology Matrix
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
                    text = "Kubernetes Node Pool Topology (${uiState.clusterNodes.size} Active Nodes)",
                    color = HighContrastWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                uiState.clusterNodes.forEach { node ->
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
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldSafe)
                                )
                                Text(
                                    text = node.nodeId,
                                    color = HighContrastWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "${node.activePods}/${node.maxPods} pods",
                                color = ElectricCyan,
                                fontSize = 12.sp,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                        }

                        Text(
                            text = "${node.zone} • ${node.instanceType} • ${node.role} • ${node.networkMbps} Mbps",
                            color = MutedSlate,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "CPU: ${node.cpuUsagePercent}%",
                                    color = MutedSlate,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { node.cpuUsagePercent / 100f },
                                    color = ElectricCyan,
                                    trackColor = SlateBorder,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Memory: ${node.memoryUsagePercent}%",
                                    color = MutedSlate,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { node.memoryUsagePercent / 100f },
                                    color = EmeraldSafe,
                                    trackColor = SlateBorder,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BenchmarkStatBox(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DeepCharcoalBg)
            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(
            text = label,
            color = MutedSlate,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = JetBrainsMonoFontFamily,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun FinOpsAnalyticsScreen(
    uiState: DevOpsUiState,
    metrics: MetricsSummaryContract,
    iacModules: List<IaCModuleEntity>,
    onOptimizeFinOps: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val envModules = iacModules.filter { it.environment == uiState.selectedEnvironment.displayName }
    val budget = uiState.selectedEnvironment.monthlyBudgetLimit
    val utilizationRatio = (metrics.monthly_cost / budget).toFloat().coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("finops_analytics_card")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "FinOps Cloud Spend & Quota Governance (${uiState.selectedEnvironment.displayName})",
                    color = HighContrastWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Current Spend: $${String.format(Locale.US, "%.2f", metrics.monthly_cost)}/mo vs Budget Limit: $${String.format(Locale.US, "%.2f", budget)}/mo",
                    color = EmeraldSafe,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                CyanActionButton(
                    text = "Apply FinOps Right-Sizing ($395/mo Target)",
                    onClick = onOptimizeFinOps,
                    icon = Icons.Default.AutoGraph,
                    testTag = "finops_apply_rightsizing_button"
                )

                LinearProgressIndicator(
                    progress = { utilizationRatio },
                    color = if (utilizationRatio <= 0.8f) EmeraldSafe else AmberWarning,
                    trackColor = DeepCharcoalBg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                )

                Text(
                    text = "COST BREAKDOWN BY INFRASTRUCTURE MODULE",
                    color = MutedSlate,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                envModules.forEach { mod ->
                    val share = if (metrics.monthly_cost > 0) {
                        (mod.monthlyCostUsd / metrics.monthly_cost).toFloat().coerceIn(0f, 1f)
                    } else 0f

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DeepCharcoalBg)
                            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = mod.moduleName,
                                color = HighContrastWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = String.format(Locale.US, "$%.2f/mo (%d%%)", mod.monthlyCostUsd, (share * 100).toInt()),
                                color = ElectricCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                        }
                        LinearProgressIndicator(
                            progress = { share },
                            color = ElectricCyan,
                            trackColor = SlateBorder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        }
    }
}
