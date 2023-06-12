package org.uwaterloo.subletr.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.uwaterloo.subletr.api.apis.DefaultApi
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.services.NavigationService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
	@Singleton
	@Provides
	fun provideApiClient(): DefaultApi {
		return DefaultApi("http://207.246.122.186/api")
	}
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SimpleSingletonModule {
	@Singleton
	@Binds
	abstract fun addNavigationService(navigationService: NavigationService): INavigationService
}
