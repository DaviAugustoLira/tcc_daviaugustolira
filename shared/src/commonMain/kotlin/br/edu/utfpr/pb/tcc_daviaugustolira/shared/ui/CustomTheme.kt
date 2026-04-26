package br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    content: @Composable () -> Unit
){
    MaterialTheme(
        typography = AppTypography()
    ){
        content()
    }
}