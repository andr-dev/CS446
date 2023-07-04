package org.uwaterloo.subletr.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.services.NavigationService

@Module
@InstallIn(ActivityRetainedComponent::class)
object ActivityRetainedModule {
	@Provides
	@ActivityRetainedScoped
	fun provideNavigationService(@ApplicationContext context: Context): INavigationService {
		return NavigationService(context = context)
	}
}
