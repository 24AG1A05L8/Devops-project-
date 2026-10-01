package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DevOpsSection
import com.example.domain.model.EnvironmentType
import com.example.domain.model.RbacRole
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.DarkerTeal
import com.example.ui.theme.DeepCharcoalBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanDim
import com.example.ui.theme.EmeraldDim
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.HighContrastWhite
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.VioletAccent
import com.example.ui.theme.VioletDim

@Composable
fun DevOpsTopNavbar(
    selectedEnvironment: EnvironmentType,
    onEnvironmentSelected: (EnvironmentType) -> Unit,
    currentRole: RbacRole,
    onRoleSelected: (RbacRole) -> Unit,
    isCompact: Boolean,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var envDropdownExpanded by remember { mutableStateOf(false) }
    var roleDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SlateCardSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .padding(horizontal = if (isCompact) 12.dp else 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Menu Icon (on Compact) + App Logo "DevOps Core"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isCompact) {
                    IconButton(
                        onClick = onMenuClick,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("open_drawer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Navigation Drawer",
                            tint = ElectricCyan
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ElectricCyanDim)
                        .border(1.dp, ElectricCyan, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Dns,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "DevOps Core",
                    color = ElectricCyan,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("app_logo_title")
                )
            }

            // Right: Environment Selector Dropdown (+ RBAC & System Health on wide screens)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box {
                    Row(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(6.dp))
                            .background(DeepCharcoalBg)
                            .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                            .clickable { envDropdownExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("environment_selector_dropdown"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    when (selectedEnvironment) {
                                        EnvironmentType.PRODUCTION -> EmeraldSafe
                                        EnvironmentType.STAGING -> ElectricCyan
                                        EnvironmentType.DEVELOPMENT -> VioletAccent
                                    }
                                )
                        )
                        Text(
                            text = selectedEnvironment.displayName,
                            color = HighContrastWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Environment",
                            tint = MutedSlate,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = envDropdownExpanded,
                        onDismissRequest = { envDropdownExpanded = false },
                        modifier = Modifier
                            .background(SlateCardSurface)
                            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                    ) {
                        EnvironmentType.entries.forEach { env ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = env.displayName,
                                            color = if (env == selectedEnvironment) ElectricCyan else HighContrastWhite,
                                            fontWeight = if (env == selectedEnvironment) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            text = "($${env.monthlyBudgetLimit.toInt()} cap)",
                                            color = MutedSlate,
                                            fontSize = 11.sp
                                        )
                                    }
                                },
                                onClick = {
                                    envDropdownExpanded = false
                                    onEnvironmentSelected(env)
                                },
                                modifier = Modifier.testTag("env_option_${env.apiSlug}")
                            )
                        }
                    }
                }

                if (!isCompact) {
                    RbacRoleDropdownPill(
                        currentRole = currentRole,
                        expanded = roleDropdownExpanded,
                        onExpandChange = { roleDropdownExpanded = it },
                        onRoleSelected = onRoleSelected,
                        showFullRoleName = true
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(DeepCharcoalBg)
                            .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("system_health_badge")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(EmeraldSafe)
                        )
                        Text(
                            text = "All Systems Operational",
                            color = MutedSlate,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Clean 2nd bar on Compact screens so System Health & RBAC Role Selector never collide with logo
        if (isCompact) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepCharcoalBg.copy(alpha = 0.7f))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("system_health_badge"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(EmeraldSafe)
                    )
                    Text(
                        text = "All Systems Operational",
                        color = MutedSlate,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                RbacRoleDropdownPill(
                    currentRole = currentRole,
                    expanded = roleDropdownExpanded,
                    onExpandChange = { roleDropdownExpanded = it },
                    onRoleSelected = onRoleSelected,
                    showFullRoleName = false
                )
            }
        }

        HorizontalDivider(thickness = 1.dp, color = SlateBorder)
    }
}

