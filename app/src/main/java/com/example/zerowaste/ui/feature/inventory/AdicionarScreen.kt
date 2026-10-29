package com.example.zerowaste.ui.feature.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zerowaste.ui.theme.Green2

import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.zerowaste.ui.theme.Green1
import com.example.zerowaste.ui.theme.White
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Ecrã de adicionar um produto.
 * Responsável pela adição de novos produtos ao inventário do utilizador.
 * Combina ambas inserções manual tal como através da leitura do código de
 * barras do produto, que permite preencher alguns campos de forma instantânea.
 *
 * @param viewModel ViewModel que vai gerir a adição dos produtos ao inventário
 */

@Composable
fun AdicionarScreen(viewModel: InventarioViewModel = viewModel())
{
    //Campos do formulário
    var nome by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    var validade by remember { mutableStateOf("") }
    var local by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var imagemURL by remember { mutableStateOf("") }

    //Calendário
    var mostrarCalendario by remember { mutableStateOf(false) }

    //Lista fixa de locais para armazenar um determinado produto
    val opcoesLocal = listOf("Chest Freezer", "Freezer", "Pantry", "Refrigerator")

    //Contexto necessário para iniciar os serviços do ML Kit
    val context = LocalContext.current

    //Iniciar o Google ML Kit Barcode Scanner
    val scanner = remember { GmsBarcodeScanning.getClient(context) }

    //Verifica o estado das mensagens
    val mensagem by viewModel.mensagem.collectAsState()

    //Date Picker - Android Compose
    //https://developer.android.com/develop/ui/compose/components/datepickers
    if (mostrarCalendario) {
        DatePickerModal(
            onDateSelected = { millis ->
                if (millis != null) {
                    val formatarData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    formatarData.timeZone = TimeZone.getTimeZone("UTC")
                    validade = formatarData.format(Date(millis))
                }
                mostrarCalendario = false
            },
            onDismiss = { mostrarCalendario = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Add Product",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                //Abre a câmara da Google para leitura de Barcodes
                scanner.startScan()
                    .addOnSuccessListener { barcode ->
                        val numeroLido = barcode.rawValue ?: ""

                        //Chamada à API da Open Food Facts
                        viewModel.procurarProduto(numeroLido) { nomeEncontrado, catEncontrada, imgEncontrada ->
                            nome = nomeEncontrado
                            categoria = catEncontrada
                            imagemURL = imgEncontrada
                        }
                    }
            },
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Green2)
        ) {
            Text("SCAN BARCODE")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (imagemURL.isNotEmpty())
        {
            AsyncImage(
                model = imagemURL,
                contentDescription = "",
                modifier = Modifier.size(100.dp).padding(bottom = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Name of the Product") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = quantidade,
            onValueChange = { quantidade = it },
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = validade,
            onValueChange = { },
            label = { Text("Expiration Date") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { mostrarCalendario = true }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Open Calendar")
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        //Drop Down Menu - Android Compose
        //https://developer.android.com/develop/ui/compose/components/menu
        DropDownLocal(
            localSelecionado = local,
            opcoes = opcoesLocal,
            onLocalChange = { novoLocal -> local = novoLocal }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = categoria, onValueChange = { categoria = it },
            label = { Text("Category (ex: Dairy)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                viewModel.gravarProduto(nome, quantidade, validade, local, categoria, imagemURL) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = Green2,
                contentColor = White
            )
        ) {
            Text("SAVE")
        }

        if (mensagem != null)
        {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (mensagem == "sucesso") "Product Saved!" else mensagem!!,
                color = if (mensagem == "sucesso") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

//Date Picker - Android Compose
//https://developer.android.com/develop/ui/compose/components/datepickers
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState  = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }
            ) {
                Text("OK", color = Green1)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Green1)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

//Drop Down Menu - Android Compose
//https://developer.android.com/develop/ui/compose/components/menu
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownLocal(
    localSelecionado: String,
    opcoes: List<String>,
    onLocalChange: (String) -> Unit
) {
    var expandirDropdown by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandirDropdown,
        onExpandedChange = { expandirDropdown = !expandirDropdown }
    ) {
        OutlinedTextField(
            value = localSelecionado,
            onValueChange = { },
            readOnly = true,
            label = { Text("Local") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirDropdown)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expandirDropdown,
            onDismissRequest = { expandirDropdown = false }
        ) {
            opcoes.forEach { opcao ->
                DropdownMenuItem(
                    text = { Text(opcao) },
                    onClick = {
                        onLocalChange(opcao)
                        expandirDropdown = false
                    }
                )
            }
        }
    }
}
