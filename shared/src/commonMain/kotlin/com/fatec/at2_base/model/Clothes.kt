package com.fatec.at2_base.model

import kotlinx.serialization.Serializable

@Serializable
data class Clothes(
    val id: Int,
    val nome: String,
    val tipo: String? = null,
    val cor: String? = null,
)