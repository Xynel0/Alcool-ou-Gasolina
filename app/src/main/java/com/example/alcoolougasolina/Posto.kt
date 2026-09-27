package com.example.alcoolougasolina
import kotlinx.serialization.Serializable

@Serializable
data class Posto(
    val id: String,
    val nome: String,
    val precoAlcool: Double,
    val precoGasolina: Double,
    val usa75: Boolean,
    val dataCadastro: Long,
    val latitude: Double?,
    val longitude: Double?
)