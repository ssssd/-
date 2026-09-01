package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun FocusFlowTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accentTheme: AccentTheme = AccentTheme.INDIGO_PURPLE,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        accentTheme == AccentTheme.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> {
            darkColorScheme(
                primary = accentTheme.primary,
                secondary = accentTheme.secondary,
                tertiary = accentTheme.tertiary,
                background = DeepSlateBackground,
                surface = DeepSlateSurface,
                surfaceVariant = DeepSlateSurfaceVariant,
                outline = Color(0x33FFFFFF),
                outlineVariant = Color(0x1AFFFFFF),
                onPrimary = Color(0xFF1E1B2E),
                onSecondary = Color(0xFF1C1B1F),
                onTertiary = Color.White,
                onBackground = TextPrimaryDark,
                onSurface = TextPrimaryDark,
                onSurfaceVariant = TextSecondaryDark
            )
        }
        else -> {
            lightColorScheme(
                primary = accentTheme.primary,
                secondary = accentTheme.secondary,
                tertiary = accentTheme.tertiary,
                background = Color(0xFFF8FAFC),
                surface = Color.White,
                surfaceVariant = LightSurfaceVariant,
                onPrimary = Color.White,
                onSecondary = Color.White,
                onTertiary = Color.White,
                onBackground = TextPrimaryLight,
                onSurface = TextPrimaryLight,
                onSurfaceVariant = TextSecondaryLight
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
