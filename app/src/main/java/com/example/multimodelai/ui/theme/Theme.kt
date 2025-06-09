package com.example.multimodelai.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Primary60,
    onPrimary = Neutral99,
    primaryContainer = Primary20,
    onPrimaryContainer = Primary80,
    
    secondary = Secondary60,
    onSecondary = Neutral99,
    secondaryContainer = Secondary20,
    onSecondaryContainer = Secondary80,
    
    tertiary = Tertiary60,
    onTertiary = Neutral99,
    tertiaryContainer = Tertiary20,
    onTertiaryContainer = Tertiary80,
    
    background = Neutral10,
    onBackground = Neutral90,
    surface = Neutral10,
    onSurface = Neutral90,
    surfaceVariant = Neutral20,
    onSurfaceVariant = Neutral80,
    
    error = Error80,
    onError = Neutral10,
    errorContainer = Error40,
    onErrorContainer = Error80,
    
    outline = Neutral60,
    outlineVariant = Neutral40,
    scrim = Neutral0,
    inverseSurface = Neutral95,
    inverseOnSurface = Neutral20,
    inversePrimary = Primary40
)

private val LightColorScheme = lightColorScheme(
    primary = Primary40,
    onPrimary = Neutral99,
    primaryContainer = Primary80,
    onPrimaryContainer = Primary20,
    
    secondary = Secondary40,
    onSecondary = Neutral99,
    secondaryContainer = Secondary80,
    onSecondaryContainer = Secondary20,
    
    tertiary = Tertiary40,
    onTertiary = Neutral99,
    tertiaryContainer = Tertiary80,
    onTertiaryContainer = Tertiary20,
    
    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = Neutral40,
    
    error = Error40,
    onError = Neutral99,
    errorContainer = Error80,
    onErrorContainer = Error20,
    
    outline = Neutral60,
    outlineVariant = Neutral80,
    scrim = Neutral0,
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,
    inversePrimary = Primary80
)

@Composable
fun MultiModelAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+ but disabled for consistent branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}