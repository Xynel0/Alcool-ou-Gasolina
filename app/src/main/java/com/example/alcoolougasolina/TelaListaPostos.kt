package com.example.alcoolougasolina

import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun TelaListaPostos(
    onPostoClicado: (String) -> Unit,
    onNovoPostoClicado: () -> Unit
) {
    val context = LocalContext.current
    val repositorio = remember { PostoRepository(context) }
    var postos by remember { mutableStateOf<List<Posto>>(emptyList()) }

    LaunchedEffect(Unit) {
        postos = repositorio.listar()
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
    ) {
        if (postos.isEmpty()) {
            Text(
                text = stringResource(R.string.nenhum_posto_salvo_por_enquanto),
                modifier = Modifier.padding(25.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(postos) { posto ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onPostoClicado(posto.id) }
                            .padding(15.dp)
                    ) {
                        Text(
                            text = posto.nome,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(stringResource(R.string.lcool_r, posto.precoAlcool))
                        Text(stringResource(R.string.gasolina_r, posto.precoGasolina))
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = onNovoPostoClicado,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.novo_posto))
        }
    }
}