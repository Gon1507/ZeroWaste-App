package com.example.zerowaste.ui.feature.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.example.zerowaste.ui.theme.Green1
import com.example.zerowaste.ui.theme.Green2
import com.example.zerowaste.ui.theme.Green3
import com.example.zerowaste.ui.theme.White

/**
 * Ecrã de Login/Registo da aplicação.
 * Permite ao utilizador entrar na sua conta ou criar uma nova, com
 * suporte para partilha de inventário familiar por um ID de convite
 *
 * @param viewModel ViewModel responsável pela autenticação do utilizador
 * @param onLoginSucesso Callback executado após uma autenticação bem-sucedida,
 * neste caso, para mudar para a página principal
 */

@Composable

fun LoginScreen(
    authView: AuthViewModel = viewModel(),
    onLoginSucesso: () -> Unit
) {
    //Campos do formulário
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var primeiroNome by remember { mutableStateOf("") }
    var ultimoNome by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var familiaID by remember { mutableStateOf("") }

    //Alteração entre Login e Registo
    var isRegisto by remember { mutableStateOf(false) }

    //Verifica o estado das mensagens
    val mensagem by authView.mensagem.collectAsState()

    //Para ecrãs mais pequenos, a necessidade de se fazer scroll
    val scrollState = rememberScrollState()

    //Navegação automática em caso de sucesso
    LaunchedEffect(mensagem)
    {
        if (mensagem == "sucesso")
        {
            onLoginSucesso()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Text(
            text = "ZeroWaste",
            style = MaterialTheme.typography.displayMedium,
            color = Green1,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Save Food, Save Money",
            style = MaterialTheme.typography.bodyLarge,
            color = Green1
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Green3.copy(alpha = 0.05f))
        )
        {
            Column(modifier = Modifier.padding(24.dp)) {
                if(isRegisto)
                {
                    Column {
                        OutlinedTextField(
                            value = primeiroNome,
                            onValueChange = { primeiroNome = it },
                            label = { Text("First Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Green1,
                                focusedLabelColor = Green1,
                                focusedLeadingIconColor = Green1
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = ultimoNome,
                            onValueChange = { ultimoNome = it },
                            label = { Text("Last Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Green1,
                                focusedLabelColor = Green1,
                                focusedLeadingIconColor = Green1
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = numero,
                            onValueChange = { numero = it },
                            label = { Text("Mobile Phone") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Green1,
                                focusedLabelColor = Green1,
                                focusedLeadingIconColor = Green1
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = familiaID,
                            onValueChange = { familiaID = it },
                            label = { Text("Invite Code (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Green1,
                                focusedLabelColor = Green1,
                                focusedLeadingIconColor = Green1
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green1,
                        focusedLabelColor = Green1,
                        focusedLeadingIconColor = Green1
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green1,
                        focusedLabelColor = Green1,
                        focusedLeadingIconColor = Green1
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (mensagem != null && mensagem != "sucesso")
        {
            Text(
                text = mensagem!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        FilledTonalButton(
            onClick =
                {
                    if (isRegisto) authView.fazerRegisto(primeiroNome, ultimoNome, email, numero, password, familiaID)
                    else authView.fazerLogin(email, password)
                },
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .height(50.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = Green2,
                contentColor = White
            )
        )
        {
            Text(if (isRegisto) "CREATE ACCOUNT" else "LOGIN")
        }


        TextButton(onClick = { isRegisto = !isRegisto })
        {
            Text(if (isRegisto) "Already have an account? Login" else "New here? Sign Up!", color = Green1)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        )
        {
            Text(
                text = "\uD83C\uDF31" + " ",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Designed for a zero waste planet",
                style = MaterialTheme.typography.labelMedium,
                color = Green1.copy(alpha = 0.6f),
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = " " + "\uD83C\uDF31",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
