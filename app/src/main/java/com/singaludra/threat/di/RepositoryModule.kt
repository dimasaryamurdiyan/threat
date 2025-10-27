package com.singaludra.threat.di

import com.singaludra.threat.data.repository.SecurityAlertRepositoryImpl
import com.singaludra.threat.domain.repository.SecurityAlertRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSecurityAlertRepository(
        securityAlertRepositoryImpl: SecurityAlertRepositoryImpl
    ): SecurityAlertRepository
}