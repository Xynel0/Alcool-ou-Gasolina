package com.example.alcoolougasolina

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.alcoolougasolina.ui.theme.AlcoolOuGasolinaTheme

class MainActivity : ComponentActivity() {
    private val TAG = "CicloDeVida"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "No onCreate")
        enableEdgeToEdge()
        setContent {
            AlcoolOuGasolinaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "no onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "no onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "no onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "no onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "no onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "no onDestroy")
    }
}