package com.nakama.todoupload.ui.gallery

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.nakama.core.data.model.Gallery
import com.nakama.core.data.model.Task
import com.nakama.core.domain.usecase.GalleryUseCase
import com.nakama.core.domain.usecase.TaskUseCase
import java.io.File
import java.time.LocalDate
import java.time.YearMonth

class GalleryViewModel(private val galleryUseCase: GalleryUseCase) : ViewModel() {
  fun addGallery(gallery: Gallery, file: File) = galleryUseCase.addGallery(gallery, file).asLiveData()
  fun deleteGallery(gallery: Gallery) = galleryUseCase.deleteGallery(gallery).asLiveData()
  fun getUserGallery() = galleryUseCase.getAllUserGallery().asLiveData()
}