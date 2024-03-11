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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import java.net.Socket

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppSurface() // Die Hauptoberfläche wird gerendert
            }
        }
    }
}

@Composable
fun AppSurface() {
    // Zustandsvariablen für die Eingabe der Matrikelnummer und die Serverantwort
    val studentId = remember { mutableStateOf("") }
    val serverResponse = remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GreetingMessage() // Anzeige der Begrüßungsnachricht
            InputField(studentId) // Textfeld für die Eingabe der Matrikelnummer
            ActionButtons(studentId, serverResponse) // Aktionsbuttons zum Abfragen des Servers und zur lokalen Verarbeitung
            if (serverResponse.value.isNotEmpty()) {
                ResponseDisplay(serverResponse.value) // Anzeige der Serverantwort, falls vorhanden
            }
        }
    }
}

@Composable
fun GreetingMessage() {
    // Begrüßungsnachricht anzeigen
    Text(
        text = "Bitte Matrikelnummer eingeben:",
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun InputField(state: MutableState<String>) {
    // Textfeld für die Eingabe der Matrikelnummer
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
        // Button zum Abfragen des Servers
        Button(onClick = {
            coroutineScope.launch {
                responseState.value = queryServer(idState.value)
            }
        }) {
            Text("Server abfragen")
        }
        Spacer(modifier = Modifier.width(8.dp))
        // Button fuer Berechnung
        Button(onClick = {
            responseState.value = localProcessing(idState.value)
        }) {
            Text("Berechnung")
        }
    }
}

@Composable
fun ResponseDisplay(message: String) {
    // Anzeige der Serverantwort
    Text(
        text = message,
        modifier = Modifier.padding(16.dp),
        textAlign = TextAlign.Center,
        color = Color.Black,
        fontSize = 18.sp
    )
}

fun localProcessing(input: String): String {
    // Lokale Verarbeitung der Matrikelnummer
    val digits = input.toCharArray()

    // Arrays für gerade und ungerade Ziffern erstellen
    val evenDigits = StringBuilder()
    val oddDigits = StringBuilder()

    // Ziffern nach Geradheit sortieren
    digits.forEach { digit ->
        if (digit.isDigit()) {
            if (digit.toInt() % 2 == 0) {
                evenDigits.append(digit)
            } else {
                oddDigits.append(digit)
            }
        }
    }

    // Sortieren der Ziffern
    val sortedEvenDigits = evenDigits.toString().toCharArray().sorted().joinToString("")
    val sortedOddDigits = oddDigits.toString().toCharArray().sorted().joinToString("")

    // Ergebnis zusammenstellen
    val sortedNumber = sortedEvenDigits + sortedOddDigits

    return sortedNumber

}

suspend fun queryServer(input: String): String = withContext(Dispatchers.IO) {
    // Abfrage des Servers
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






