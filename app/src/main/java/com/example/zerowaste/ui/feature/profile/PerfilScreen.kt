package com.example.zerowaste.ui.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.zerowaste.data.repository.AuthRepository
import com.example.zerowaste.ui.navigation.Rotas
import com.example.zerowaste.ui.theme.Gray
import com.example.zerowaste.ui.theme.Green1
import com.example.zerowaste.ui.theme.Green2
import com.example.zerowaste.ui.theme.White
import com.example.zerowaste.ui.theme.Yellow
import com.example.zerowaste.ui.feature.auth.AuthViewModel
import com.example.zerowaste.ui.theme.Green4
import com.google.firebase.auth.FirebaseAuth

/**
 * Ecrã de Perfil do utilizador.
 * Responsável por exibir as informações pessoas da conta do utilizador,
 * gerir o seu plano de subscrição, dar logout, e subscrição ao modo de
 * familia ou não.
 *
 * @param navController
 * @param authView ViewModel que contém a autenticação dos dados do utilizador
 */

@Composable
fun PerfilScreen(
    navController: NavController,
    authView: AuthViewModel = viewModel()
)
{
    val auth = FirebaseAuth.getInstance()

    //Para ecrãs mais pequenos, a necessidade de se fazer scroll
    val scrollState = rememberScrollState()

    //Variável para fazer alterações de subscrição de conta
    val dadosUtilizador by authView.dadosUtilizador.collectAsState()

    //Variável para subscrição de conta
    val premium = dadosUtilizador?.premium == true

    //Variável para se o utilizador quiser trocar de ID de família
    var novoFamiliaID by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Welcome To Your Profile!",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            color = Green1
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(contentAlignment = Alignment.TopEnd)
        {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(100.dp),
                tint = Green2
            )

            //Distinção de contas Premium/Free
            if (premium)
            {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Premium Badge",
                    tint = Yellow,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(50))
                        .background(White).padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            colors = CardDefaults.cardColors(containerColor = Green4.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Account Details",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = Green1
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                )
                {
                    Text(
                        text = "Name: ${dadosUtilizador?.primeiroNome ?: ""} ${dadosUtilizador?.ultimoNome ?: ""}",
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Email: ${auth.currentUser?.email ?: "N/A"}"
                    )
                    Text(
                        text = "Phone: ${dadosUtilizador?.numero ?: "N/A"}"
                    )
                    Text(
                        text = "ID: ${auth.currentUser?.uid ?: "N/A"}"
                    )
                    Text(
                        text = "Family ID: ${dadosUtilizador?.familiaID ?: "N/A"}"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (premium) "PLAN: PRO MEMBER" else "PLAN: FREE ACCOUNT",
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Bold,
                        color = Gray
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = novoFamiliaID,
                        onValueChange = { novoFamiliaID = it },
                        label = { Text("Family ID") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Green1,
                            focusedLabelColor = Green1,
                            focusedLeadingIconColor = Green1
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (novoFamiliaID.isNotBlank()) {
                                authView.trocaIDFamilia(novoFamiliaID)
                                novoFamiliaID = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Green2),
                        modifier = Modifier.padding(horizontal = 6.dp)
                    ) {
                        Text("Save")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        FilledTonalButton(
            onClick = {if(premium) authView.upgradePremium(false) else authView.upgradePremium(true)},
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = if (premium) Gray else Yellow,
                contentColor = White
            )
        ) {
            Text(if (premium) "CANCEL PRO PLAN" else "UPGRADE TO PRO",fontWeight = FontWeight.Bold)
        }
        Text(if (premium) "Tired of PRO? Cancel it!" else "Tired of getting limited features? Get Premium!", color = Green1)

        Spacer(modifier = Modifier.height(12.dp))


        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val repositorio = AuthRepository()
                repositorio.logout()

                navController.navigate(Rotas.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green2)
        ) {
            Text("LOGOUT", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
