package com.fatec.at2_base.model

import kotlinx.serialization.Serializable

@Serializable
data class Roupa(
    val id: Int = 0,
    val nome: String,
    val tipo: String,       // ex: "Camiseta", "Calça", "Tênis"
    val cor: String,
    val tamanho: String     // ex: "P", "M", "G", "38", "42"
)