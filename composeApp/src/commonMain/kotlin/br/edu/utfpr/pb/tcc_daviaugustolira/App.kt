package br.edu.utfpr.pb.tcc_daviaugustolira

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.edu.utfpr.pb.tcc_daviaugustolira.navigation.Navigation
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        Navigation()
    }
}
