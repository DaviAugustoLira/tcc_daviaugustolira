package br.edu.utfpr.pb.tcc_daviaugustolira.home.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.INavigator
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.Screen
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.resource.Resources
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.Color
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontSize
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontWeight
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeScreenRoute(navigator: INavigator) {
    HomeScreen(
        onNavigateToSearch = {
            navigator.navigate(Screen.Search)
        },
        onNavigateToLoginAdmin = {
            navigator.navigate(Screen.Login)
        },
        onNavigateToBeaconDebug = {
            navigator.navigate(Screen.BeaconDebug)
        },
    )
}

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit = {},
    onNavigateToExplorer: () -> Unit = {},
    onNavigateToLoginAdmin: () -> Unit = {},
    onNavigateToBeaconDebug: () -> Unit = {},
) {
    Scaffold(
        containerColor = Color.BACKGROUND_20,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .padding(
                        bottom = 20.dp,
                    ).clip(
                        shape = RoundedCornerShape(size = 25.dp),
                    ).fillMaxSize()
                    .background(Color.BACKGROUND_10),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                // Entrada temporária pro spike de leitura de beacons — sem isso a tela de
                // debug não é alcançável fora de preview. Ver plano do spike de BLE.
                Button(
                    contentPadding =
                        PaddingValues(
                            vertical = 4.dp,
                            horizontal = 8.dp,
                        ),
                    onClick = onNavigateToBeaconDebug,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.BACKGROUND_30,
                            contentColor = Color.BLACK_ABSOLUTE,
                            disabledContainerColor = Color.BACKGROUND_30,
                            disabledContentColor = Color.BLACK_ABSOLUTE,
                        ),
                    border =
                        BorderStroke(
                            1.dp,
                            Color.BLACK_ABSOLUTE,
                        ),
                    shape = RoundedCornerShape(size = 10.dp),
                    modifier =
                        Modifier
                            .semantics {
                                contentDescription = "Debug de beacons"
                            },
                ) {
                    Text(
                        fontWeight = FontWeight.REGULAR,
                        fontSize = FontSize.SMALL,
                        text = "Debug de beacons",
                    )
                }
                Spacer(
                    modifier =
                        Modifier
                            .size(8.dp),
                )
                Button(
                    contentPadding =
                        PaddingValues(
                            vertical = 4.dp,
                            horizontal = 8.dp,
                        ),
                    onClick = onNavigateToLoginAdmin,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.BACKGROUND_30,
                            contentColor = Color.BLACK_ABSOLUTE,
                            disabledContainerColor = Color.BACKGROUND_30,
                            disabledContentColor = Color.BLACK_ABSOLUTE,
                        ),
                    border =
                        BorderStroke(
                            1.dp,
                            Color.BLACK_ABSOLUTE,
                        ),
                    shape = RoundedCornerShape(size = 10.dp),
                    modifier =
                        Modifier
                            .shadow(
                                shape = RoundedCornerShape(size = 5.dp),
                                elevation = 10.dp,
                                ambientColor = Color.BLACK_ABSOLUTE.copy(alpha = 0.3f),
                                spotColor = Color.BLACK_ABSOLUTE.copy(alpha = 0.8f),
                            ).clip(shape = RoundedCornerShape(size = 10.dp)),
                ) {
                    Image(
                        painterResource(Resources.Icons.Lock),
                        contentDescription = null,
                    )
                    Spacer(
                        modifier =
                            Modifier
                                .size(4.dp),
                    )
                    Text(
                        fontWeight = FontWeight.REGULAR,
                        fontSize = FontSize.SMALL,
                        text = "Painel administrativo",
                    )
                }
            }
            Spacer(
                modifier =
                    Modifier
                        .size(100.dp),
            )
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier =
                        Modifier
                            .semantics {
                                heading()
                            },
                    text = "Bem vindo ao Navegação indoor acessível",
                    fontSize = FontSize.SMALL,
                    fontWeight = FontWeight.SEMIBOLD,
                )
                Image(
                    modifier =
                        Modifier
                            .width(273.dp)
                            .height(112.dp),
                    painter = painterResource(Resources.Images.Logo),
                    contentDescription = null,
                )
            }

            Spacer(
                modifier =
                    Modifier
                        .size(50.dp),
            )
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                textAlign = TextAlign.Center,
                fontSize = FontSize.MEDIUM,
                fontWeight = FontWeight.BOLD,
                text = "Escolha uma opção para começar",
            )

            Spacer(
                modifier =
                    Modifier
                        .size(25.dp),
            )
            Button(
                onClick = onNavigateToSearch,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        contentColor = Color.BACKGROUND_10,
                    ),
                modifier =
                    Modifier
                        .clip(
                            RoundedCornerShape(size = 24.dp),
                        ).background(
                            brush =
                                Brush.horizontalGradient(
                                    colors =
                                        listOf(
                                            Color.ORANGE_10,
                                            Color.ORANGE_20,
                                        ),
                                ),
                        ),
            ) {
                Text(
                    modifier =
                        Modifier
                            .padding(
                                vertical = 4.dp,
                                horizontal = 32.dp,
                            ),
                    fontWeight = FontWeight.SEMIBOLD,
                    fontSize = FontSize.MEDIUM,
                    text = "Buscar destino",
                )
            }
            Spacer(
                modifier =
                    Modifier
                        .size(25.dp),
            )
            Button(
                onClick = onNavigateToExplorer,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        contentColor = Color.BACKGROUND_10,
                    ),
                modifier =
                    Modifier
                        .clip(
                            RoundedCornerShape(size = 24.dp),
                        ).background(
                            brush =
                                Brush.horizontalGradient(
                                    colors =
                                        listOf(
                                            Color.ORANGE_10,
                                            Color.ORANGE_20,
                                        ),
                                ),
                        ),
            ) {
                Text(
                    modifier =
                        Modifier
                            .padding(
                                vertical = 4.dp,
                                horizontal = 32.dp,
                            ),
                    fontWeight = FontWeight.SEMIBOLD,
                    fontSize = FontSize.MEDIUM,
                    text = "Exploração livre",
                )
            }
        }
    }
}
