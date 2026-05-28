package com.fatec.at2_base

import androidx.compose.foundation.BorderStroke
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
import com.fatec.at2_base.model.Roupa
import kotlinx.coroutines.launch

// ── Navegação simples ─────────────────────────────────────────────────────────
enum class Tela { LISTA, ADICIONAR }

// ── Cores ─────────────────────────────────────────────────────────────────────
private val Borda = Color(0xFFE5E7EB)
private val Secundario = Color(0xFF6B7280)

@Composable
fun App() {
    MaterialTheme {
        var telaAtual by remember { mutableStateOf(Tela.LISTA) }
        var roupas by remember { mutableStateOf<List<Roupa>>(emptyList()) }
        var carregando by remember { mutableStateOf(true) }
        var erro by remember { mutableStateOf<String?>(null) }
        val scope = rememberCoroutineScope()

        fun atualizar() {
            scope.launch {
                carregando = true; erro = null
                try { roupas = ApiClient.getRoupas() }
                catch (e: Exception) { erro = e.message }
                carregando = false
            }
        }

        LaunchedEffect(Unit) { atualizar() }

        when (telaAtual) {
            Tela.LISTA -> TelaLista(
                roupas = roupas,
                carregando = carregando,
                erro = erro,
                onAtualizar = { atualizar() },
                onAdicionar = { telaAtual = Tela.ADICIONAR }
            )
            Tela.ADICIONAR -> TelaAdicionar(
                onVoltar = { telaAtual = Tela.LISTA },
                onRoupaCriada = {
                    telaAtual = Tela.LISTA
                    atualizar()
                }
            )
        }
    }
}

// ── Tela de Lista ─────────────────────────────────────────────────────────────
@Composable
fun TelaLista(
    roupas: List<Roupa>,
    carregando: Boolean,
    erro: String?,
    onAtualizar: () -> Unit,
    onAdicionar: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("👗 Guarda-Roupa", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                    Text("Suas peças cadastradas.", fontSize = 14.sp, color = Secundario)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onAtualizar,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, Borda)
                    ) { Text("↻", fontSize = 16.sp, color = Color.Black) }
                    Button(
                        onClick = onAdicionar,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) { Text("+ Peça", fontSize = 13.sp) }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Borda)

            when {
                carregando -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = Secundario
                        )
                    }
                }
                erro != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Erro de conexão", fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(4.dp))
                            Text(erro, fontSize = 13.sp, color = Secundario)
                            Spacer(Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = onAtualizar,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, Borda)
                            ) { Text("Tentar novamente", fontSize = 13.sp, color = Color.Black) }
                        }
                    }
                }
                roupas.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Nenhuma peça cadastrada ainda.", color = Secundario, fontSize = 14.sp)
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(roupas) { roupa ->
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Borda),
                                colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        tipoParaEmoji(roupa.tipo),
                                        fontSize = 28.sp,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                    Column {
                                        Text(roupa.nome, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                        Text(
                                            "${roupa.tipo} · ${roupa.cor} · Tam. ${roupa.tamanho}",
                                            fontSize = 12.sp,
                                            color = Secundario
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun tipoParaEmoji(tipo: String): String = when (tipo.lowercase()) {
    "camiseta", "camisa", "blusa" -> "👕"
    "calça", "calca", "short", "bermuda" -> "👖"
    "tênis", "tenis", "sapato", "sandália", "bota" -> "👟"
    "vestido", "saia" -> "👗"
    "jaqueta", "casaco", "moletom" -> "🧥"
    "meias" -> "🧦"
    "boné", "chapéu" -> "🧢"
    else -> "🛍️"
}