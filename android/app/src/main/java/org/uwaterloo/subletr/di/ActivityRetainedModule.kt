package org.uwaterloo.subletr.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.services.ISnackbarService
import org.uwaterloo.subletr.services.NavigationService
import org.uwaterloo.subletr.services.SnackbarService

@Module
@InstallIn(ActivityRetainedComponent::class)
object ActivityRetainedModule {
	@Provides
	@ActivityRetainedScoped
	fun provideNavigationService(@ApplicationContext context: Context): INavigationService {
		return NavigationService(context = context)
	}

	@Provides
	@ActivityRetainedScoped
	fun provideSnackbarService(): ISnackbarService {
		return SnackbarService()
	}
}
