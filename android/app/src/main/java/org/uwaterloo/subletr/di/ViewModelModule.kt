package org.uwaterloo.subletr.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.uwaterloo.subletr.services.ILocationService
import org.uwaterloo.subletr.services.LocationService

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
	@Provides
	@Reusable
	fun provideLocationService(@ApplicationContext context: Context): ILocationService {
		return LocationService(context)
	}
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class SimpleViewModelModule
