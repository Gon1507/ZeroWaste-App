package com.example.zerowaste.ui.feature.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.zerowaste.model.EstadoProduto
import com.example.zerowaste.ui.theme.Chestnut
import com.example.zerowaste.ui.theme.Green1
import com.example.zerowaste.ui.theme.Green2
import com.example.zerowaste.ui.theme.Green3
import com.example.zerowaste.ui.theme.Green4
import com.example.zerowaste.ui.theme.Red
import com.example.zerowaste.ui.theme.Terracota
import com.example.zerowaste.ui.theme.White

/**
 * Ecrã de Informações de um produto.
 * Responsável por permitir ao utilizador visualizar a informação
 * completa de um produto em específico, selecionado a partir da lista
 * do seu inventário, e realizar alterações aos estados dos produtos
 * consoante os consome, desperdiça, ou se quiser apagar um.
 *
 * @param produtoID ID do produto a ser visualizar
 * @param navController Controller de navegação usado para voltar ao ecrã anterior, a lista
 * @param viewModel ViewModel que contém a lista de produtos atualizada em tempo real
 */

@Composable
fun InformacaoProdutoScreen(
    produtoID: String,
    navController: NavController,
    viewModel: InventarioViewModel = viewModel()
)
{
    //Lista de produtos do inventário do utilizador.
    //Se um produto for alterado, atualiza automaticamente
    val listaProdutos by viewModel.produtos.collectAsState()
    val produto = remember(listaProdutos, produtoID) {
        listaProdutos.find { it.id == produtoID }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }

        if (produto == null)
        {
            Text("Produto Not Found.")
        }
        else
        {
            if (produto.imagemURL.isNotEmpty())
            {
                AsyncImage(
                    model = produto.imagemURL,
                    contentDescription = "Photo",
                    modifier = Modifier.size(150.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = produto.nome,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        Icon(imageVector = Icons.Default.Numbers,
                            contentDescription = "Quantity",
                            tint = Green4,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(text = "Quantity",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = produto.quantidade,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        Icon(imageVector = Icons.Default.DateRange,
                            contentDescription = "Expires",
                            tint = Green2,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Expires",
                                style = MaterialTheme.typography.labelMedium,
                            )
                            Text(
                                text = produto.dataValidade,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Location",
                            tint = Green3,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Location",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = produto.local,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = "Category",
                            tint = Green1,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Category",
                                style = MaterialTheme.typography.labelMedium,
                            )
                            Text(
                                text = produto.categoria,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.atualizarProdutoConsLixo(produtoID, EstadoProduto.CONSUMIDO.name)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Chestnut),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CONSUMED", color = White, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        viewModel.atualizarProdutoConsLixo(produtoID, EstadoProduto.LIXO.name)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Terracota),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("TRASH", color = White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    viewModel.apagarProduto(produtoID)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Red),
                border = androidx.compose.foundation.BorderStroke(1.dp, Red),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.DeleteOutline, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("DELETE", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
