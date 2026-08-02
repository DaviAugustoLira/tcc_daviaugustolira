package br.edu.utfpr.pb.tcc_daviaugustolira.search.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.INavigator
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.resource.Resources
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.Color
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontSize
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontWeight
import org.jetbrains.compose.resources.painterResource

@Composable
fun SearchScreenRoute(navigator: INavigator) {
    SearchScreen()
}

@Composable
fun SearchScreen() {
    Scaffold(
        containerColor = Color.BACKGROUND_20,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
        ) {
            Text(
                text = "Buscar destino por voz",
                modifier =
                    Modifier
                        .padding(top = 24.dp)
                        .semantics {
                            heading()
                        }.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = FontSize.LARGE,
                fontWeight = FontWeight.SEMIBOLD,
            )
            Column(
                modifier =
                    Modifier
                        .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Button(
                    onClick = {
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        ),
                    modifier =
                        Modifier
                            .semantics {
                                contentDescription = "Falar destino por voz"
                            }.size(200.dp)
                            .clip(
                                shape = RoundedCornerShape(size = 1000.dp),
                            ).background(
                                Brush.radialGradient(
                                    listOf(Color.ORANGE_10, Color.ORANGE_20),
                                ),
                            ),
                ) {
                    Image(
                        painter = painterResource(Resources.Icons.Microphone),
                        contentDescription = null,
                    )
                }

                Text(
                    modifier =
                        Modifier
                            .padding(top = 20.dp)
                            .padding(horizontal = 40.dp),
                    textAlign = TextAlign.Center,
                    fontSize = FontSize.MEDIUM,
                    fontWeight = FontWeight.MEDIUM,
                    text = "Toque no botão para falar o destino",
                )
            }
        }
    }
}
