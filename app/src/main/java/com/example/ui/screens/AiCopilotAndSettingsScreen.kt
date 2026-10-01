package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyanActionButton
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
import com.example.ui.theme.VioletDim
import com.example.ui.viewmodel.DevOpsUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiCopilotScreen(
    uiState: DevOpsUiState,
    onSelectModel: (String) -> Unit,
    onPromptChange: (String) -> Unit,
    onAskCopilot: (String?) -> Unit,
    onApplyFinOpsOptimization: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var copiedId by remember { mutableStateOf<String?>(null) }

    val quickPrompts = listOf(
        "Optimize FinOps ($395/mo)" to "Analyze our current $395/mo cloud spend and recommend concrete AWS EKS, RDS, and ElastiCache right-sizing optimizations.",
        "Audit CI/CD & Zero-CVE" to "Audit our CI/CD pipeline security posture, SAST/Trivy scanning, and RBAC least-privilege controls for SOC2 Type II compliance.",
        "Generate K8s HPA YAML" to "Generate a production Kubernetes HorizontalPodAutoscaler and NetworkPolicy YAML for devops-core-engine.",
        "Diagnose Cluster & RAM" to "Analyze our live Kubernetes node pool CPU/Memory telemetry and async Room database p99 latency under load."
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Gemini AI Copilot Control Header Card
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_copilot_header_card")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DevOps Core AI Architect & SecOps Copilot",
                            color = HighContrastWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Powered by Gemini API • Context-aware of live ${uiState.selectedEnvironment.displayName} telemetry, FinOps & RBAC state",
                            color = MutedSlate,
                            fontSize = 12.sp
                        )
                    }
                }

                // Model Selector Pills (gemini-3.5-flash vs gemini-3.1-pro-preview)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val models = listOf(
                        "gemini-3.5-flash" to "gemini-3.5-flash (Fast SRE)",
                        "gemini-3.1-pro-preview" to "gemini-3.1-pro-preview (Deep IaC)"
                    )
                    models.forEach { (modelId, label) ->
                        val selected = uiState.selectedGeminiModel == modelId
                        Box(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) ElectricCyan else DeepCharcoalBg)
                                .border(1.dp, if (selected) ElectricCyan else SlateBorder, RoundedCornerShape(8.dp))
                                .clickable { onSelectModel(modelId) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("select_model_$modelId"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (selected) DeepCharcoalBg else HighContrastWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = JetBrainsMonoFontFamily,
                                maxLines = 1
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { openExternalUrl(context, "https://aistudio.google.com/apikey") },
                        border = BorderStroke(1.dp, VioletAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("open_aistudio_apikey_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = VioletAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Get Gemini API Key",
                            color = HighContrastWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }

                // 1-Tap Context Diagnostic Chips
                Text(
                    text = "1-TAP LIVE CONTEXT DIAGNOSTICS",
                    color = MutedSlate,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickPrompts.forEachIndexed { idx, (shortTitle, fullPrompt) ->
                        OutlinedButton(
                            onClick = { onAskCopilot(fullPrompt) },
                            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("ai_quick_chip_$idx")
                        ) {
                            Text(
                                text = shortTitle,
                                color = ElectricCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Custom Prompt Box + Send Button
                OutlinedTextField(
                    value = uiState.aiPromptInput,
                    onValueChange = onPromptChange,
                    label = { Text("Ask about Terraform, Kubernetes HPA, FastAPI, FinOps, or CVEs...", color = MutedSlate) },
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
                        .testTag("ai_prompt_input")
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CyanActionButton(
                        text = "Ask AI Copilot",
                        onClick = { onAskCopilot(null) },
                        isLoading = uiState.isAiGenerating,
                        icon = Icons.AutoMirrored.Filled.Send,
                        testTag = "ask_ai_copilot_button"
                    )
                    OutlinedButton(
                        onClick = onApplyFinOpsOptimization,
                        border = BorderStroke(1.dp, EmeraldSafe),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("ai_apply_finops_button")
                    ) {
                        Text(
                            text = "Apply AI Right-Sizing ($395/mo)",
                            color = EmeraldSafe,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }

                // Security Notice Banner (Required by gemini-api & android-secret-management skills)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepCharcoalBg)
                        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Security Note: Configure GEMINI_API_KEY via the AI Studio Secrets panel. Prototype APKs using BuildConfig should not be shared publicly as keys can be extracted if decompiled.",
                        color = MutedSlate,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 2. AI Copilot Conversation Stream
        uiState.aiMessages.forEach { msg ->
            Surface(
                color = if (msg.isUser) DeepCharcoalBg else SlateCardSurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (msg.isUser) ElectricCyan.copy(alpha = 0.5f) else SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = msg.promptTitle,
                                color = if (msg.isUser) ElectricCyan else EmeraldSafe,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${msg.modelTag} • ${msg.timestampFormatted}",
                                color = MutedSlate,
                                fontSize = 11.sp,
                                fontFamily = JetBrainsMonoFontFamily,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                copyToClipboard(context, msg.promptTitle, msg.content)
                                copiedId = msg.id
                            },
                            border = BorderStroke(1.dp, SlateBorder),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy AI Response",
                                tint = ElectricCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (copiedId == msg.id) "Copied" else "Copy",
                                color = ElectricCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DeepCharcoalBg)
                            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = msg.content,
                            color = HighContrastWhite,
                            fontSize = 13.sp,
                            fontFamily = JetBrainsMonoFontFamily
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsAndGitHubScreen(
    uiState: DevOpsUiState,
    onCustomHttpProbeUrlChange: (String) -> Unit,
    onRefreshLiveProbes: () -> Unit,
    onGitHubRepoSlugChange: (String) -> Unit,
    onFetchGitHubRepo: (Boolean) -> Unit,
    onCustomWsUrlChange: (String) -> Unit,
    onConnectCustomWs: () -> Unit,
    onToggleAutoBackup: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var copyBanner by remember { mutableStateOf<String?>(null) }

    val githubRepoDescription =
        "Enterprise DevOps, FinOps & Cloud Security Control Plane featuring real-time WebSocket telemetry, Terraform IaC cost simulation, CI/CD pipeline automation, Role-Based Access Control (RBAC), SHA-256 immutable audit logging, automated state backups, and SOC2/ISO27001/CIS compliance monitoring."

    val gitCommandsSnippet = """
git init
git add .
git commit -m "feat(devops-core): enterprise FinOps, CI/CD, RBAC, SHA-256 audit, live probes & Gemini AI"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/devops-core-platform.git
git push -u origin main
    """.trimIndent()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Real-World Live Cloud & Endpoint HTTP Latency Prober
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("live_endpoint_prober_card")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Real-World Live HTTP / Cloud Latency Prober",
                    color = HighContrastWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Executes real HTTPS network requests to verify GitHub Status, Cloudflare Edge, PyPI FastAPI releases, Kubernetes OCI Registry, and any custom URL.",
                    color = MutedSlate,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = uiState.customHttpProbeUrl,
                    onValueChange = onCustomHttpProbeUrlChange,
                    label = { Text("Custom Live HTTP/HTTPS Endpoint to Probe", color = MutedSlate) },
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
                        .testTag("custom_http_probe_input")
                )

                CyanActionButton(
                    text = "Ping Real Cloud Endpoints Now",
                    onClick = onRefreshLiveProbes,
                    isLoading = uiState.isProbingLiveEndpoints,
                    icon = Icons.Default.Refresh,
                    testTag = "refresh_live_probes_button"
                )

                uiState.liveEndpointProbes.forEach { probe ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DeepCharcoalBg)
                            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                            .clickable { openExternalUrl(context, probe.url) }
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
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
                                        .background(if (probe.isHealthy) EmeraldSafe else AmberWarning)
                                )
                                Text(
                                    text = probe.name,
                                    color = HighContrastWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "${probe.latencyMs} ms",
                                color = ElectricCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                        }
                        Text(
                            text = probe.liveDetail,
                            color = if (probe.isHealthy) EmeraldSafe else AmberWarning,
                            fontSize = 12.sp,
                            fontFamily = JetBrainsMonoFontFamily
                        )
                        Text(
                            text = "${probe.url} • Checked at ${probe.checkedAtFormatted}",
                            color = MutedSlate,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // 2. Real-World Live GitHub Repository & Commit Sync Engine
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("live_github_sync_card")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Real-Time GitHub Repository & Commit SHA Connector",
                    color = HighContrastWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Connect any public GitHub repository (owner/repo) via the live GitHub REST API (https://api.github.com) and sync its latest real Commit SHA directly into your CI/CD Pipeline.",
                    color = MutedSlate,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = uiState.githubRepoSlugInput,
                    onValueChange = onGitHubRepoSlugChange,
                    label = { Text("GitHub Repository (e.g. tiangolo/fastapi or kubernetes/kubernetes)", color = MutedSlate) },
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
                        .testTag("github_repo_slug_input")
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CyanActionButton(
                        text = "Fetch & Sync Real Commit SHA",
                        onClick = { onFetchGitHubRepo(true) },
                        isLoading = uiState.isFetchingGitHubRepo,
                        icon = Icons.Default.Sync,
                        testTag = "sync_github_commit_button"
                    )
                    OutlinedButton(
                        onClick = {
                            val slug = uiState.githubRepoSlugInput.trim().ifBlank { "tiangolo/fastapi" }
                            openExternalUrl(context, "https://github.com/$slug")
                        },
                        border = BorderStroke(1.dp, ElectricCyan),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Open Repo in Browser",
                            color = ElectricCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }

                uiState.liveGitHubRepo?.let { repo ->
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
                                text = repo.fullName,
                                color = ElectricCyan,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                            Text(
                                text = "★ ${repo.stars} • Branch: ${repo.defaultBranch}",
                                color = EmeraldSafe,
                                fontSize = 12.sp,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                        }
                        Text(
                            text = repo.description,
                            color = HighContrastWhite,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Latest Commit: ${repo.latestCommitSha.take(12)} by ${repo.latestCommitAuthor}",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontFamily = JetBrainsMonoFontFamily
                        )
                        Text(
                            text = "\"${repo.latestCommitMessage}\"",
                            color = MutedSlate,
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // 3. GitHub Commit & Push Helper + Direct Repository Description Copy
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("github_push_helper_card")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Commit & Push to GitHub Center",
                    color = HighContrastWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tip: In Google AI Studio, click the GitHub / Export icon in the top-right toolbar to push this project directly to your GitHub account, or use the CLI commands below.",
                    color = MutedSlate,
                    fontSize = 12.sp
                )

                if (copyBanner != null) {
                    Text(
                        text = copyBanner!!,
                        color = EmeraldSafe,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CyanActionButton(
                        text = "Copy Git Push Commands",
                        onClick = {
                            copyToClipboard(context, "Git Commands", gitCommandsSnippet)
                            copyBanner = "Copied Git commit & push commands to clipboard!"
                        },
                        icon = Icons.Default.ContentCopy,
                        testTag = "copy_git_commands_button"
                    )
                    OutlinedButton(
                        onClick = {
                            copyToClipboard(context, "GitHub Description", githubRepoDescription)
                            copyBanner = "Copied GitHub repository description to clipboard!"
                        },
                        border = BorderStroke(1.dp, ElectricCyan),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "Copy GitHub Description",
                            color = ElectricCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                    OutlinedButton(
                        onClick = { openExternalUrl(context, "https://github.com/new") },
                        border = BorderStroke(1.dp, EmeraldSafe),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = EmeraldSafe,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Open github.com/new",
                            color = EmeraldSafe,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepCharcoalBg)
                        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = gitCommandsSnippet,
                        color = EmeraldSafe,
                        fontSize = 12.sp,
                        fontFamily = JetBrainsMonoFontFamily
                    )
                }
            }
        }

        // 4. Essential Real-World DevOps, Cloud & Security Portals (1-Tap Browser Launch)
        Surface(
            color = SlateCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("real_world_links_card")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Real-World Cloud, SecOps & Documentation Links",
                    color = HighContrastWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap any resource below to open the live external console or documentation in your browser:",
                    color = MutedSlate,
                    fontSize = 12.sp
                )

                uiState.essentialLinks.forEach { link ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DeepCharcoalBg)
                            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                            .clickable { openExternalUrl(context, link.url) }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ElectricCyanDim)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = link.badge,
                                        color = ElectricCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = JetBrainsMonoFontFamily
                                    )
                                }
                                Text(
                                    text = link.title,
                                    color = HighContrastWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = link.subtitle,
                                color = MutedSlate,
                                fontSize = 12.sp
                            )
                            Text(
                                text = link.url,
                                color = ElectricCyan,
                                fontSize = 11.sp,
                                fontFamily = JetBrainsMonoFontFamily,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Open ${link.title}",
                            tint = ElectricCyan
                        )
                    }
                }
            }
        }

        // 5. Custom Live WebSocket Endpoint Connector
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
                    text = "Live WebSocket Endpoint & Automated Backup Settings",
                    color = HighContrastWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = uiState.customWsUrl,
                    onValueChange = onCustomWsUrlChange,
                    label = { Text("Remote WebSocket Endpoint (ws:// or wss://)", color = MutedSlate) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = HighContrastWhite,
                        unfocusedTextColor = HighContrastWhite,
                        focusedContainerColor = DeepCharcoalBg,
                        unfocusedContainerColor = DeepCharcoalBg
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CyanActionButton(
                        text = "Connect Live WebSocket",
                        onClick = onConnectCustomWs,
                        icon = Icons.Default.Public,
                        testTag = "connect_custom_ws_button"
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Automated Pre-Teardown & Scheduled State Backups",
                        color = HighContrastWhite,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = uiState.isAutoBackupEnabled,
                        onCheckedChange = onToggleAutoBackup,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DeepCharcoalBg,
                            checkedTrackColor = EmeraldSafe
                        )
                    )
                }
            }
        }
    }
}

fun openExternalUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
}
