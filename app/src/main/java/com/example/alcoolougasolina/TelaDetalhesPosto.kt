package com.example.alcoolougasolina

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
import androidx.compose.ui.res.stringResource
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

    if (postoAtual == null) {
        Text(
            text = stringResource(R.string.posto_n_o_encontrado),
            modifier = Modifier.padding(24.dp)
        )
    } else {
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
                Text(stringResource(R.string.pre_o_do_lcool_r, postoAtual.precoAlcool))
                Text(stringResource(R.string.pre_o_da_gasolina_r, postoAtual.precoGasolina))
                Text(
                    stringResource(
                        R.string.percentual_usado,
                        if (postoAtual.usa75) "75%" else "70%"
                    )
                )

                val percentual = if (postoAtual.usa75) 0.75 else 0.70
                val decisao = if (postoAtual.precoAlcool <= postoAtual.precoGasolina * percentual) {
                    stringResource(R.string.abaste_a_com_lcool)
                } else {
                    stringResource(R.string.abaste_a_com_gasolina)
                }
                Text(
                    text = decisao,
                    style = MaterialTheme.typography.titleMedium
                )

                val formato = remember { DateFormat.getDateFormat(context) }
                val formatoHora = remember { DateFormat.getTimeFormat(context) }

                val dataCadastroTexto = remember(postoAtual.dataCadastro) {
                    formato.format(Date(postoAtual.dataCadastro))
                }
                val horaCadastroTexto = remember(postoAtual.dataCadastro) {
                    formatoHora.format(Date(postoAtual.dataCadastro))
                }
                val dataEdicaoTexto = remember(postoAtual.dataUltimaEdicao) {
                    formato.format(Date(postoAtual.dataUltimaEdicao))
                }
                val horaEdicaoTexto = remember(postoAtual.dataUltimaEdicao) {
                    formatoHora.format(Date(postoAtual.dataUltimaEdicao))
                }

                val dataFormatada = stringResource(R.string.s, dataCadastroTexto, horaCadastroTexto)
                val dataEdicaoFormatada = stringResource(R.string.s, dataEdicaoTexto, horaEdicaoTexto)

                Text(stringResource(R.string.cadastrado_em, dataFormatada))
                Text(stringResource(R.string.ltima_atualiza_o, dataEdicaoFormatada))

                if (postoAtual.latitude != null && postoAtual.longitude != null) {
                    Text(
                        stringResource(
                            R.string.localiza_o,
                            "%.5f".format(postoAtual.latitude),
                            "%.5f".format(postoAtual.longitude)
                        )
                    )

                    Button(
                        onClick = {
                            abrirLocalizacaoNoMapa(context, postoAtual.latitude, postoAtual.longitude)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.abrir_no_maps))
                    }
                } else {
                    Text(stringResource(R.string.sem_localiza_o_salva))
                }

                Button(
                    onClick = { aoEditar(postoAtual.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.editar))
                }

                Button(
                    onClick = {
                        repositorio.remover(postoAtual.id)
                        aoExcluir()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.excluir))
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
                    text = postoAtual.nome,
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