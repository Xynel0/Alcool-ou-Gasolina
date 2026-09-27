package com.example.alcoolougasolina

import androidx.compose.foundation.background
import com.example.alcoolougasolina.ui.theme.AlcoolOuGasolinaTheme
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alcoolougasolina.ui.theme.AlcoolOuGasolinaTheme

@Composable
fun TelaInicial(
    aoClicarSalvarPosto: (
            nome: String,
            precoAlcool: String,
            precoGasolina: String,
            usa75: Boolean
            ) -> Unit,
    aoClicarVerPostos: () -> Unit
){
    val context = LocalContext.current

    var precoAlcool by rememberSaveable { mutableStateOf("") }
    var precoGasolina by rememberSaveable { mutableStateOf("") }
    var nomeDoPosto by rememberSaveable { mutableStateOf("") }
    var usa75 by rememberSaveable { mutableStateOf(carregarUsa75(context)) }
    var resultado by rememberSaveable { mutableStateOf("Vamos Calcular?") }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = 450.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(top = 70.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically)
        ) {

            OutlinedTextField(
                value = nomeDoPosto,
                onValueChange = { nomeDoPosto = it },
                label = { Text("Nome do Posto (opcional)") },
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
                        onCheckedChange = {
                            usa75 = it
                            salvarUsa75(context, it)
                        })
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
                    val alcool = precoAlcool.replace(",", ".").toDoubleOrNull()
                    val gasolina = precoGasolina.replace(",", ".").toDoubleOrNull()

                    if (alcool == null || gasolina == null) {
                        resultado = "Digite os dois preços!"
                    } else {
                        val percentual = if (usa75) 0.75 else 0.70
                        resultado = if (alcool <= gasolina * percentual) {
                            "Abasteça com Álcool"
                        } else {
                            "Abasteça com Gasolina"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calcular")
            }

            Text(
                text = resultado,
                textAlign = TextAlign.Center,
                style = if (resultado == "Vamos Calcular?" || resultado == "Digite os dois preços!") MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    aoClicarSalvarPosto(
                        nomeDoPosto,
                        precoAlcool,
                        precoGasolina,
                        usa75
                        )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Posto")
            }

            OutlinedButton(
                onClick = {
                    aoClicarVerPostos()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Postos Salvos")
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
                text = "Álcool ou Gasolina?",
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

@Preview(showBackground = true)
@Composable
fun TelaPreview() {
    AlcoolOuGasolinaTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            TelaInicial(
                aoClicarSalvarPosto = { _, _, _, _ -> },
                aoClicarVerPostos = {}
            )
        }
    }
}