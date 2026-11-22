package com.practice.trialapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = InkBlack,
    onPrimary = PureWhite,
    primaryContainer = FieldGrey,
    onPrimaryContainer = InkBlack,
    secondary = SteelGrey,
    onSecondary = PureWhite,
    secondaryContainer = FieldGrey,
    onSecondaryContainer = InkBlack,
    tertiary = SteelGrey,
    onTertiary = PureWhite,
    background = PureWhite,
    onBackground = InkBlack,
    surface = PureWhite,
    onSurface = InkBlack,
    surfaceVariant = FieldGrey,
    onSurfaceVariant = AshGrey,
    surfaceTint = PureWhite,
    surfaceContainerLowest = PureWhite,
    surfaceContainerLow = PureWhite,
    surfaceContainer = PureWhite,
    surfaceContainerHigh = PureWhite,
    surfaceContainerHighest = FieldGrey,
    inverseSurface = InkBlack,
    inverseOnSurface = PureWhite,
    outline = SilverGrey,
    outlineVariant = LineGrey,
    error = ErrorRed,
    onError = PureWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = InkBlack,
    primaryContainer = Charcoal,
    onPrimaryContainer = PureWhite,
    secondary = AshGrey,
    onSecondary = InkBlack,
    secondaryContainer = Charcoal,
    onSecondaryContainer = PureWhite,
    tertiary = AshGrey,
    onTertiary = InkBlack,
    background = InkBlack,
    onBackground = PureWhite,
    surface = Graphite,
    onSurface = PureWhite,
    surfaceVariant = Charcoal,
    onSurfaceVariant = AshGrey,
    surfaceTint = Graphite,
    surfaceContainerLowest = InkBlack,
    surfaceContainerLow = Graphite,
    surfaceContainer = Graphite,
    surfaceContainerHigh = Graphite,
    surfaceContainerHighest = Charcoal,
    inverseSurface = PureWhite,
    inverseOnSurface = InkBlack,
    outline = SteelGrey,
    outlineVariant = Charcoal,
    error = ErrorRedDark,
    onError = InkBlack
)

// soft rounded corners everywhere, like the reference design
private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun TrialAppTheme(
    //supports both dark & light, but I prefer light theme cuz looks better so only kept it enable
//    darkTheme: Boolean = isSystemInDarkTheme(),
    darkTheme: Boolean = false,

    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
