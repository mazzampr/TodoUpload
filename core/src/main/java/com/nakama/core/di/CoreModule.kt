package com.nakama.core.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.nakama.core.data.AuthRepositoryImpl
import com.nakama.core.data.GalleryRepositoryImpl
import com.nakama.core.data.ProfileRepositoryImpl
import com.nakama.core.data.TaskRepositoryImpl
import com.nakama.core.data.remote.RemoteDataSource
import com.nakama.core.domain.repository.AuthRepository
import com.nakama.core.domain.repository.GalleryRepository
import com.nakama.core.domain.repository.ProfileRepository
import com.nakama.core.domain.repository.TaskRepository
import org.koin.dsl.module

val firebaseModule = module {
  single { Firebase.firestore }
  single { FirebaseAuth.getInstance() }
  single { Firebase.storage }
}

val repositoryModule = module {
  single { RemoteDataSource(get(), get(), get()) }

  // Data
  single<AuthRepository> { AuthRepositoryImpl(get()) }
  single<ProfileRepository> { ProfileRepositoryImpl(get()) }
  single<TaskRepository> { TaskRepositoryImpl(get()) }
  single<GalleryRepository> { GalleryRepositoryImpl(get()) }
}

