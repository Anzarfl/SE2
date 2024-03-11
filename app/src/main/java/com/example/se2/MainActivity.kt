package com.example.se2

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.se2.ui.theme.SE2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SE2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Greeting()
                        Button(text = "Connect", onClick = {
                            // todo
                        })

                    }
                }

            }
        }
    }

    @Composable
    fun Greeting() {
        Text(
            text = "Input Matrikelnummer bitte"
        )
    }

    @Composable
    fun Buttons(text: String, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .padding(8.dp)
                .size(200.dp, 60.dp),
        ) {
            Text(text)
        }
    }
}

