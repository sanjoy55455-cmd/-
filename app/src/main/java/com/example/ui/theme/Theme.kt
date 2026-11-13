package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val StudioDarkColorScheme =
  darkColorScheme(
    primary = BroadcastCrimson,
    onPrimary = Color.White,
    primaryContainer = BroadcastCrimsonContainer,
    onPrimaryContainer = Color(0xFFFFD9DC),
    secondary = LoopEmerald,
    onSecondary = Color(0xFF002116),
    secondaryContainer = LoopEmeraldContainer,
    onSecondaryContainer = LoopEmerald,
    tertiary = CoralAccent,
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = StudioSurface,
    onSurface = TextPrimary,
    surfaceVariant = StudioSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    surfaceContainerHighest = InputWell,
    outline = OutlineSubtle,
    error = Color(0xFFFF5252),
    onError = Color.White,
  )

private val StudioLightColorScheme =
  lightColorScheme(
    primary = BroadcastCrimson,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD9),
    onPrimaryContainer = Color(0xFF410006),
    secondary = Color(0xFF00875F),
    onSecondary = Color.White,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFFD5D7E2),
  )

private val StudioShapes =
  Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
  )

@Composable
fun MultiViewTheme(
  darkTheme: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) StudioDarkColorScheme else StudioLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    shapes = StudioShapes,
    content = content,
  )
}
