package br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
import tcc_daviaugustolira.shared.generated.resources.Res
import tcc_daviaugustolira.shared.generated.resources.Urbanist_VariableFont_wght

@Composable
fun appFontFamily(): FontFamily {
    return FontFamily(
        Font(
            Res.font.Urbanist_VariableFont_wght,
            weight = FontWeight.MEDIUM
        )
    )
}