package com.example.alcoolougasolina

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch

@Composable
fun TelaCadastroPosto(
    postoIdParaEditar: String?,
    nomeInicial: String = "",
    precoAlcoolInicial: String = "",
    precoGasolinaInicial: String = "",
    usa75Inicial: Boolean = true,
    aoSalvar: (String) -> Unit,
    aoEscolherNoMapa: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val repositorio = remember { PostoRepository(context) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    val textoErroNomeVazio = stringResource(R.string.digite_o_nome_do_posto)
    val textoErroPrecosInvalidos = stringResource(R.string.digite_os_dois_pre_os_corretamente)

    var nome by rememberSaveable { mutableStateOf(nomeInicial) }
    var precoAlcool by rememberSaveable { mutableStateOf(precoAlcoolInicial) }
    var precoGasolina by rememberSaveable { mutableStateOf(precoGasolinaInicial) }
    var usa75 by rememberSaveable { mutableStateOf(usa75Inicial) }
    var postoOriginal by remember { mutableStateOf<Posto?>(null) }
    var mensagemErro by remember { mutableStateOf<String?>(null) }

    var latitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var longitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var buscandoLocalizacao by remember { mutableStateOf(false) }
    var localizacaoFalhou by remember { mutableStateOf(false) }

    var estadoPermissao by remember {
        mutableStateOf(
            if (temPermissaoLocalizacao(context)) {
                EstadoPermissaoLocalizacao.CONCEDIDA
            } else {
                EstadoPermissaoLocalizacao.NAO_PEDIDA
            }
        )
    }
    var mostrarDialogoExplicacao by remember { mutableStateOf(false) }
    var mostrarDialogoConfiguracoes by remember { mutableStateOf(false) }

    val launcherPermissao = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedida ->
        estadoPermissao = when {
            concedida -> EstadoPermissaoLocalizacao.CONCEDIDA
            activity != null && podeExplicarPermissao(activity) -> EstadoPermissaoLocalizacao.NEGADA
            else -> EstadoPermissaoLocalizacao.NEGADA_PERMANENTE
        }

        if (estadoPermissao == EstadoPermissaoLocalizacao.NEGADA) {
            mostrarDialogoExplicacao = true
        } else if (estadoPermissao == EstadoPermissaoLocalizacao.NEGADA_PERMANENTE) {
            mostrarDialogoConfiguracoes = true
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                estadoPermissao = if (temPermissaoLocalizacao(context)) {
                    EstadoPermissaoLocalizacao.CONCEDIDA
                } else {
                    EstadoPermissaoLocalizacao.NAO_PEDIDA
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(postoIdParaEditar) {
        if (!postoIdParaEditar.isNullOrEmpty()) {
            val encontrado = repositorio.listar().find { it.id == postoIdParaEditar }
            if (encontrado != null) {
                postoOriginal = encontrado
                nome = encontrado.nome
                precoAlcool = encontrado.precoAlcool.toString()
                precoGasolina = encontrado.precoGasolina.toString()
                usa75 = encontrado.usa75
                latitude = encontrado.latitude
                longitude = encontrado.longitude
            }
        }
    }

    LaunchedEffect(Unit) {
        if (ResultadoEscolhaLocal.latitude != null && ResultadoEscolhaLocal.longitude != null) {
            latitude = ResultadoEscolhaLocal.latitude
            longitude = ResultadoEscolhaLocal.longitude
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = 450.dp)
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 70.dp, start = 20.dp, end = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text(stringResource(R.string.nome_do_posto)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = precoAlcool,
                onValueChange = { precoAlcool = it },
                label = { Text(stringResource(R.string.preco_do_alcool_r)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = precoGasolina,
                onValueChange = { precoGasolina = it },
                label = { Text(stringResource(R.string.preco_da_gasolina_r)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ) {
                Text(stringResource(R.string.porcentagem_do_c_lculo))

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

            if (estadoPermissao != EstadoPermissaoLocalizacao.CONCEDIDA && latitude != null && longitude != null) {
                Text(
                    stringResource(
                        R.string.localiza_o_escolhida,
                        "%.5f".format(latitude),
                        "%.5f".format(longitude)
                    )
                )
            }

            when (estadoPermissao) {
                EstadoPermissaoLocalizacao.CONCEDIDA -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (latitude != null && longitude != null) {
                            Text(
                                stringResource(
                                    R.string.localiza_o_salva,
                                    "%.5f".format(latitude),
                                    "%.5f".format(longitude)
                                )
                            )
                        } else {
                            Text(stringResource(R.string.nenhuma_localiza_o_salva_ainda))
                        }

                        Button(
                            onClick = {
                                buscandoLocalizacao = true
                                localizacaoFalhou = false
                                coroutineScope.launch {
                                    val localizacao = obterLocalizacaoAtual(context)
                                    if (localizacao != null) {
                                        latitude = localizacao.latitude
                                        longitude = localizacao.longitude
                                    } else {
                                        localizacaoFalhou = true
                                    }
                                    buscandoLocalizacao = false
                                }
                            },
                            enabled = !buscandoLocalizacao,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (buscandoLocalizacao) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(stringResource(R.string.usar_localiza_o_atual))
                            }
                        }

                        if (localizacaoFalhou) {
                            Text(
                                text = stringResource(R.string.local_nao_encontrado_ver_config),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                EstadoPermissaoLocalizacao.NAO_PEDIDA, EstadoPermissaoLocalizacao.NEGADA -> {
                    Button(
                        onClick = {
                            launcherPermissao.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.permitir_acesso_localiza_o))
                    }
                }

                EstadoPermissaoLocalizacao.NEGADA_PERMANENTE -> {
                    Button(
                        onClick = { mostrarDialogoConfiguracoes = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.permitir_acesso_localiza_o))
                    }
                }
            }

            Button(
                onClick = {
                    ResultadoEscolhaLocal.latitude = latitude
                    ResultadoEscolhaLocal.longitude = longitude
                    aoEscolherNoMapa()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.escolher_local_no_mapa))
            }

            if (latitude != null && longitude != null) {
                OutlinedButton(
                    onClick = {
                        latitude = null
                        longitude = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.remover_localiza_o))
                }
            }

            Button(
                onClick = {
                    val alcool = precoAlcool.replace(",", ".").toDoubleOrNull()
                    val gasolina = precoGasolina.replace(",", ".").toDoubleOrNull()

                    when {
                        nome.isBlank() -> {
                            mensagemErro = textoErroNomeVazio
                        }
                        alcool == null || gasolina == null -> {
                            mensagemErro = textoErroPrecosInvalidos
                        }
                        else -> {
                            mensagemErro = null
                            val original = postoOriginal
                            if (original != null) {
                                val editado = original.copy(
                                    nome = nome,
                                    precoAlcool = alcool,
                                    precoGasolina = gasolina,
                                    usa75 = usa75,
                                    dataUltimaEdicao = System.currentTimeMillis(),
                                    latitude = latitude,
                                    longitude = longitude
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
                                    dataUltimaEdicao = System.currentTimeMillis(),
                                    latitude = latitude,
                                    longitude = longitude
                                )
                                val salvo = repositorio.adicionar(novo)
                                aoSalvar(salvo.id)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.salvar))
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
                text = if (postoIdParaEditar.isNullOrEmpty()) stringResource(R.string.novo_posto) else stringResource(
                    R.string.editar_posto
                ),
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

    if (mostrarDialogoExplicacao) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoExplicacao = false },
            title = { Text(stringResource(R.string.por_que_pedimos_sua_localiza_o)) },
            text = {
                Text(stringResource(R.string.aviso_sobre_local))
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoExplicacao = false
                        launcherPermissao.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                ) {
                    Text(stringResource(R.string.permitir))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarDialogoExplicacao = false }) {
                    Text(stringResource(R.string.agora_n_o))
                }
            }
        )
    }

    if (mostrarDialogoConfiguracoes) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoConfiguracoes = false },
            title = { Text(stringResource(R.string.localiza_o_bloqueada)) },
            text = {
                Text(stringResource(R.string.aviso_final_local))
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoConfiguracoes = false
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                        context.startActivity(intent)
                    }
                ) {
                    Text(stringResource(R.string.abrir_configura_es))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarDialogoConfiguracoes = false }) {
                    Text(stringResource(R.string.agora_n_o))
                }
            }
        )
    }
}