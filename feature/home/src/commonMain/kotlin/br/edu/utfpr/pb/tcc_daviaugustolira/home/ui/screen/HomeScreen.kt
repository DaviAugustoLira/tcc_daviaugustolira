package br.edu.utfpr.pb.tcc_daviaugustolira.home.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.resource.Resources
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.Color
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontSize
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontWeight
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeScreenRoute(

){
    HomeScreen()
}

@Composable
fun HomeScreen() {
    Scaffold (
        containerColor = Color.BACKGROUND_20
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(
                    bottom = 20.dp
                )
                .clip(
                    shape = RoundedCornerShape(size = 25.dp)
                )
                .fillMaxSize()
                .background(Color.BACKGROUND_10)
        ){
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ){
                Row(
                    modifier = Modifier
                        .border(
                            width = 0.5.dp,
                            color = Color.BLACK_ABSOLUTE,
                            shape = RoundedCornerShape(size = 10.dp)
                        )
                        .shadow(
                            shape = RoundedCornerShape(size = 10.dp),
                            elevation = 5.dp,
                            ambientColor = Color.BLACK_ABSOLUTE.copy(alpha = 0.8f),
                            spotColor = Color.BLACK_ABSOLUTE.copy(alpha = 0.5f)
                        )
                        .clip(shape = RoundedCornerShape(size = 10.dp))
                        .background(Color.BACKGROUND_30)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Image(
                        painterResource(Resources.Icons.Lock),
                        contentDescription = "Cadeado"
                    )
                    Text(
                        fontWeight = FontWeight.REGULAR,
                        text = "Painel Admin"
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .size(100.dp)
            )
            Column (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "Bem Vindo ao",
                    fontSize = FontSize.MEDIUM,
                    fontWeight = FontWeight.BOLD
                )
                Image(
                    modifier = Modifier
                        .width(273.dp)
                        .height(112.dp),
                    painter = painterResource(Resources.Images.Logo),
                    contentDescription = "Navegação indoor acessível"
                )
            }

            Spacer(
                modifier = Modifier
                    .size(200.dp)
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                textAlign = TextAlign.Center,
                fontSize = FontSize.MEDIUM,
                fontWeight = FontWeight.BOLD,
                text = "Toque duas vezes em qualquer local da tela para navegar até " +
                        "um destino, ou mantenha pressionado para explorar locais próximos"
            )
        }
    }
}