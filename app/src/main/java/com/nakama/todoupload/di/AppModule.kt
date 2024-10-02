package com.nakama.todoupload.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.nakama.core.domain.interactor.AuthInteractor
import com.nakama.core.domain.interactor.GalleryInteractor
import com.nakama.core.domain.interactor.ProfileInteractor
import com.nakama.core.domain.interactor.TaskInteractor
import com.nakama.core.domain.usecase.AuthUseCase
import com.nakama.core.domain.usecase.GalleryUseCase
import com.nakama.core.domain.usecase.ProfileUseCase
import com.nakama.core.domain.usecase.TaskUseCase
import com.nakama.todoupload.ui.auth.AuthViewModel
import com.nakama.todoupload.ui.gallery.GalleryViewModel
import com.nakama.todoupload.ui.home.HomeViewModel
import com.nakama.todoupload.ui.profile.ProfileViewModel
import com.nakama.todoupload.ui.task.TaskViewModel
import org.koin.dsl.module

val useCaseModule = module {
  factory<AuthUseCase> { AuthInteractor(get()) }
  factory<ProfileUseCase> { ProfileInteractor(get()) }
  factory<TaskUseCase> { TaskInteractor(get()) }
  factory<GalleryUseCase> { GalleryInteractor(get()) }
}

@RequiresApi(Build.VERSION_CODES.O)
val viewModelModule = module {
  single { AuthViewModel(get()) }
  single { ProfileViewModel(get(), get()) }
  single { HomeViewModel(get()) }
  single { TaskViewModel(get()) }
  single { GalleryViewModel(get()) }
}