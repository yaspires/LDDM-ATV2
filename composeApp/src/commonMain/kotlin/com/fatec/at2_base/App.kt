package com.fatec.at2_base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fatec.at2_base.model.Filme
import kotlinx.coroutines.launch

private val Azul = Color(0xFF1E3A8A)
private val Fundo = Color(0xFFF3F4F6)
private val Cinza = Color(0xFF6B7280)

@Composable
fun App() {

    var filmes by remember {
        mutableStateOf<List<Filme>>(emptyList())
    }

    var carregando by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    var nome by remember { mutableStateOf("") }
    var ano by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var duracao by remember { mutableStateOf("") }

    fun carregarFilmes() {

        scope.launch {

            carregando = true

            filmes = ApiClient.getFilmes()

            carregando = false
        }
    }

    fun cadastrarFilme() {

        scope.launch {

            ApiClient.createFilme(
                Filme(
                    nome = nome,
                    ano = ano,
                    genero = genero,
                    duracao = duracao
                )
            )

            nome = ""
            ano = ""
            genero = ""
            duracao = ""

            carregarFilmes()
        }
    }

    LaunchedEffect(Unit) {
        carregarFilmes()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Fundo
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            Text(
                text = "Catálogo de Filmes",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Azul
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do filme") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = ano,
                onValueChange = { ano = it },
                label = { Text("Ano") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = genero,
                onValueChange = { genero = it },
                label = { Text("Gênero") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = duracao,
                onValueChange = { duracao = it },
                label = { Text("Duração") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { cadastrarFilme() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Azul
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cadastrar Filme")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (carregando) {

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Azul)
                }

            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(filmes) { filme ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .padding(16.dp)
                            ) {

                                Text(
                                    text = filme.nome,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Ano: ${filme.ano}",
                                    color = Cinza
                                )

                                Text(
                                    text = "Gênero: ${filme.genero}",
                                    color = Cinza
                                )

                                Text(
                                    text = "Duração: ${filme.duracao}",
                                    color = Cinza
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}