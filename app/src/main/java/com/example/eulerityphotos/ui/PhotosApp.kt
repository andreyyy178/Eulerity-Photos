package com.example.eulerityphotos.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eulerityphotos.ui.screens.PhotosViewModel
import com.example.eulerityphotos.R
import com.example.eulerityphotos.ui.screens.EditScreen
import com.example.eulerityphotos.ui.screens.HomeScreen

@Composable
fun PhotosApp(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = MaterialTheme.colors.background
        ) {
            val navController = rememberNavController()

            val photosViewModel: PhotosViewModel =
                viewModel(factory = PhotosViewModel.Factory)

            //ui navigation routes
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(
                        navController = navController,
                        photosUiState = photosViewModel.photosUiState,
                        retryAction = photosViewModel::getPhotos
                    )
                }
                //passing photo id argument to the edit view in order to retrieve it
                composable("edit/{photoId}",
                    arguments = listOf(
                        navArgument("photoId") {
                            type = NavType.StringType
                        }
                    )
                ) {
                    val photoId = it.arguments?.getString("photoId") ?: ""
                    EditScreen(
                        navController = navController,
                        photosUiState = photosViewModel.photosUiState,
                        photoId = photoId,
                        retryAction = photosViewModel::getPhotos
                    )
                }
            }
        }
    }
}