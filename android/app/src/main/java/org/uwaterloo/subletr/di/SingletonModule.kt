package org.uwaterloo.subletr.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.uwaterloo.subletr.api.apis.DefaultApi

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
	val api = DefaultApi("http://207.246.122.186/api")

	@Provides
	fun provideApiClient(): DefaultApi {
		return api;
	}
}
