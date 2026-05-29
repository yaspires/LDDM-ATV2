package com.fatec.at2_base.model

import kotlinx.serialization.Serializable

@Serializable
data class Filme(
    val id: Int = 0,
    val nome: String,
    val ano: String,
    val genero: String,
    val duracao: String
)