package org.uwaterloo.subletr.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule

@Module
@InstallIn(ViewModelComponent::class)
abstract class SimpleViewModelModule
