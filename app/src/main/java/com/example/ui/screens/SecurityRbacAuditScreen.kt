package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AuditLogEntity
import com.example.data.local.BackupSnapshotEntity
import com.example.data.local.ComplianceControlEntity
import com.example.data.repository.DevOpsRepository
import com.example.domain.model.Permission
import com.example.domain.model.RbacRole
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
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.VioletDim
import com.example.ui.viewmodel.DevOpsUiState
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SecurityRbacAuditScreen(
    uiState: DevOpsUiState,
    auditLogs: List<AuditLogEntity>,
    backups: List<BackupSnapshotEntity>,
    complianceControls: List<ComplianceControlEntity>,
    onSelectRole: (RbacRole) -> Unit,
    onRunComplianceScan: () -> Unit,
    onCreateBackup: () -> Unit,
    onRestoreBackup: (BackupSnapshotEntity) -> Unit,
    onToggleAutoBackup: (Boolean) -> Unit,
    onAuditSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableIntStateOf(0) }
    val subTabs = listOf(
        "RBAC Roles",
        "Compliance",
        "Audit Logs (${auditLogs.size})",
        "Backups (${backups.size})"
    )
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            subTabs.forEachIndexed { index, title ->
                val isSelected = selectedSubTab == index
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ElectricCyan else SlateCardSurface)
                        .border(1.dp, if (isSelected) ElectricCyan else SlateBorder, RoundedCornerShape(8.dp))
                        .clickable { selectedSubTab = index }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("security_subtab_$index"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) DeepCharcoalBg else HighContrastWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }

        when (selectedSubTab) {
            0 -> RbacGovernancePanel(
                currentRole = uiState.currentRole,
                onSelectRole = onSelectRole
            )
            1 -> ComplianceMonitoringPanel(
                controls = complianceControls,
                onRunComplianceScan = onRunComplianceScan
            )
            2 -> ImmutableAuditLogPanel(
                auditLogs = auditLogs,
                searchQuery = uiState.auditSearchQuery,
                onSearchChange = onAuditSearchChange
            )
            3 -> AutomatedBackupsPanel(
                uiState = uiState,
                backups = backups,
                onCreateBackup = onCreateBackup,
                onRestoreBackup = onRestoreBackup,
                onToggleAutoBackup = onToggleAutoBackup
            )
        }
    }
}

