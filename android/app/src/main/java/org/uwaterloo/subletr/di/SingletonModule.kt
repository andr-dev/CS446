package org.uwaterloo.subletr.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import org.uwaterloo.subletr.api.apis.AuthenticationApi
import org.uwaterloo.subletr.api.apis.ListingsApi
import org.uwaterloo.subletr.api.apis.ServerApi
import org.uwaterloo.subletr.api.apis.UserApi
import org.uwaterloo.subletr.api.infrastructure.ApiClient
import org.uwaterloo.subletr.services.AuthenticationService
import org.uwaterloo.subletr.services.IAuthenticationService
import org.uwaterloo.subletr.services.IIoService
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.services.IoService
import org.uwaterloo.subletr.services.NavigationService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
	@Singleton
	@Provides
	fun provideOkHttpClient(): OkHttpClient {
		return ApiClient.defaultClient
	}

	@Singleton
	@Provides
	fun provideAuthenticationApi(httpClient: OkHttpClient): AuthenticationApi {
		return AuthenticationApi("http://207.246.122.186/api", httpClient)
	}

	@Singleton
	@Provides
	fun provideListingsApi(httpClient: OkHttpClient): ListingsApi {
		return ListingsApi("http://207.246.122.186/api", httpClient)
	}

	@Singleton
	@Provides
	fun provideServerApi(httpClient: OkHttpClient): ServerApi {
		return ServerApi("http://207.246.122.186/api", httpClient)
	}

	@Singleton
	@Provides
	fun provideUserApi(httpClient: OkHttpClient): UserApi {
		return UserApi("http://207.246.122.186/api", httpClient)
	}

	@Singleton
	@Provides
	fun provideIoService(@ApplicationContext context: Context): IIoService {
		return IoService(context = context)
	}

	@Singleton
	@Provides
	fun provideAuthenticationService(ioService: IIoService): IAuthenticationService {
		return AuthenticationService(ioService = ioService)
	}
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SimpleSingletonModule {
	@Singleton
	@Binds
	abstract fun addNavigationService(navigationService: NavigationService): INavigationService
}
