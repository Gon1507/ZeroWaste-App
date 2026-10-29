package com.example.zerowaste.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.zerowaste.ui.feature.inventory.AdicionarScreen
import com.example.zerowaste.ui.feature.statistics.EstatisticasScreen
import com.example.zerowaste.ui.feature.inventory.InformacaoProdutoScreen
import com.example.zerowaste.ui.feature.inventory.ListaScreen

import com.example.zerowaste.ui.feature.auth.LoginScreen
import com.example.zerowaste.ui.feature.profile.PerfilScreen
import com.example.zerowaste.ui.feature.ai_agent.PrincipalScreen
import com.example.zerowaste.ui.feature.ai_agent.ReceitasAIScreen

/**
 * Ficheiro responsável pela navegação na aplicação.
 */

object Rotas {
    const val LOGIN = "login"
    const val PRINCIPAL = "principal"
    const val ADICIONAR = "adicionar"
    const val LISTA = "lista"
    const val ESTATISTICAS = "estatisticas"
    const val AIRECEITAS = "aireceitas"
    const val INFORMACOES = "informacoes"
    const val PERFIL = "perfil"
}

/**
 * Função que permite, então, navegar pela aplicação.
 * Também apresenta a NavHost, o menu, na parte inferior da aplicação
 */
@Composable
fun Rotas() {
    val navController = rememberNavController()

    //Variáveis para monitorizar a rota atual e garantir que
    //a barra de navegação inferior reage corretamente ao ser clicada
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rotaAtual = navBackStackEntry?.destination?.route

    //A barra de navegação inferior aparece em todos os ecrãs menos
    //se o utilizador estiver presente no ecrã de login
    Scaffold(
        bottomBar =
            {
                if (rotaAtual != Rotas.LOGIN)
                {
                    NavBarInferior(navController, rotaAtual)
                }
            }
    ) { paddingValues ->
        //Define a primeira rota da aplicação como Login
        NavHost(
            navController = navController,
            startDestination = Rotas.LOGIN,
            modifier = Modifier.padding(paddingValues)
        )
        {
            composable(Rotas.LOGIN)
            {
                LoginScreen(
                    onLoginSucesso =
                        {
                            navController.navigate(Rotas.PRINCIPAL)
                            {
                                popUpTo(Rotas.LOGIN) { inclusive = true }
                            }
                        }
                )
            }

            composable(Rotas.PRINCIPAL)
            {
                PrincipalScreen()
            }

            composable(Rotas.ADICIONAR)
            {
                AdicionarScreen()
            }

            composable(Rotas.AIRECEITAS)
            {
                ReceitasAIScreen()
            }

            composable(Rotas.LISTA)
            {
                //Novos argumentos, pois a navegação acontece
                //dentro da página da lista dos produtos
                ListaScreen(navController = navController)
            }

            composable(Rotas.ESTATISTICAS)
            {
                EstatisticasScreen()
            }

            //O composable da página de informações do produto é diferente
            //dos outros pelo facto deste precisar de um argumento de ID do
            //produto, para o podermos observar
            composable("${Rotas.INFORMACOES}/{produtoId}")
            { backStackEntry ->
                val idProduto = backStackEntry.arguments?.getString("produtoId") ?: ""
                InformacaoProdutoScreen(
                    produtoID = idProduto,
                    navController = navController
                )
            }

            composable(Rotas.PERFIL)
            {
                PerfilScreen(navController = navController)
            }
        }
    }

}

/**
 * Barra de navegação inferior.
 * Permite a transição rápida entre páginas e funcionalidades da aplicação.
 *
 * @param navController Controller de navegação utilizado trocar de ecrã ao clicar nos ícones.
 * @param rotaAtual A rota que representa o ecrã atualmente visível.
 */
@Composable
fun NavBarInferior(
    navController: NavController,
    rotaAtual: String?
)
{
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
    {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Cottage, "Home") },
            label = { Text("Home") },
            selected = rotaAtual == Rotas.PRINCIPAL,
            onClick = { navController.navigate(Rotas.PRINCIPAL) }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.QrCodeScanner, "Add Products") },
            label = { Text("Scan") },
            selected = rotaAtual == Rotas.ADICIONAR,
            onClick = { navController.navigate(Rotas.ADICIONAR) }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.FormatListNumbered, "Inventory") },
            label = { Text("Inventory") },
            selected = rotaAtual == Rotas.LISTA,
            onClick = { navController.navigate(Rotas.LISTA) }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.SmartToy, "AI Recipes") },
            label = { Text("Recipes") },
            selected = rotaAtual == Rotas.AIRECEITAS,
            onClick = { navController.navigate(Rotas.AIRECEITAS) }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.BarChart, "Stats") },
            label = { Text("Stats") },
            selected = rotaAtual == Rotas.ESTATISTICAS,
            onClick = { navController.navigate(Rotas.ESTATISTICAS) }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, "Profile") },
            label = { Text("Profile") },
            selected = rotaAtual == Rotas.PERFIL,
            onClick = { navController.navigate(Rotas.PERFIL) }
        )


    }
}
