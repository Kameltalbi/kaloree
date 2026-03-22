package com.kaloree.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kaloree.ui.navigation.MainScreen
import com.kaloree.ui.theme.KaloreeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KaloreeTheme {
                MainScreen()
            }
        }
    }
}
