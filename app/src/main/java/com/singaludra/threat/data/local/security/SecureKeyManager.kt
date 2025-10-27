package com.singaludra.threat.data.local.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureKeyManager @Inject constructor(
    private val context: Context
) {
    private val keyAlias = "SecurityAlertDatabaseKey"
    private val prefsFileName = "secure_alert_prefs"

    fun getDatabasePassphrase(): String {
        return try {
            val existingKey = getSecurePreferences().getString(keyAlias, null)
            existingKey ?: generateAndStoreNewKey()
        } catch (e: Exception) {
            generateAndStoreNewKey()
        }
    }

    private fun generateAndStoreNewKey(): String {
        val secureRandom = SecureRandom()
        val keyBytes = ByteArray(32)
        secureRandom.nextBytes(keyBytes)
        val key = keyBytes.joinToString("") { "%02x".format(it) }

        try {
            getSecurePreferences()
                .edit()
                .putString(keyAlias, key)
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return key
    }

    private fun getSecurePreferences(): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            prefsFileName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}