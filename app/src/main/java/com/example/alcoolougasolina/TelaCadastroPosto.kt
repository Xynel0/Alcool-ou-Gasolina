package com.example.alcoolougasolina

import android.R
import android.R.attr.direction
import androidx.collection.emptyLongSet
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TelaCadastroPosto(
    postoIdParaEditar: String?,
    nomeInicial: String = "",
    precoAlcoolInicial: String = "",
    precoGasolinaInicial: String = "",
    usa75Inicial: Boolean = true,
    aoSalvar: (String) -> Unit
) {
    val context = LocalContext.current
    val repositorio = remember { PostoRepository(context) }

    var nome by rememberSaveable { mutableStateOf(nomeInicial) }
    var precoAlcool by rememberSaveable { mutableStateOf(precoAlcoolInicial) }
    var precoGasolina by rememberSaveable { mutableStateOf(precoGasolinaInicial) }
    var usa75 by rememberSaveable { mutableStateOf(usa75Inicial) }
    var postoOriginal by remember { mutableStateOf<Posto?>(null) }
    var mensagemErro by remember { mutableStateOf<String?>(null)}

    LaunchedEffect(postoIdParaEditar) {
        if (!postoIdParaEditar.isNullOrEmpty()) {
            val encontrado = repositorio.listar().find { it.id == postoIdParaEditar }
            if (encontrado != null) {
                postoOriginal = encontrado
                nome = encontrado.nome
                precoAlcool = encontrado.precoAlcool.toString()
                precoGasolina = encontrado.precoGasolina.toString()
                usa75 = encontrado.usa75
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = 450.dp)
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 70.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do Posto") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = precoAlcool,
                onValueChange = { precoAlcool = it },
                label = { Text("Preço do Álcool (R$)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = precoGasolina,
                onValueChange = { precoGasolina = it },
                label = { Text("Preço da Gasolina (R$)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ) {

                Text("Porcentagem do cálculo")

                Box(contentAlignment = Alignment.Center) {
                    Switch(
                        checked = usa75,
                        onCheckedChange = { usa75 = it })
                    Text(
                        text = if (usa75) "75%" else "70%",
                        fontSize = 10.sp,
                        color = if (usa75) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(if (usa75) Alignment.CenterStart else Alignment.CenterEnd)
                            .padding(horizontal = 5.dp)
                    )
                }
            }

            Button(
                onClick = {
                    val alcool = precoAlcool.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val gasolina = precoGasolina.replace(",", ".").toDoubleOrNull() ?: 0.0

                    when{
                        nome.isBlank() -> {
                            mensagemErro = "Digite o nome do posto!"
                        }
                        else -> {
                            mensagemErro = null
                            val original = postoOriginal
                            if (original != null) {
                                val editado = original.copy(
                                    nome = nome,
                                    precoAlcool = alcool,
                                    precoGasolina = gasolina,
                                    usa75 = usa75
                                )
                                repositorio.atualizar(editado)
                                aoSalvar(editado.id)
                            } else {
                                val novo = Posto(
                                    id = "",
                                    nome = nome,
                                    precoAlcool = alcool,
                                    precoGasolina = gasolina,
                                    usa75 = usa75,
                                    dataCadastro = System.currentTimeMillis(),
                                    latitude = null,
                                    longitude = null
                                )
                                val salvo = repositorio.adicionar(novo)
                                aoSalvar(salvo.id)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar")
            }
            mensagemErro?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = if (postoIdParaEditar.isNullOrEmpty()) {
                    "Novo Posto"
                } else {
                    "Editar Posto"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 25.dp, bottom = 10.dp)
            )
        }
    }
}