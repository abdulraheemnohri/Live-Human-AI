package com.livehumanai.livehumanai.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.livehumanai.livehumanai.ui.theme.LiveHumanAITheme

/**
 * MainActivity is the entry point of the Live Human AI app.
 * It sets up the Compose UI and navigates to the main screen.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LiveHumanAITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    com.livehumanai.livehumanai.ui.navigation.NavGraph()
                }
            }
        }
    }
}
