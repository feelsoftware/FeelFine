package com.feelsoftware.feelfine.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal val colorWhite = Color(0xFFFFFFFF)
internal val colorBlack = Color(0xFF000000)
internal val colorLightPurple = Color(0xFFEDE5F0)
internal val colorLightPink = Color(0xFFFFA9C4)
internal val colorBluePurple = Color(0xFFACA8EE)
internal val colorAqua = Color(0xFF71C7D3)
internal val colorBlue = Color(0xFF7E9DF4)
internal val colorYellowGreen = Color(0xFFCBD859)
internal val colorPink = Color(0xFFE94667)
internal val colorPurple = Color(0xFFA77BB5)
internal val colorLightGrey = Color(0xFFACAFBC)
internal val colorGrey = Color(0xFF626262)

internal val LightColorScheme = lightColorScheme(
    primary = colorPurple,
    onPrimary = colorWhite,
//    primaryContainer = md_theme_light_primaryContainer,
//    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = colorLightPurple,
    onSecondary = colorGrey,
//    secondaryContainer = md_theme_light_secondaryContainer,
//    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = colorAqua,
    onTertiary = colorWhite,
//    tertiaryContainer = md_theme_light_tertiaryContainer,
//    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = colorPink,
//    errorContainer = md_theme_light_errorContainer,
    onError = colorWhite,
//    onErrorContainer = md_theme_light_onErrorContainer,
    background = colorWhite,
    onBackground = colorGrey,
    surface = colorWhite,
    onSurface = colorGrey,
//    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = colorLightGrey,
//    outline = md_theme_light_outline,
//    inverseOnSurface = md_theme_light_inverseOnSurface,
//    inverseSurface = md_theme_light_inverseSurface,
//    inversePrimary = md_theme_light_inversePrimary,
//    surfaceTint = md_theme_light_surfaceTint,
//    outlineVariant = md_theme_light_outlineVariant,
//    scrim = md_theme_light_scrim,
)

internal val DarkColorScheme = darkColorScheme(
    primary = colorPurple,
    onPrimary = colorWhite,
//    primaryContainer = md_theme_light_primaryContainer,
//    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = colorLightPurple,
    onSecondary = colorGrey,
//    secondaryContainer = md_theme_light_secondaryContainer,
//    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = colorAqua,
    onTertiary = colorWhite,
//    tertiaryContainer = md_theme_light_tertiaryContainer,
//    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = colorPink,
//    errorContainer = md_theme_light_errorContainer,
    onError = colorWhite,
//    onErrorContainer = md_theme_light_onErrorContainer,
    background = colorBlack,
    onBackground = colorWhite,
    surface = colorBlack,
    onSurface = colorGrey,
//    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = colorLightGrey,
//    outline = md_theme_light_outline,
//    inverseOnSurface = md_theme_light_inverseOnSurface,
//    inverseSurface = md_theme_light_inverseSurface,
//    inversePrimary = md_theme_light_inversePrimary,
//    surfaceTint = md_theme_light_surfaceTint,
//    outlineVariant = md_theme_light_outlineVariant,
//    scrim = md_theme_light_scrim,
)
