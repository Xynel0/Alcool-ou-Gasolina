package com.example.alcoolougasolina

import android.content.Context
import androidx.core.content.edit
import com.example.alcoolougasolina.RascunhoPosto.usa75
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private const val PREFS_NAME = "alcool_ou_gasolina_prefs"
private const val CHAVE_POSTOS = "postos"
private const val CHAVE_USA_75 = "usa75"

fun salvarUsa75(context: Context, usa75: Boolean) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putBoolean(CHAVE_USA_75, usa75).apply()
}

fun carregarUsa75(context: Context): Boolean {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getBoolean(CHAVE_USA_75, true)
}
fun salvarPostos(context: Context, postos: List<Posto>){
    val json = Json.encodeToString(postos)
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE )
    prefs.edit().putString(CHAVE_POSTOS, json).apply()
}

fun carregarPostos(context: Context): List<Posto> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE )
    val json = prefs.getString(CHAVE_POSTOS, null) ?: return emptyList()
    return Json.decodeFromString(json)
}