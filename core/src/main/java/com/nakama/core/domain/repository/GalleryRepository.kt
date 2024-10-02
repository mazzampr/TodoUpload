package com.nakama.core.domain.repository

import com.nakama.core.data.model.Gallery
import com.nakama.core.data.model.Resource
import com.nakama.core.data.model.Task
import kotlinx.coroutines.flow.Flow
import java.io.File

interface GalleryRepository {
  fun getAllUserGallery(): Flow<Resource<List<Gallery>>>
  fun addGallery(gallery: Gallery, file: File): Flow<Resource<String>>
  fun deleteGallery(gallery: Gallery): Flow<Resource<String>>
}