@Composable
private fun RbacRoleDropdownPill(
    currentRole: RbacRole,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onRoleSelected: (RbacRole) -> Unit,
    showFullRoleName: Boolean
) {
    Box {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(VioletDim)
                .border(1.dp, VioletAccent.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                .clickable { onExpandChange(true) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("rbac_role_selector"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Active RBAC Role",
                tint = VioletAccent,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = if (showFullRoleName) currentRole.roleName else "RBAC: ${currentRole.badgeCode}",
                color = HighContrastWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MutedSlate,
                modifier = Modifier.size(16.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier
                .background(SlateCardSurface)
                .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
        ) {
            RbacRole.entries.forEach { role ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = "${role.roleName} [${role.badgeCode}]",
                                color = if (role == currentRole) ElectricCyan else HighContrastWhite,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = role.clearanceLevel,
                                color = MutedSlate,
                                fontSize = 11.sp
                            )
                        }
                    },
                    onClick = {
                        onExpandChange(false)
                        onRoleSelected(role)
                    },
                    modifier = Modifier.testTag("role_option_${role.badgeCode}")
                )
            }
        }
    }
}

@Composable
fun DevOpsSidebar(
    activeSection: DevOpsSection,
    onSectionSelected: (DevOpsSection) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(SlateCardSurface)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "CONTROL PLANE",
                    color = MutedSlate.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                )

                DevOpsSection.entries.forEach { section ->
                    val isActive = section == activeSection
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .background(if (isActive) SlateBorder else Color.Transparent)
                            .clickable { onSectionSelected(section) }
                            .testTag(section.testTag),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(44.dp)
                                .background(if (isActive) ElectricCyan else Color.Transparent)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = iconForSection(section),
                                contentDescription = section.title,
                                tint = if (isActive) ElectricCyan else MutedSlate,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = section.title,
                                fontSize = 14.sp,
                                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isActive) PureWhite else MutedSlate,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = DeepCharcoalBg,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EmeraldSafe)
                        )
                        Text(
                            text = "Live Cloud + Gemini AI",
                            color = HighContrastWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Real HTTP Probes • GitHub API • SHA-256 Audit",
                        color = MutedSlate,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(SlateBorder)
        )
    }
}

@Composable
fun CompactSectionTabStrip(
    activeSection: DevOpsSection,
    onSectionSelected: (DevOpsSection) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .background(SlateCardSurface)
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(DevOpsSection.entries) { section ->
            val isSelected = section == activeSection
            Row(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) SlateBorder else DeepCharcoalBg)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) ElectricCyan else SlateBorder,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onSectionSelected(section) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("tab_${section.testTag}"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = iconForSection(section),
                    contentDescription = section.title,
                    tint = if (isSelected) ElectricCyan else MutedSlate,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = section.title,
                    color = if (isSelected) PureWhite else MutedSlate,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun DevOpsMetricCard(
    title: String,
    bigValue: String,
    bigValueColor: Color,
    subLabel: String,
    subLabelColor: Color,
    badgeText: String? = null,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.testTag(testTag),
        color = SlateCardSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SlateBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = MutedSlate,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (badgeText != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(EmeraldDim)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = EmeraldSafe,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = bigValue,
                color = bigValueColor,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displayLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subLabel,
                color = subLabelColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CyanActionButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    icon: ImageVector? = Icons.Default.PlayArrow,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "cyan_btn_scale"
    )

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPressed) DarkerTeal else ElectricCyan,
            contentColor = DeepCharcoalBg
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
        modifier = modifier
            .height(48.dp)
            .scale(scale)
            .testTag(testTag)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = DeepCharcoalBg,
                strokeWidth = 2.dp,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DeepCharcoalBg,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = DeepCharcoalBg,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DangerOutlineButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPressed || isLoading) CrimsonDanger else Color.Transparent,
            contentColor = if (isPressed || isLoading) PureWhite else CrimsonDanger
        ),
        border = BorderStroke(2.dp, CrimsonDanger),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
        modifier = modifier
            .height(48.dp)
            .testTag(testTag)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = PureWhite,
                strokeWidth = 2.dp,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isPressed || isLoading) PureWhite else CrimsonDanger,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun iconForSection(section: DevOpsSection): ImageVector = when (section) {
    DevOpsSection.DASHBOARD -> Icons.Default.Dashboard
    DevOpsSection.AI_COPILOT -> Icons.Default.AutoAwesome
    DevOpsSection.INFRASTRUCTURE -> Icons.Default.Storage
    DevOpsSection.PIPELINES -> Icons.Default.AccountTree
    DevOpsSection.CLUSTER_HEALTH -> Icons.Default.Dns
    DevOpsSection.FINOPS -> Icons.Default.Analytics
    DevOpsSection.SECURITY_RBAC -> Icons.Default.Security
    DevOpsSection.BLUEPRINT -> Icons.Default.Code
    DevOpsSection.SETTINGS -> Icons.Default.Settings
}
