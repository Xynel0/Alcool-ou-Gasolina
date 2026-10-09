package com.example.alcoolougasolina

import android.R.attr.fontWeight
import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Date

@Composable
fun TelaDetalhesPosto(
    postoId: String,
    aoEditar: (String) -> Unit,
    aoExcluir: () -> Unit
) {
    val context = LocalContext.current
    val repositorio = remember { PostoRepository(context) }
    var posto by remember { mutableStateOf<Posto?>(null) }

    LaunchedEffect(postoId) {
        posto = repositorio.listar().find { it.id == postoId }
    }

    val postoAtual = posto

    if (postoAtual == null){
        Text(
            text = "Posto não encontrado!",
            modifier = Modifier.padding(24.dp)
        )
    }
    else {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 450.dp)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(top = 70.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Preço do Álcool: R$ ${postoAtual.precoAlcool}")
                Text("Preço da Gasolina: R$ ${postoAtual.precoGasolina}")
                Text("Percentual usado: ${
                    if (postoAtual.usa75){
                        "75%"
                    }
                    else{
                        "70%"
                    }
                }")

                val percentual = if (postoAtual.usa75) 0.75 else 0.70
                val decisao = if (postoAtual.precoAlcool <= postoAtual.precoGasolina * percentual) {
                    "Abasteça com Álcool"
                }
                else{
                    "Abasteça com Gasolina"
                }
                Text(
                    text = decisao,
                    style = MaterialTheme.typography.titleMedium
                )

                val formato = remember { DateFormat.getDateFormat(context) }
                val formatoHora = remember { DateFormat.getTimeFormat(context) }
                val dataFormatada = remember(postoAtual.dataCadastro) {
                    "${formato.format(
                        Date(
                            postoAtual.dataCadastro
                        )
                    )
                    } às ${
                        formatoHora.format(
                            Date(
                                postoAtual.dataCadastro
                            )
                        )
                    }"
                }
                Text("Cadastrado em: $dataFormatada")
                val dataEdicaoFormatada = remember(postoAtual.dataUltimaEdicao) {
                    "${formato.format(Date(postoAtual.dataUltimaEdicao))} às ${formatoHora.format(Date(postoAtual.dataUltimaEdicao))}"
                }
                Text("Última atualização: $dataEdicaoFormatada")

                if (postoAtual.latitude != null && postoAtual.longitude != null) {
                    Text(
                        "Localização: ${
                            "%5f".format(
                                postoAtual.latitude
                            )
                        }, ${
                            "%5f".format(
                                postoAtual.longitude
                            )
                        }"
                    )

                    Button(
                        onClick = {
                            abrirLocalizacaoNoMapa(context, postoAtual.latitude, postoAtual.longitude)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Abrir no Maps")
                    }
                } else {
                    Text("Sem localização salva")
                }

                Button(
                    onClick = { aoEditar(postoAtual.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar")
                }



                Button(
                    onClick = {
                        repositorio.remover(postoAtual.id)
                        aoExcluir()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Excluir")
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
                    text =  postoAtual.nome,
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
}