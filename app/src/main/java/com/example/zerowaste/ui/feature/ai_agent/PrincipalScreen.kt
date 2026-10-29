package com.example.zerowaste.ui.feature.ai_agent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zerowaste.model.EstadoProduto
import com.example.zerowaste.ui.theme.Green1
import com.example.zerowaste.ui.theme.Gray
import com.example.zerowaste.ui.theme.Green2
import com.example.zerowaste.ui.theme.Green3
import com.example.zerowaste.ui.theme.Green4
import com.example.zerowaste.ui.theme.Green5
import com.example.zerowaste.ui.theme.Green6
import com.example.zerowaste.ui.feature.auth.AuthViewModel
import com.example.zerowaste.ui.feature.inventory.InventarioViewModel
import com.example.zerowaste.ui.theme.Red

/**
 * Ecrã principal da aplicação. Apresenta streaks, neste caso, estatísticas
 * dos produtos consumidos vs. produtos desperdiçados e integra um agente de
 * Inteligência Artificial para conseguir gerar dicas diárias de sustentabilidade
 *
 * @param viewModel ViewModel responsável pela gestão do inventário e pela
 * apresentação das dicas AI
 * @param authView ViewModel responsável pelos dados da sessão do utilizador
 */
@Composable
fun PrincipalScreen(
    viewModel: InventarioViewModel = viewModel(),
    authView: AuthViewModel = viewModel())
{

    //Para ecrãs mais pequenos, a necessidade de se fazer scroll
    val scrollState = rememberScrollState()

    //Variáveis para as streaks do utilizador
    val produtos by viewModel.produtos.collectAsState()
    val totalConsumidos = produtos.count { it.estado == EstadoProduto.CONSUMIDO.name }
    val totalLixo = produtos.count { it.estado == EstadoProduto.LIXO.name }

    //Variáveis para as boas-vindas ao utilizador
    val dadosUtilizador by authView.dadosUtilizador.collectAsState()
    val primeiroNome = dadosUtilizador?.primeiroNome
    val ultimoNome = dadosUtilizador?.ultimoNome

    //Variável para a cor do nome do utilizador
    val coresGradient = listOf(Green6, Green3, Green4, Green5)

    //Variáveis para barra de progresso da streak de consumidos do utilizador
    val objetivoStreak = 25
    val progresso = (totalConsumidos % objetivoStreak) / objetivoStreak.toFloat()
    val faltam = objetivoStreak - (totalConsumidos % objetivoStreak)

    //Variáveis para barra de lixo do utilizador
    val limiteLixo = 10
    val progressoLixo = (totalLixo % limiteLixo) / limiteLixo.toFloat()
    val faltamLixo = limiteLixo - (totalLixo % limiteLixo)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 24.dp
            )
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement= Arrangement.Top
    )
    {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome To ZeroWaste!",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineLarge,
            color = Green1
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "It's so nice to have you back,",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            color = Green1,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "$primeiroNome $ultimoNome",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium.copy(
                brush = Brush.linearGradient(colors = coresGradient)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Ready to save food and money today?",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        //Streaks do utilizador
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Green3.copy(alpha = 0.2f))
        )
        {
            Column(modifier = Modifier.padding(20.dp))
            {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        text = "Consumed Streak \uD83C\uDF7D",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Green1
                    )

                    Text(
                        text = "$totalConsumidos",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = Green1
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { progresso },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Green4,
                    trackColor = Green3.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Just $faltam more to reach your next milestone!",
                    style = MaterialTheme.typography.labelMedium,
                    color = Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Red.copy(alpha = 0.05f))
        )
        {
            Column(modifier = Modifier.padding(20.dp))
            {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        text = "Wasted Items \uD83D\uDDD1\uFE0F",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )

                    Text(
                        text = "$totalLixo",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = Red.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { progressoLixo },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Red,
                    trackColor = Red.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Careful! $faltamLixo more and you'll hit your waste limit.",
                    style = MaterialTheme.typography.labelMedium,
                    color = Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Need some daily inspiration?",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Green1,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Let our AI suggest a new way to save food and reduce waste!",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            ElevatedButton(
                onClick = { viewModel.gerarDicaDia() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !viewModel.aCarregarDica,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green2,
                    disabledContainerColor = Green2.copy(alpha = 0.5f)
                )
            )
            {
                Text(
                    text = if (viewModel.aCarregarDica) "Thinking..." else "Generate Daily Tip!",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (viewModel.dicaDia.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Green3.copy(alpha = 0.2f))
                )
                {
                    Text(
                        text = viewModel.dicaDia,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Justify,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

        }
    }
}