@Composable
private fun RbacGovernancePanel(
    currentRole: RbacRole,
    onSelectRole: (RbacRole) -> Unit
) {
    Surface(
        color = SlateCardSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rbac_governance_panel")
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Role-Based Access Control (RBAC) Policy Engine",
                color = HighContrastWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Select an active session role below to test live least-privilege policy enforcement across Pipelines, IaC, FinOps, and Backups.",
                color = MutedSlate,
                fontSize = 13.sp
            )

            RbacRole.entries.forEach { role ->
                val isSelected = role == currentRole
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) VioletDim else DeepCharcoalBg)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) ElectricCyan else SlateBorder,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectRole(role) }
                        .padding(16.dp)
                        .testTag("rbac_card_${role.badgeCode}"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = role.roleName,
                                color = if (isSelected) ElectricCyan else HighContrastWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SlateBorder)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = role.badgeCode,
                                    color = PureWhite,
                                    fontSize = 10.sp,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    maxLines = 1
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${role.clearanceLevel} • ${role.permissions.size}/${Permission.entries.size} permissions",
                            color = MutedSlate,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSelected) "ACTIVE" else "SWITCH",
                        color = if (isSelected) EmeraldSafe else ElectricCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            Text(
                text = "ACTIVE ROLE PERMISSIONS MATRIX (${currentRole.roleName.uppercase(Locale.US)})",
                color = MutedSlate,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Permission.entries.forEach { perm ->
                val allowed = currentRole.can(perm)
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
                            text = perm.label,
                            color = HighContrastWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = perm.code,
                            color = MutedSlate,
                            fontSize = 11.sp,
                            fontFamily = JetBrainsMonoFontFamily
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (allowed) Icons.Default.CheckCircle else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (allowed) EmeraldSafe else CrimsonDanger,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (allowed) "ALLOWED" else "DENIED",
                            color = if (allowed) EmeraldSafe else CrimsonDanger,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComplianceMonitoringPanel(
    controls: List<ComplianceControlEntity>,
    onRunComplianceScan: () -> Unit
) {
    Surface(
        color = SlateCardSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("compliance_monitoring_panel")
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Continuous Security Monitoring & Compliance Reporting",
                color = HighContrastWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "SOC2 Type II • ISO 27001 • CIS Kubernetes v1.8 • NIST 800-53 (100% Compliant)",
                color = EmeraldSafe,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            CyanActionButton(
                text = "Run Full Compliance Audit Scan",
                onClick = onRunComplianceScan,
                icon = Icons.Default.Security,
                testTag = "run_compliance_scan_button"
            )

            controls.forEach { ctrl ->
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
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ElectricCyanDim)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${ctrl.framework} • ${ctrl.controlCode}",
                                color = ElectricCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = JetBrainsMonoFontFamily,
                                maxLines = 1
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(EmeraldDim)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = ctrl.status,
                                color = EmeraldSafe,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                    Text(
                        text = ctrl.title,
                        color = HighContrastWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = ctrl.description,
                        color = MutedSlate,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ImmutableAuditLogPanel(
    auditLogs: List<AuditLogEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    val filteredLogs = auditLogs.filter {
        searchQuery.isBlank() ||
            it.action.contains(searchQuery, ignoreCase = true) ||
            it.details.contains(searchQuery, ignoreCase = true) ||
            it.actorRole.contains(searchQuery, ignoreCase = true)
    }

    Surface(
        color = SlateCardSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("immutable_audit_log_panel")
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Immutable Cryptographic Audit Trail (SHA-256 Verified)",
                color = HighContrastWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label = { Text("Filter audit events by action, actor role, or SHA-256...", color = MutedSlate) },
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
                    .testTag("audit_search_input")
            )

            filteredLogs.forEach { log ->
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
                            text = "${log.action} • ${log.environment}",
                            color = if (log.severity == "WARNING") AmberWarning else ElectricCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JetBrainsMonoFontFamily,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = DevOpsRepository.formatTimestamp(log.timestamp),
                            color = MutedSlate,
                            fontSize = 11.sp,
                            fontFamily = JetBrainsMonoFontFamily
                        )
                    }
                    Text(
                        text = log.details,
                        color = HighContrastWhite,
                        fontSize = 12.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Actor: ${log.actorRole} [${log.category}]",
                            color = MutedSlate,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "SHA-256: ${log.sha256Hash.take(12)}",
                            color = EmeraldSafe,
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
private fun AutomatedBackupsPanel(
    uiState: DevOpsUiState,
    backups: List<BackupSnapshotEntity>,
    onCreateBackup: () -> Unit,
    onRestoreBackup: (BackupSnapshotEntity) -> Unit,
    onToggleAutoBackup: (Boolean) -> Unit
) {
    Surface(
        color = SlateCardSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("automated_backups_panel")
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Automated Point-in-Time Backups & Disaster Recovery",
                color = HighContrastWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Every state snapshot is verified with a SHA-256 checksum and can restore full IaC module state in 1 click.",
                color = MutedSlate,
                fontSize = 12.sp
            )

            CyanActionButton(
                text = "Create Point-in-Time Snapshot",
                onClick = onCreateBackup,
                icon = Icons.Default.Backup,
                testTag = "create_backup_snapshot_button"
            )

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
                        text = "Automated Pre-Change & Scheduled Backups",
                        color = HighContrastWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Snapshots database state before resource teardown or major scaling",
                        color = MutedSlate,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = uiState.isAutoBackupEnabled,
                    onCheckedChange = onToggleAutoBackup,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = DeepCharcoalBg,
                        checkedTrackColor = EmeraldSafe
                    ),
                    modifier = Modifier.testTag("auto_backup_switch")
                )
            }

            backups.forEach { snap ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${snap.snapshotCode} • ${snap.environment} (${snap.status})",
                                color = ElectricCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = JetBrainsMonoFontFamily,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${snap.triggerType} • ${snap.modulesCount} modules • $${String.format(Locale.US, "%.2f", snap.totalMonthlyCostSnapshot)}/mo",
                                color = HighContrastWhite,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "SHA-256: ${snap.sha256Checksum}",
                                color = EmeraldSafe,
                                fontSize = 11.sp,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { onRestoreBackup(snap) },
                            border = BorderStroke(1.dp, ElectricCyan),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("restore_snapshot_${snap.snapshotCode}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Restore",
                                color = ElectricCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
