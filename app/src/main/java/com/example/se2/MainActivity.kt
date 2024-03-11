package com.example.se2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.*
import java.net.Socket

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppSurface()
            }
        }
    }
}

@Composable
fun AppSurface() {
    val studentId = remember { mutableStateOf("") }
    val serverResponse = remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GreetingMessage()
            InputField(studentId)
            ActionButtons(studentId, serverResponse)
            if (serverResponse.value.isNotEmpty()) {
                ResponseDisplay(serverResponse.value)
            }
        }
    }
}

@Composable
fun GreetingMessage() {
    Text(
        text = "Bitte Matrikelnummer eingeben:",
        modifier = Modifier.padding(16.dp)
        )
}

@Composable
fun InputField(state: MutableState<String>) {
    TextField(
        value = state.value,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() }) state.value = newValue
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ActionButtons(idState: MutableState<String>, responseState: MutableState<String>) {
    val coroutineScope = rememberCoroutineScope()

    Row {
        Button(onClick = {
            coroutineScope.launch {
                responseState.value = queryServer(idState.value)
            }
        }) {
            Text("Query Server")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = {
            responseState.value = localProcessing(idState.value)
        }) {
            Text("Local Process")
        }
    }
}

@Composable
fun ResponseDisplay(message: String) {
    Text(text = message, modifier = Modifier.padding(16.dp))
}



fun localProcessing(input: String): String {

    return input

}

suspend fun queryServer(input: String): String = withContext(Dispatchers.IO) {
    try {
        Socket("se2-submission.aau.at", 20080).use { socket ->

            val outputStream = socket.getOutputStream()
            val inputStream = socket.getInputStream()

            val matrikelnummerWithNewline = "$input\n"
            outputStream.write(matrikelnummerWithNewline.toByteArray())
            outputStream.flush()

            val responseBytes = inputStream.readBytes()
            val response = String(responseBytes)

            response
        }
    } catch (e: Exception) {
        "Error connecting to server: ${e.message}"
    }
}






