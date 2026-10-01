package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DevOpsEnterpriseColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = DeepCharcoalBg,
    primaryContainer = DarkerTeal,
    onPrimaryContainer = HighContrastWhite,
    secondary = EmeraldSafe,
    onSecondary = DeepCharcoalBg,
    secondaryContainer = SlateBorder,
    onSecondaryContainer = HighContrastWhite,
    tertiary = VioletAccent,
    onTertiary = HighContrastWhite,
    background = DeepCharcoalBg,
    onBackground = HighContrastWhite,
    surface = SlateCardSurface,
    onSurface = HighContrastWhite,
    surfaceVariant = SlateBorder,
    onSurfaceVariant = MutedSlate,
    error = CrimsonDanger,
    onError = PureWhite,
    outline = SlateBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DevOpsEnterpriseColorScheme,
        typography = Typography,
        content = content
    )
}
