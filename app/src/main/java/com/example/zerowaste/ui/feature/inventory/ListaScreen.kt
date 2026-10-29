package com.example.zerowaste.ui.feature.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.zerowaste.ui.theme.Green1
import com.example.zerowaste.ui.theme.Green2
import com.example.zerowaste.ui.theme.Green3

/**
 * Ecrã do inventário do utilizador.
 * Responsável por apresentar todos os produtos atualmente disponíveis
 * do utilizador. Inclui filtros de localização ou ordenação por data de validade
 *
 * @param navController
 * @param viewModel ViewModel que fornece a lista de todos os produtos disponíveis
 * consoante os filtros aplicados
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    navController: NavController,
    viewModel: InventarioViewModel = viewModel()
)
{
    //Variável para o registo de todos os produtos do utilizador
    val listaCompleta by viewModel.produtos.collectAsState()
    //Variável para a lista com os filtros aplicados
    val listaPronta by viewModel.produtosSelect.collectAsState()

    //Variável que permite mostrar apenas produtos que ainda podem ser
    //usados, ou seja, que continuam disponíveis. Os produtos marcados
    //como CONSUMIDO ou LIXO, não serão apresentados.
    val listaParaExibir = listaPronta.filter { it.estado == "DISPONIVEL" }

    //Variável para ler a lista completa de produtos disponíveis
    val locais = listOf("All") + listaCompleta.map { it.local }.distinct().filter { it.isNotBlank() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    )
    {
        Text(
            text = "Places",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
            color = Green1
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(locais) { local ->
                FilterChip(
                    selected = viewModel.localSelect == local,
                    onClick = { viewModel.atualizarLocal(local) },
                    label = { Text(local) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(
                "Your Items",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                color = Green1
            )

            TextButton(
                onClick = { viewModel.alternarOrdenacao() }
            )
            {
                Icon(
                    Icons.AutoMirrored.Filled.Sort,
                    contentDescription = "Sort",
                    modifier = Modifier.padding(end = 4.dp),
                    tint = Green2
                )

                Text(if (viewModel.ordenarPorValidade) "Sorted by Date" else "Sort by Date", color = Green2)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(listaParaExibir) { produto ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { navController.navigate("informacoes/${produto.id}") },
                    colors = CardDefaults.cardColors(containerColor = Green3.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    )
                    {
                        Text(
                            text = produto.nome,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Validade: ${produto.dataValidade}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Quantidade: ${produto.quantidade}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
