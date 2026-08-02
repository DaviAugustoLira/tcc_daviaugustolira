package br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.LoginEffect
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.LoginIntent
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.LoginState
import br.edu.utfpr.pb.tcc_daviaugustolira.admin.login.presentation.viewmodel.LoginViewModel
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.domain.auth.AdminAuthError
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.INavigator
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.navigation.Screen
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.resource.Resources
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.Color
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontSize
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontWeight
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.components.CustomTextField
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreenRoute(navigator: INavigator) {
    val viewModel: LoginViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToAdminHome -> navigator.navigateClearingBackStack(Screen.AdminHome)
            }
        }
    }

    LoginScreen(
        state = state,
        onEmailChanged = { viewModel.onIntent(LoginIntent.EmailChanged(it)) },
        onPasswordChanged = { viewModel.onIntent(LoginIntent.PasswordChanged(it)) },
        onSubmit = { viewModel.onIntent(LoginIntent.Submit) },
        onNavigateBack = { navigator.navigateBack() },
    )
}

@Composable
fun LoginScreen(
    state: LoginState = LoginState(),
    onEmailChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    val hapticFeedback = LocalHapticFeedback.current
    LaunchedEffect(state.error) {
        if (state.error != null) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(
        containerColor = Color.BACKGROUND_20,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .padding(bottom = 20.dp)
                    .clip(shape = RoundedCornerShape(size = 25.dp))
                    .fillMaxSize()
                    .background(Color.BACKGROUND_10),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LoginBackButton(onNavigateBack)
            Spacer(modifier = Modifier.size(25.dp))
            LoginBrandHeader()
            Spacer(modifier = Modifier.size(50.dp))
            LoginInstructions()
            Spacer(modifier = Modifier.size(12.dp))
            LoginFormFields(
                email = state.email,
                password = state.password,
                onEmailChanged = onEmailChanged,
                onPasswordChanged = onPasswordChanged,
            )
            state.error?.let { LoginErrorMessage(it) }
            Spacer(modifier = Modifier.size(18.dp))
            LoginSubmitButton(isSubmitting = state.isSubmitting, onSubmit = onSubmit)
        }
    }
}

@Composable
private fun LoginBackButton(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.Start,
    ) {
        Row(
            modifier =
                Modifier
                    .heightIn(min = 48.dp)
                    .clickable(onClickLabel = "Voltar", role = Role.Button, onClick = onNavigateBack)
                    .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Image(painterResource(Resources.Icons.ArrowLeftCircle), contentDescription = null)
            Text(fontWeight = FontWeight.MEDIUM, text = "Voltar")
        }
    }
}

@Composable
private fun LoginBrandHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Bem Vindo ao", fontSize = FontSize.MEDIUM, fontWeight = FontWeight.BOLD)
        Image(
            modifier = Modifier.width(273.dp).height(112.dp),
            painter = painterResource(Resources.Images.Logo),
            contentDescription = "Navegação indoor acessível",
        )
    }
}

@Composable
private fun LoginInstructions() {
    Text(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
        textAlign = TextAlign.Center,
        fontSize = FontSize.MEDIUM,
        fontWeight = FontWeight.BOLD,
        text = "Você está na tela de Login para o painel de ADMIN",
    )
    Spacer(modifier = Modifier.size(50.dp))
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        fontSize = FontSize.MEDIUM,
        fontWeight = FontWeight.MEDIUM,
        text = "Informe seus dados",
    )
}

@Composable
private fun LoginFormFields(
    email: String,
    password: String,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
) {
    CustomTextField(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .semantics { contentDescription = "Usuário, e-mail administrativo" },
        label = "Usuário",
        value = email,
        onValueChange = onEmailChanged,
        keyboardType = KeyboardType.Email,
    )
    Spacer(modifier = Modifier.size(18.dp))
    CustomTextField(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
        label = "Senha",
        value = password,
        onValueChange = onPasswordChanged,
        keyboardType = KeyboardType.Password,
        visualTransformation = PasswordVisualTransformation(),
    )
}

@Composable
private fun LoginErrorMessage(error: AdminAuthError) {
    Spacer(modifier = Modifier.size(12.dp))
    Text(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .semantics { liveRegion = LiveRegionMode.Assertive },
        textAlign = TextAlign.Center,
        fontSize = FontSize.SMALL,
        fontWeight = FontWeight.MEDIUM,
        text = errorMessage(error),
    )
}

@Composable
private fun LoginSubmitButton(
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
) {
    Button(
        onClick = onSubmit,
        enabled = !isSubmitting,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                contentColor = Color.BACKGROUND_10,
            ),
        modifier =
            Modifier
                .clip(RoundedCornerShape(size = 24.dp))
                .background(
                    brush = Brush.horizontalGradient(colors = listOf(Color.ORANGE_10, Color.ORANGE_20)),
                ).semantics {
                    contentDescription = if (isSubmitting) "Entrando, aguarde" else "Entrar no painel administrativo"
                },
    ) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 32.dp),
            fontWeight = FontWeight.SEMIBOLD,
            fontSize = FontSize.MEDIUM,
            text = if (isSubmitting) "Entrando..." else "Entrar",
        )
    }
}

private fun errorMessage(error: AdminAuthError): String =
    when (error) {
        AdminAuthError.EmptyFields -> "Preencha usuário e senha para continuar."
        AdminAuthError.InvalidCredentials -> "Usuário ou senha inválidos."
        AdminAuthError.NetworkUnavailable -> "Sem conexão com a internet. Verifique sua rede e tente novamente."
        is AdminAuthError.Unknown -> "Não foi possível entrar agora. Tente novamente em instantes."
    }
