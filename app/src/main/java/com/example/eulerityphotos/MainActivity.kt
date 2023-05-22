package com.example.eulerityphotos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import com.example.eulerityphotos.ui.PhotosApp
import com.example.eulerityphotos.ui.theme.EulerityPhotosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EulerityPhotosTheme {
                window?.statusBarColor = MaterialTheme.colors.primaryVariant.toArgb()
                PhotosApp()
            }
        }
    }
}
