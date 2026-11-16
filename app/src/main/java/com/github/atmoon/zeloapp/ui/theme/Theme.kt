package com.github.atmoon.zeloapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ZeloPrimary = Color(0xFF3949AB)
val ZeloPrimaryDark = Color(0xFF1A237E)
val ZeloPrimarySoft = Color(0xFFE8EAF6)
val ZeloBackground = Color(0xFFF4F5FA)
val ZeloTextMuted = Color(0xFF6B7280)
val ZeloSuccess = Color(0xFF2E7D32)
val ZeloSuccessSoft = Color(0xFFE8F5E9)
val ZeloWarning = Color(0xFFEF6C00)
val ZeloWarningSoft = Color(0xFFFFF3E0)
val ZeloDanger = Color(0xFFC62828)
val ZeloDangerSoft = Color(0xFFFDECEC)

private val LightColors = lightColorScheme(
    primary = ZeloPrimary,
    onPrimary = Color.White,
    primaryContainer = ZeloPrimarySoft,
    onPrimaryContainer = ZeloPrimaryDark,
    background = ZeloBackground,
    surface = Color.White
)

@Composable
fun ZeloTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}