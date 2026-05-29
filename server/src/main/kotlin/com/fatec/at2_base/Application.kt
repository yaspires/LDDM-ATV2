package com.fatec.at2_base

import com.fatec.at2_base.model.Filme
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val filmes = mutableListOf(
    Filme(1, "Interestelar", "2014", "Ficção Científica", "169 min"),
    Filme(2, "Batman", "2022", "Ação", "176 min"),
    Filme(3, "Corra", "2017", "Suspense", "104 min")
)

var nextId = 4

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    routing {

        get("/filmes") {

            val json = filmes.joinToString(
                prefix = "[",
                postfix = "]"
            ) { filme ->

                """
                {
                    "id":${filme.id},
                    "nome":"${filme.nome}",
                    "ano":"${filme.ano}",
                    "genero":"${filme.genero}",
                    "duracao":"${filme.duracao}"
                }
                """.trimIndent()
            }

            call.respondText(
                json,
                contentType = io.ktor.http.ContentType.Application.Json
            )
        }

        post("/filmes") {

            val body = call.receiveText()

            val nome = extrairCampo(body, "nome")
            val ano = extrairCampo(body, "ano")
            val genero = extrairCampo(body, "genero")
            val duracao = extrairCampo(body, "duracao")

            val novoFilme = Filme(
                id = nextId++,
                nome = nome,
                ano = ano,
                genero = genero,
                duracao = duracao
            )

            filmes.add(novoFilme)

            call.respondText(
                """{"mensagem":"Filme cadastrado"}""",
                contentType = io.ktor.http.ContentType.Application.Json
            )
        }
    }
}

fun extrairCampo(json: String, campo: String): String {

    val regex = """"$campo"\s*:\s*"([^"]+)"""".toRegex()

    return regex.find(json)
        ?.groupValues
        ?.get(1)
        ?: ""
}