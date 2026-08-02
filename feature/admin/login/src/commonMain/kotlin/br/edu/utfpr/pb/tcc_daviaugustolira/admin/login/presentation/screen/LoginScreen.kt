package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.INavigator
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.Screen
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.resource.Resources
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.Color
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontSize
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontWeight
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.components.CustomTextField
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginScreenRoute(navigator: INavigator) {
    LoginScreen(
        onNavigateBack = {
            navigator.navigateBack()
        },
        onNavigateToHome = {
            navigator.navigate(Screen.Home)
        },
    )
}

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
) {
    Scaffold(
        modifier =
            Modifier
                .combinedClickable(
                    onLongClick = onNavigateToHome,
                    onClick = {},
                ),
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
                horizontalArrangement = Arrangement.Start,
            ) {
                Row(
                    modifier =
                        Modifier
                            .clickable {
                                onNavigateBack()
                            }.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Image(
                        painterResource(Resources.Icons.ArrowLeftCircle),
                        contentDescription = "Flecha para esquerda",
                    )
                    Text(
                        fontWeight = FontWeight.MEDIUM,
                        text = "Voltar",
                    )
                }
            }
            Spacer(
                modifier =
                    Modifier
                        .size(25.dp),
            )
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Bem Vindo ao",
                    fontSize = FontSize.MEDIUM,
                    fontWeight = FontWeight.BOLD,
                )
                Image(
                    modifier =
                        Modifier
                            .width(273.dp)
                            .height(112.dp),
                    painter = painterResource(Resources.Images.Logo),
                    contentDescription = "Navegação indoor acessível",
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
                text =
                    "Você está na tela de Login para o painel de ADMIN, " +
                        "se não tem acesso basta manter pressionado na tela para " +
                        "voltar a tela inicial",
            )

            Spacer(
                modifier =
                    Modifier
                        .size(50.dp),
            )
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = FontSize.MEDIUM,
                fontWeight = FontWeight.MEDIUM,
                text = "Informe seus dados",
            )

            Spacer(
                modifier =
                    Modifier
                        .size(12.dp),
            )
            CustomTextField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                label = "Usuário",
                value = "",
                onValueChange = {
                },
            )
            Spacer(
                modifier =
                    Modifier
                        .size(18.dp),
            )
            CustomTextField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                label = "Senha",
                value = "",
                onValueChange = {
                },
                keyboardType = KeyboardType.Password,
            )
            Spacer(
                modifier =
                    Modifier
                        .size(18.dp),
            )
            Button(
                onClick = { },
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
                    text = "Entrar",
                )
            }
        }
    }
}
