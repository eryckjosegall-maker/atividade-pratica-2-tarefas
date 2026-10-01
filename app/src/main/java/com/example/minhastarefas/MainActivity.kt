package com.example.minhastarefas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class Tarefa(
    val id: Int,
    val titulo: String,
    val descricao: String,
    val concluida: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TelaTarefas()
                }
            }
        }
    }
}

@Composable
fun TelaTarefas() {
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var erroTitulo by remember { mutableStateOf(false) }
    var apenasPendentes by remember { mutableStateOf(false) }
    var proximoId by remember { mutableIntStateOf(1) }
    val tarefas = remember { mutableStateListOf<Tarefa>() }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val listaVisivel = if (apenasPendentes) tarefas.filter { !it.concluida } else tarefas
    val pendentes = tarefas.count { !it.concluida }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Layout 1: Row (logo + título + texto informativo)
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Layout 2: Box (logo)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Minhas Tarefas",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Organize seu dia de forma simples",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dois campos de entrada
            OutlinedTextField(
                value = titulo,
                onValueChange = {
                    titulo = it
                    erroTitulo = false
                },
                label = { Text("Título da tarefa") },
                isError = erroTitulo,
                supportingText = {
                    if (erroTitulo) Text("Informe um título")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botão principal com onClick + resposta visual (Snackbar)
            Button(
                onClick = {
                    if (titulo.isBlank()) {
                        erroTitulo = true
                        scope.launch {
                            snackbarHostState.showSnackbar("Preencha o título antes de adicionar")
                        }
                    } else {
                        tarefas.add(Tarefa(proximoId, titulo.trim(), descricao.trim()))
                        proximoId++
                        titulo = ""
                        descricao = ""
                        scope.launch {
                            snackbarHostState.showSnackbar("Tarefa adicionada!")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Adicionar tarefa")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card com informação relevante
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Total: ${tarefas.size}  •  Pendentes: $pendentes")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Componente adicional: Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mostrar apenas pendentes")
                Switch(
                    checked = apenasPendentes,
                    onCheckedChange = { apenasPendentes = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Layout 3: LazyColumn (lista de tarefas)
            if (listaVisivel.isEmpty()) {
                Text(
                    text = "Nenhuma tarefa por aqui.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaVisivel, key = { it.id }) { tarefa ->
                        ItemTarefa(
                            tarefa = tarefa,
                            onAlternar = {
                                val i = tarefas.indexOfFirst { it.id == tarefa.id }
                                if (i >= 0) {
                                    tarefas[i] = tarefa.copy(concluida = !tarefa.concluida)
                                }
                            },
                            onRemover = {
                                tarefas.removeAll { it.id == tarefa.id }
                                scope.launch {
                                    snackbarHostState.showSnackbar("Tarefa removida")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemTarefa(
    tarefa: Tarefa,
    onAlternar: () -> Unit,
    onRemover: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = tarefa.concluida, onCheckedChange = { onAlternar() })
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarefa.titulo,
                    fontWeight = FontWeight.SemiBold,
                    color = if (tarefa.concluida)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
                if (tarefa.descricao.isNotEmpty()) {
                    Text(
                        text = tarefa.descricao,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            TextButton(onClick = onRemover) {
                Text("Remover")
            }
        }
    }
}