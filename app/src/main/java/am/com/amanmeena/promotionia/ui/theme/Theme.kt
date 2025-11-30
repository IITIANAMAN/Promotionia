package am.com.amanmeena.promotionia.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// PURE WHITE ON BLACK (Dark Mode)
private val DarkColorScheme = darkColorScheme(

    // Soft matte blacks (NOT glossy)
    background = Color(0xFF121212),
    surface = Color(0xFF1A1A1A),

    // Primary should NOT be white (too bright)
    primary = Color(0xFFE0E0E0),      // soft white
    onPrimary = Color.Black,

    // Text colors
    onBackground = Color(0xFFEAEAEA), // soft white
    onSurface = Color(0xFFEAEAEA),

    // Subtle secondary text
    onSurfaceVariant = Color(0xFFB0B0B0),

    secondary = Color(0xFFE0E0E0),
    onSecondary = Color.Black,

    tertiary = Color(0xFFE0E0E0),
    onTertiary = Color.Black
)


// PURE BLACK ON WHITE (Light Mode)
private val LightColorScheme = lightColorScheme(
    // Buttons, primary actions
    primary = Color.Black,
    onPrimary = Color.White,

    // Backgrounds
    background = Color.White,
    surface = Color.White,

    // Text colors
    onBackground = Color.Black,
    onSurface = Color.Black,

    // Secondary text (slightly dim black)
    onSurfaceVariant = Color(0xFF444444),

    secondary = Color.Black,
    onSecondary = Color.White,
)

@Composable
fun PromotioniaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,   // MUST be OFF for pure B&W theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}