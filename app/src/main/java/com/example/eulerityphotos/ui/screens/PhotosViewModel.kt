package com.example.eulerityphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.eulerityphotos.data.PhotosRepository
import com.example.eulerityphotos.model.Photo
import com.example.eulerityphotos.PhotosApplication
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface PhotosUiState {
    data class Success(val photos: List<Photo>) : PhotosUiState
    object Error : PhotosUiState
    object Loading : PhotosUiState
}

class PhotosViewModel(private val photosRepository: PhotosRepository) : ViewModel() {

    var photosUiState: PhotosUiState by mutableStateOf(PhotosUiState.Loading)
        private set

    init {
        getPhotos()
    }

    fun getPhotos() {
        viewModelScope.launch {
            photosUiState = PhotosUiState.Loading
            photosUiState = try {
                PhotosUiState.Success(photosRepository.getPhotos())
            } catch (e: IOException) {
                PhotosUiState.Error
            } catch (e: HttpException) {
                PhotosUiState.Error
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PhotosApplication)
                val photosRepository = application.container.photosRepository
                PhotosViewModel(photosRepository = photosRepository)
            }
        }
    }
}