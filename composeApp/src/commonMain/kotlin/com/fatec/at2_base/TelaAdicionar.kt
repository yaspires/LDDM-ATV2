package com.fatec.at2_base


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fatec.at2_base.model.Roupa
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Borda = Color(0xFFE5E7EB)
private val Secundario = Color(0xFF6B7280)
private val Verde = Color(0xFF16A34A)

@Composable
fun TelaAdicionar(
    onVoltar: () -> Unit,
    onRoupaCriada: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var cor by remember { mutableStateOf("") }
    var tamanho by remember { mutableStateOf("") }
    var salvando by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf<String?>(null) }
    var isErro by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Nova Peça", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                    Text("Adicione ao seu armário.", fontSize = 14.sp, color = Secundario)
                }
                OutlinedButton(
                    onClick = onVoltar,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Borda)
                ) { Text("← Voltar", fontSize = 13.sp, color = Color.Black) }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Borda)

            // Campo Nome
            CampoTexto(label = "Nome da peça *", valor = nome, placeholder = "Ex: Camiseta Branca") { nome = it }
            Spacer(Modifier.height(12.dp))

            // Campo Tipo
            CampoTexto(label = "Tipo *", valor = tipo, placeholder = "Ex: Camiseta, Calça, Tênis...") { tipo = it }
            Spacer(Modifier.height(12.dp))

            // Campo Cor
            CampoTexto(label = "Cor *", valor = cor, placeholder = "Ex: Azul, Preto, Branco...") { cor = it }
            Spacer(Modifier.height(12.dp))

            // Campo Tamanho
            CampoTexto(label = "Tamanho *", valor = tamanho, placeholder = "Ex: P, M, G, 38, 42...") { tamanho = it }

            Spacer(Modifier.height(24.dp))

            // Botão Salvar
            Button(
                onClick = {
                    if (nome.isBlank() || tipo.isBlank() || cor.isBlank() || tamanho.isBlank()) {
                        mensagem = "Preencha todos os campos obrigatórios."
                        isErro = true
                        return@Button
                    }
                    scope.launch {
                        salvando = true; mensagem = null
                        try {
                            ApiClient.createRoupa(
                                Roupa(
                                    nome = nome.trim(),
                                    tipo = tipo.trim(),
                                    cor = cor.trim(),
                                    tamanho = tamanho.trim()
                                )
                            )
                            mensagem = "✅ Peça cadastrada com sucesso!"
                            isErro = false
                            delay(800)
                            onRoupaCriada()
                        } catch (e: Exception) {
                            mensagem = "Erro: ${e.message}"
                            isErro = true
                        }
                        salvando = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                enabled = !salvando,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                if (salvando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (salvando) "Salvando..." else "Salvar Peça", fontSize = 14.sp)
            }

            // Feedback
            mensagem?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = it,
                    fontSize = 13.sp,
                    color = if (isErro) Color(0xFFDC2626) else Verde,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CampoTexto(label: String, valor: String, placeholder: String, onMudanca: (String) -> Unit) {
    val Borda = Color(0xFFE5E7EB)
    val Secundario = Color(0xFF6B7280)
    Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(4.dp))
    OutlinedTextField(
        value = valor,
        onValueChange = onMudanca,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = Secundario) },
        shape = RoundedCornerShape(6.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Borda,
            focusedBorderColor = Color.Black
        )
    )
}