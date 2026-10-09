package com.example.alcoolougasolina

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import android.net.Uri
import com.example.alcoolougasolina.ResultadoEscolhaLocal.latitude
import com.example.alcoolougasolina.ResultadoEscolhaLocal.longitude

suspend fun obterLocalizacaoAtual(context: Context): android.location.Location? {
    if (!temPermissaoLocalizacao(context)) return null

    val cliente = LocationServices.getFusedLocationProviderClient(context)

    return suspendCancellableCoroutine { continuation ->
        try {
            cliente.lastLocation
                .addOnSuccessListener { ultima ->
                    if (ultima != null) {
                        continuation.resume(ultima)
                    } else {
                        cliente.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                            .addOnSuccessListener { atual ->
                                continuation.resume(atual)
                            }
                            .addOnFailureListener {
                                continuation.resume(null)
                            }
                    }
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        } catch (e: SecurityException) {
            continuation.resume(null)
        }
    }
}

fun temPermissaoLocalizacao(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun podeExplicarPermissao(activity: Activity): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
}

enum class EstadoPermissaoLocalizacao {
    NAO_PEDIDA,
    NEGADA,
    NEGADA_PERMANENTE,
    CONCEDIDA
}

fun abrirLocalizacaoNoMapa(context: Context, latitude: Double, longitude: Double){
    val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
    val intent = Intent(Intent.ACTION_VIEW, uri)

    try{
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            "Nenhum aplicativo de mapas encontrado",
            Toast.LENGTH_SHORT
        ).show()
    }
}