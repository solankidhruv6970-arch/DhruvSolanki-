package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val StudioDarkColorScheme = darkColorScheme(
  primary = StudioBlue,
  onPrimary = Color(0xFF003258),
  primaryContainer = Color(0xFF00497D),
  onPrimaryContainer = Color(0xFFD1E4FF),
  secondary = StudioCyan,
  onSecondary = Color(0xFF00363D),
  secondaryContainer = Color(0xFF004F58),
  onSecondaryContainer = Color(0xFF9EEFFD),
  tertiary = StudioPurple,
  onTertiary = Color(0xFF381E72),
  background = StudioDarkBg,
  onBackground = StudioTextPrimaryDark,
  surface = StudioDarkSurface,
  onSurface = StudioTextPrimaryDark,
  surfaceVariant = StudioDarkSurfaceElevated,
  onSurfaceVariant = StudioTextSecondaryDark,
  outline = StudioDarkBorder,
  outlineVariant = StudioDarkBorderLight
)

val StudioLightColorScheme = lightColorScheme(
  primary = StudioBlueDark,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFD1E4FF),
  onPrimaryContainer = Color(0xFF001D36),
  secondary = Color(0xFF006874),
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF9EEFFD),
  onSecondaryContainer = Color(0xFF001F24),
  tertiary = Color(0xFF6B4EA2),
  onTertiary = Color.White,
  background = StudioLightBg,
  onBackground = StudioTextPrimaryLight,
  surface = StudioLightSurface,
  onSurface = StudioTextPrimaryLight,
  surfaceVariant = StudioLightSurfaceElevated,
  onSurfaceVariant = StudioTextSecondaryLight,
  outline = StudioLightBorder,
  outlineVariant = StudioLightBorderLight
)

@Composable
fun AIStudioTheme(
  darkTheme: Boolean = true,
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) StudioDarkColorScheme else StudioLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = StudioTypography,
    content = content
  )
}
