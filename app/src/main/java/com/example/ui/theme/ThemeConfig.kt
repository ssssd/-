package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class ThemeMode(val title: String) {
    SYSTEM("跟随系统 / System"),
    LIGHT("浅色 / Light"),
    DARK("深色 / Dark")
}

enum class AccentTheme(val title: String, val primary: Color, val secondary: Color, val tertiary: Color) {
    INDIGO_PURPLE("沉浸紫 / Immersive Lavender", Color(0xFFD0BCFF), Color(0xFFCCC2DC), Color(0xFF6366F1)),
    OCEAN_BLUE("蓝色 / Ocean Blue", Color(0xFF38BDF8), Color(0xFF60A5FA), Color(0xFF2563EB)),
    TEAL_CYAN("青色 / Cyan Teal", Color(0xFF2DD4BF), Color(0xFF14B8A6), Color(0xFF06B6D4)),
    EMERALD_GREEN("绿色 / Emerald Green", Color(0xFF34D399), Color(0xFF10B981), Color(0xFF059669)),
    SUNSET_ORANGE("橙色 / Sunset Orange", Color(0xFFFB923C), Color(0xFFF97316), Color(0xFFEA580C)),
    ROSE_PINK("粉色 / Rose Pink", Color(0xFFFB7185), Color(0xFFF43F5E), Color(0xFFE11D48)),
    RUBY_RED("红色 / Ruby Red", Color(0xFFF87171), Color(0xFFEF4444), Color(0xFFDC2626)),
    SLATE_MONO("黑白 / Slate Minimal", Color(0xFF94A3B8), Color(0xFF64748B), Color(0xFF334155)),
    DYNAMIC("动态取色 / Dynamic Material", Color(0xFFD0BCFF), Color(0xFFCCC2DC), Color(0xFF6366F1))
}

// Immersive Dark Theme tokens (#0A0A0A canvas, #1C1B1F surface, #2A2A2A surface-variant)
val DeepSlateBackground = Color(0xFF0A0A0A)
val DeepSlateSurface = Color(0xFF1C1B1F)
val DeepSlateSurfaceVariant = Color(0xFF2A2A2A)
val LightSurface = Color(0xFFF8FAFC)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val TextPrimaryDark = Color(0xFFF1F5F9)
val TextSecondaryDark = Color(0xFF94A3B8)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF64748B)

// Glassmorphism constants
val GlassCardBackgroundDark = Color(0xFF1C1B1F)
val GlassCardBorderDark = Color(0x20FFFFFF)
val GlassCardHighlight = Color(0x14FFFFFF)

// Priority colors
val PriorityLowColor = Color(0xFF34D399) // Soft emerald
val PriorityNormalColor = Color(0xFF60A5FA) // Soft blue
val PriorityHighColor = Color(0xFFFBBF24) // Warm amber
val PriorityUrgentColor = Color(0xFFF87171) // Vibrant red

// Focus indicator colors
val CyanAccent = Color(0xFF38BDF8)
val VioletAccent = Color(0xFFC084FC)
val AmberAccent = Color(0xFFFBBF24)
val EmeraldAccent = Color(0xFF34D399)
val IndigoPrimary = Color(0xFF6366F1)
val LavenderPrimary = Color(0xFFD0BCFF)

