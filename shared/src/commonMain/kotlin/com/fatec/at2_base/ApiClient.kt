package com.fatec.at2_base

import com.fatec.at2_base.model.Filme
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

expect fun createHttpClient(): HttpClient

object ApiClient {

    private val client: HttpClient by lazy {
        createHttpClient()
    }

    suspend fun getFilmes(): List<Filme> {

        val texto = client
            .get("$BASE_URL/filmes")
            .bodyAsText()

        return parseFilmes(texto)
    }

    suspend fun createFilme(filme: Filme) {

        val body = """
            {
                "nome":"${filme.nome}",
                "ano":"${filme.ano}",
                "genero":"${filme.genero}",
                "duracao":"${filme.duracao}"
            }
        """.trimIndent()

        client.post("$BASE_URL/filmes") {

            contentType(ContentType.Application.Json)

            setBody(body)
        }
    }
}

fun parseFilmes(json: String): List<Filme> {

    val itens = json
        .trim()
        .removePrefix("[")
        .removeSuffix("]")

    if (itens.isBlank()) {
        return emptyList()
    }

    return itens
        .split("},")
        .map {
            parseFilme(
                it + if (!it.endsWith("}")) "}" else ""
            )
        }
}

fun parseFilme(json: String): Filme {

    fun campo(nome: String): String {

        val regex = """"$nome"\s*:\s*"([^"]+)"""".toRegex()

        return regex.find(json)
            ?.groupValues
            ?.get(1)
            ?: ""
    }

    fun campoInt(nome: String): Int {

        val regex = """"$nome"\s*:\s*(\d+)""".toRegex()

        return regex.find(json)
            ?.groupValues
            ?.get(1)
            ?.toIntOrNull()
            ?: 0
    }

    return Filme(
        id = campoInt("id"),
        nome = campo("nome"),
        ano = campo("ano"),
        genero = campo("genero"),
        duracao = campo("duracao")
    )
}