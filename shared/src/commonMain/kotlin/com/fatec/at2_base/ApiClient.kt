package com.fatec.at2_base

import com.fatec.at2_base.model.Roupa
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

expect fun createHttpClient(): HttpClient

object ApiClient {

    private val client: HttpClient by lazy { createHttpClient() }

    /** GET /roupas */
    suspend fun getRoupas(): List<Roupa> {
        val texto = client.get("$BASE_URL/roupas").bodyAsText()
        return parseRoupas(texto)
    }

    /** POST /roupas */
    suspend fun createRoupa(roupa: Roupa): Roupa {
        val body = """{"nome":"${roupa.nome}","tipo":"${roupa.tipo}","cor":"${roupa.cor}","tamanho":"${roupa.tamanho}"}"""
        val texto = client.post("$BASE_URL/roupas") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.bodyAsText()
        return parseRoupa(texto)
    }
}

fun parseRoupas(json: String): List<Roupa> {
    val itens = json.trim().removePrefix("[").removeSuffix("]")
    if (itens.isBlank()) return emptyList()
    return itens.split("},").map { parseRoupa(it + if (!it.endsWith("}")) "}" else "") }
}

fun parseRoupa(json: String): Roupa {
    fun campo(nome: String): String {
        val regex = """"$nome"\s*:\s*"([^"]+)"""".toRegex()
        return regex.find(json)?.groupValues?.get(1) ?: ""
    }
    fun campoInt(nome: String): Int {
        val regex = """"$nome"\s*:\s*(\d+)""".toRegex()
        return regex.find(json)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    }
    return Roupa(
        id = campoInt("id"),
        nome = campo("nome"),
        tipo = campo("tipo"),
        cor = campo("cor"),
        tamanho = campo("tamanho")
    )
}