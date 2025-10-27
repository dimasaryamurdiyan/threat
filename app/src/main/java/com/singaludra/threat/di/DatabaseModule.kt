package com.singaludra.threat.di

import android.content.Context
import com.singaludra.threat.data.local.dao.SecurityAlertDao
import com.singaludra.threat.data.local.database.EncryptedSecurityAlertDatabase
import com.singaludra.threat.data.local.security.SecureKeyManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSecureKeyManager(
        @ApplicationContext context: Context
    ): SecureKeyManager {
        return SecureKeyManager(context)
    }

    @Provides
    @Singleton
    fun provideEncryptedSecurityAlertDatabase(
        @ApplicationContext context: Context,
        secureKeyManager: SecureKeyManager
    ): EncryptedSecurityAlertDatabase {
        return EncryptedSecurityAlertDatabase.buildDatabase(context, secureKeyManager)
    }

    @Provides
    fun provideSecurityAlertDao(database: EncryptedSecurityAlertDatabase): SecurityAlertDao {
        return database.securityAlertDao()
    }
}