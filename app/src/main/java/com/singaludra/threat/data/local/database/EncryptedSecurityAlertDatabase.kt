package com.singaludra.threat.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.singaludra.threat.data.local.dao.SecurityAlertDao
import com.singaludra.threat.data.local.entity.SecurityAlertEntity
import com.singaludra.threat.data.local.security.SecureKeyManager
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [SecurityAlertEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EncryptedSecurityAlertDatabase : RoomDatabase() {

    abstract fun securityAlertDao(): SecurityAlertDao

    companion object {
        const val DATABASE_NAME = "encrypted_security_alerts"

        fun buildDatabase(
            context: Context,
            secureKeyManager: SecureKeyManager
        ): EncryptedSecurityAlertDatabase {
            val passphrase = secureKeyManager.getDatabasePassphrase()
            val factory = SupportFactory(SQLiteDatabase.getBytes(passphrase.toCharArray()))

            return Room.databaseBuilder(
                context,
                EncryptedSecurityAlertDatabase::class.java,
                DATABASE_NAME
            )
                .openHelperFactory(factory)
                .build()
        }
    }
}