package com.fatec.at2_base

import com.fatec.at2_base.model.Roupa
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// ── Armazenamento em memória ──────────────────────────────────────────────────
val roupas = mutableListOf(
    Roupa(id = 1, nome = "Camiseta Branca", tipo = "Camiseta", cor = "Branca", tamanho = "M"),
    Roupa(id = 2, nome = "Calça Jeans Azul", tipo = "Calça", cor = "Azul", tamanho = "42"),
    Roupa(id = 3, nome = "Tênis All Star Preto", tipo = "Tênis", cor = "Preto", tamanho = "40")
)
var nextId = 4

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    routing {

        // GET /roupas → retorna a lista como texto JSON manual
        get("/roupas") {
            val json = roupas.joinToString(prefix = "[", postfix = "]") { roupa ->
                """{"id":${roupa.id},"nome":"${roupa.nome}","tipo":"${roupa.tipo}","cor":"${roupa.cor}","tamanho":"${roupa.tamanho}"}"""
            }
            call.respondText(json, contentType = io.ktor.http.ContentType.Application.Json)
        }

        // POST /roupas → recebe JSON e cadastra
        post("/roupas") {
            val body = call.receiveText()
            val nome = extrairCampo(body, "nome")
            val tipo = extrairCampo(body, "tipo")
            val cor = extrairCampo(body, "cor")
            val tamanho = extrairCampo(body, "tamanho")
            val novaRoupa = Roupa(id = nextId++, nome = nome, tipo = tipo, cor = cor, tamanho = tamanho)
            roupas.add(novaRoupa)
            val resposta = """{"id":${novaRoupa.id},"nome":"${novaRoupa.nome}","tipo":"${novaRoupa.tipo}","cor":"${novaRoupa.cor}","tamanho":"${novaRoupa.tamanho}"}"""
            call.respondText(resposta, contentType = io.ktor.http.ContentType.Application.Json)
        }
    }
}

fun extrairCampo(json: String, campo: String): String {
    val regex = """"$campo"\s*:\s*"([^"]+)"""".toRegex()
    return regex.find(json)?.groupValues?.get(1) ?: ""
}
