package br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

@Composable
fun AppTypography(): Typography {
    val fontFamily = appFontFamily()


    return Typography(
        displayLarge = TextStyle(fontFamily = fontFamily),
        displayMedium = TextStyle(fontFamily = fontFamily),
        displaySmall = TextStyle(fontFamily = fontFamily),

        headlineLarge = TextStyle(fontFamily = fontFamily),
        headlineMedium = TextStyle(fontFamily = fontFamily),
        headlineSmall = TextStyle(fontFamily = fontFamily),

        titleLarge = TextStyle(fontFamily = fontFamily),
        titleMedium = TextStyle(fontFamily = fontFamily),
        titleSmall = TextStyle(fontFamily = fontFamily),

        bodyLarge = TextStyle(fontFamily = fontFamily),
        bodyMedium = TextStyle(fontFamily = fontFamily),
        bodySmall = TextStyle(fontFamily = fontFamily),

        labelLarge = TextStyle(fontFamily = fontFamily),
        labelMedium = TextStyle(fontFamily = fontFamily),
        labelSmall = TextStyle(fontFamily = fontFamily),
    )
}