package com.example.alcoolougasolina

object Rotas {
    const val  INICIAL = "inicial"
    const val LISTA = "lista"
    const val DETALHES = "detalhes/{postoId}"
    const val CADASTRO = "cadastro?postoId={postoId}"

    fun detalhes(postoId: String): String {
        return "detalhes/$postoId"
    }

    fun cadastroNovo(): String{
        return "cadastro"
    }

    fun cadastroEditar(postoId: String): String {
        return "cadastro?postoId=$postoId"
    }
}