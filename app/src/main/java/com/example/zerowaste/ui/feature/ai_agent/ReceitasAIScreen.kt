package com.example.zerowaste.ui.feature.ai_agent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zerowaste.ui.theme.Green2
import com.example.zerowaste.ui.theme.Green3
import com.example.zerowaste.ui.theme.Green4
import com.example.zerowaste.ui.theme.Green5
import com.example.zerowaste.ui.theme.Green6
import com.example.zerowaste.ui.feature.inventory.InventarioViewModel
import com.example.zerowaste.ui.theme.Gray

/**
 * Ecrã Receitas AI da aplicação.
 * Dedicado à geração de receitas através de
 * Inteligência Artificial. Lê o inventário atual do utilizador e sugere
 * formas de utilizar os ingredientes disponíveis para evitar o desperdício
 *
 * @param viewModel ViewModel responsável pela apresentação da receita AI
 */
@Composable
fun ReceitasAIScreen(viewModel: InventarioViewModel = viewModel())
{

    //Para ecrãs mais pequenos, a necessidade de se fazer scroll
    val scrollState = rememberScrollState()

    //Variável para o título da página
    val coresGradient = listOf(Green6, Green3, Green4, Green5)

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
            text = "AI Chef",
            style = MaterialTheme.typography.headlineLarge.copy(
                brush = Brush.linearGradient(colors = coresGradient)
            ),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "What's in your kitchen?",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Let our AI analyze your inventory and suggest a good, magical, zero-waste recipe from scratch!",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        ElevatedButton(
            onClick = { viewModel.gerarReceita() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),

            enabled = !viewModel.aCarregarReceita,
            colors = ButtonDefaults.buttonColors(
                containerColor = Green2,
                disabledContainerColor = Green2.copy(alpha = 0.5f)
            )
        ) {
            Text(
                text = if (viewModel.aCarregarReceita) "Thinking..." else "Generate Magic Recipe!",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (viewModel.receitaGerada.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Green3.copy(alpha = 0.2f))
            )
            {
                Text(
                    text = viewModel.receitaGerada,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
