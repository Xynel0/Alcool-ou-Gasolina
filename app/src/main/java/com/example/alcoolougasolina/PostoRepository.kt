package com.example.alcoolougasolina

import android.content.Context
import java.util.UUID

class PostoRepository(private val context: Context) {

    fun listar(): List<Posto> {
        return carregarPostos(context)
    }

    fun adicionar(posto: Posto): Posto {
        val postoComId = posto.copy(id = UUID.randomUUID().toString())
        val listaAtual = listar()
        salvarPostos(context, listaAtual + postoComId)
        return postoComId
    }

    fun atualizar(postoEditado: Posto) {
        val listaAtual = listar()
        val novaLista = listaAtual.map { posto ->
            if (posto.id == postoEditado.id){
                postoEditado
            }
            else {
                posto
            }
        }
        salvarPostos(context, novaLista)
    }

    fun remover(id: String) {
        val listaAtual = listar()
        val novaLista = listaAtual.filter { it.id != id }
        salvarPostos(context, novaLista)
    }
}