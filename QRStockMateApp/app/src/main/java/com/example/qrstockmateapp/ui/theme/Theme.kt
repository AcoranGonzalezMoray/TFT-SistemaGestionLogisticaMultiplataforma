package com.example.qrstockmateapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat




private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF121212 ),
    secondaryContainer = Color(0xFF222222),
    outline = Color(0xFF2c2c2c),
    onTertiary = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color.Black,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color.White,
    secondaryContainer = Color.White,
    outline = Color(0xfff5f6f7),
    onTertiary = Color.Gray
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

//adb shell "cmd uimode night yes"

@Composable
fun QRStockMateAppTheme(
    darkTheme: Int,
    content: @Composable () -> Unit
) {
    var darkThemeOption = when (darkTheme) {
        0 -> true
        1 -> false
        else -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (darkThemeOption) DarkColorScheme else LightColorScheme,
        /* Define typography and shapes if needed */
        content = content
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if(darkThemeOption) DarkColorScheme.secondaryContainer.toArgb() else LightColorScheme.secondaryContainer.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =!darkThemeOption
        }
    }


}
@Composable
fun isDarkMode(): Boolean {
    return  MaterialTheme.colorScheme.background.toString() ==  "Color(0.07058824, 0.07058824, 0.07058824, 1.0, sRGB IEC61966-2.1)"
}

