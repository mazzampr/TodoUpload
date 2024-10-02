package com.nakama.core.domain.interactor

import com.nakama.core.data.model.Gallery
import com.nakama.core.data.model.Resource
import com.nakama.core.data.model.Task
import com.nakama.core.data.model.UserModel
import com.nakama.core.domain.repository.GalleryRepository
import com.nakama.core.domain.repository.ProfileRepository
import com.nakama.core.domain.repository.TaskRepository
import com.nakama.core.domain.usecase.GalleryUseCase
import com.nakama.core.domain.usecase.ProfileUseCase
import com.nakama.core.domain.usecase.TaskUseCase
import kotlinx.coroutines.flow.Flow
import java.io.File

class GalleryInteractor(private val galleryRepository: GalleryRepository): GalleryUseCase {
  override fun getAllUserGallery(): Flow<Resource<List<Gallery>>> =
    galleryRepository.getAllUserGallery()

  override fun addGallery(gallery: Gallery, file: File): Flow<Resource<String>> =
    galleryRepository.addGallery(gallery, file)

  override fun deleteGallery(gallery: Gallery): Flow<Resource<String>> =
    galleryRepository.deleteGallery(gallery)

}