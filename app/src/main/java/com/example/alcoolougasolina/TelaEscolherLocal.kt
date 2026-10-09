package com.example.alcoolougasolina

import android.preference.PreferenceManager
import android.widget.Toast
import androidx.compose.foundation.checkScrollableContainerConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker


@Composable
fun TelaEscolherLocal(
    latitudeInicial: Double?,
    longitudeInicial: Double?,
    aoConfirmar: () -> Unit,
    aoCancelar: () -> Unit
) {
    val context = LocalContext.current

    remember {
        val configuracao = Configuration.getInstance()
        configuracao.load(context, PreferenceManager.getDefaultSharedPreferences(context))
        configuracao.userAgentValue = "${context.packageName}/1.0"
        true
    }

    var pontoEscolhido by remember {
        mutableStateOf(
            if (latitudeInicial != null && longitudeInicial != null) {
                GeoPoint(latitudeInicial, longitudeInicial)
            } else {
                GeoPoint(-3.7327, -38.5267)
            }
        )
    }

    var mapView by remember { mutableStateOf<MapView?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = "Toque no mapa para marcar o local",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(15.dp)
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.OpenTopo)
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        controller.setCenter(pontoEscolhido)

                        val marcador = Marker(this)
                        marcador.position = pontoEscolhido
                        overlays.add(marcador)

                        val recebedorDeToque = object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                pontoEscolhido = p
                                marcador.position = p
                                invalidate()
                                return true
                            }

                            override fun longPressHelper(p: GeoPoint): Boolean = false
                        }
                        overlays.add(MapEventsOverlay(recebedorDeToque))

                        mapView = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Local escolhido: ${"%5f".format(pontoEscolhido.latitude)}, ${"%5f".format(pontoEscolhido.longitude)}",
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    ResultadoEscolhaLocal.latitude = pontoEscolhido.latitude
                    ResultadoEscolhaLocal.longitude = pontoEscolhido.longitude
                    aoConfirmar()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar localização")
            }

            Text(
                text = "Kartendaten: ©OpenStreetMap-Mitwirkende, SRTM | © OpenTopoMap (CC-BY-SA)",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}