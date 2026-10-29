package com.example.zerowaste.ui.feature.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zerowaste.model.EstadoProduto
import com.example.zerowaste.ui.theme.Gray
import com.example.zerowaste.ui.theme.Green1
import com.example.zerowaste.ui.theme.Red
import com.example.zerowaste.ui.feature.inventory.InventarioViewModel
import java.time.LocalDate

/**
 * Ecrã das Estatísticas do utilizador.
 * Apresenta-se uma pequena análise visual de desperdício vs
 * consumo alimentar do utilizador, baseando-se no histórico
 * total de produtos alterados, tal para CONSUMIDO ou para LIXO.
 *
 * @param viewModel View Model que vai permitir gerir as estatísticas
 */

@Composable
fun EstatisticasScreen(viewModel: InventarioViewModel = viewModel())
{
    //Variável para guardar a lista total dos produtos processados
    val dados by viewModel.estatisticas.collectAsState()

    //Variáveis para se filtrar os produtos por CONSUMIDO e LIXO e a sua conta total
    val consumidos = dados.count {it.estado == EstadoProduto.CONSUMIDO.name }
    val lixo = dados.count {it.estado == EstadoProduto.LIXO.name }
    val total = consumidos + lixo

    //Variáveis para cálculo de percentagens
    val percentagemConsumido = if(total > 0){consumidos.toFloat() / total} else{0f}
    val percentagemLixo = if(total > 0){lixo.toFloat() / total} else{0f}

    //Para as estatísticas, temos de saber o mês e o seu nome
    val dataAtual = LocalDate.now()
    val nomeMes = dataAtual.month.name

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Text(
            text = "$nomeMes Stats",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineLarge,
            color = Green1
        )

        Spacer(modifier = Modifier.height(40.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Green1.copy(alpha = 0.15f)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                )
                {
                    Text(
                        "Saved",
                        fontWeight = FontWeight.Bold,
                        color = Green1
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "$consumidos",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Green1
                    )
                    Text(
                        "$percentagemConsumido%",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray
                    )
                }

                VerticalDivider(modifier = Modifier.height(100.dp).padding(horizontal = 8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                )
                {
                    Text(
                        "Wasted", fontWeight = FontWeight.Bold,
                        color = Red
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "$lixo",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Red
                    )
                    Text(
                        "$percentagemLixo%",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray
                    )
                }
            }
        }
    }
}
