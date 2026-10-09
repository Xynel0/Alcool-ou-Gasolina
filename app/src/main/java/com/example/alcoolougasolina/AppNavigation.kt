package com.example.alcoolougasolina

import android.R.attr.defaultValue
import android.R.attr.text
import android.R.attr.type
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentRecomposeScope
import androidx.navigation.NavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alcoolougasolina.Rotas.detalhes

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rotas.INICIAL
    ) {
        composable(Rotas.INICIAL) {
            TelaInicial(
                aoClicarSalvarPosto = { nome, precoAlcool, precoGasolina, usa75 ->
                    RascunhoPosto.nome = nome
                    RascunhoPosto.precoAlcool = precoAlcool
                    RascunhoPosto.precoGasolina = precoGasolina
                    RascunhoPosto.usa75 = usa75
                    navController.navigate(Rotas.cadastroNovo())
                },
                aoClicarVerPostos = {
                    navController.navigate(Rotas.LISTA)
                }
            )
        }

        composable(
            route = Rotas.CADASTRO,
            arguments = listOf(
                navArgument("postoId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
           backStackEntry ->
            val postoId = backStackEntry.arguments?.getString("postoId")
            TelaCadastroPosto(
                postoIdParaEditar = postoId,
                nomeInicial = if (postoId.isNullOrEmpty()) RascunhoPosto.nome else "",
                precoAlcoolInicial = if (postoId.isNullOrEmpty()) RascunhoPosto.precoAlcool else "",
                precoGasolinaInicial = if (postoId.isNullOrEmpty()) RascunhoPosto.precoGasolina else "",
                usa75Inicial = if (postoId.isNullOrEmpty()) RascunhoPosto.usa75 else true,
                aoSalvar = { idSalvo ->
                    navController.navigate(Rotas.detalhes(idSalvo)) {
                        if (postoId.isNullOrEmpty()) {
                            popUpTo(Rotas.CADASTRO) { inclusive = true }
                        } else {
                            popUpTo(Rotas.detalhes(postoId)) { inclusive = true }
                        }
                    }
                },
                aoEscolherNoMapa = {
                    navController.navigate(Rotas.ESCOLHER_LOCAL)
                }
            )
        }

        composable(Rotas.LISTA){
            TelaListaPostos(
                onPostoClicado = { postoId ->
                    navController.navigate(Rotas.detalhes(postoId))
                },
                onNovoPostoClicado = {
                    RascunhoPosto.nome = ""
                    RascunhoPosto.precoAlcool = ""
                    RascunhoPosto.precoGasolina = ""
                    RascunhoPosto.usa75 = true
                    navController.navigate(Rotas.cadastroNovo())
                }
            )
        }

        composable(
            route = Rotas.DETALHES,
            arguments = listOf(
                navArgument("postoId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val postoId = backStackEntry.arguments?.getString("postoId") ?: ""
            TelaDetalhesPosto(
                postoId = postoId,
                aoEditar = { id ->
                    navController.navigate(Rotas.cadastroEditar(id))
                },
                aoExcluir = {
                    navController.popBackStack()
                }
            )
        }

        composable(Rotas.ESCOLHER_LOCAL) {
            TelaEscolherLocal(
                latitudeInicial = ResultadoEscolhaLocal.latitude,
                longitudeInicial = ResultadoEscolhaLocal.longitude,
                aoConfirmar = {
                    navController.popBackStack()
                },
                aoCancelar = {
                    navController.popBackStack()
                }
            )
        }
    }
}