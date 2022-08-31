package com.example.learnerapplication.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

data class TokenPreference(
    val accessToken: String,
    val refreshToken: String,
    val otpVerification: Boolean,
    val details: Boolean
)

class TokenPreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object{
        private const val TAG = "PreferencesManager"
        val Context.accessTokenPreferenceDataStore: DataStore<Preferences> by preferencesDataStore(name = "Access Token")
    }

    private object AccessTokenKeys{
        val accessToken = stringPreferencesKey("Access Token")
        val refreshToken = stringPreferencesKey("Refresh Token")
        val otpVerification = booleanPreferencesKey("Verified")
        val details = booleanPreferencesKey("Details")
    }

    suspend fun updateAccessTokenPref(tokenPreference: TokenPreference){
        context.accessTokenPreferenceDataStore.edit {
            it[AccessTokenKeys.accessToken] = tokenPreference.accessToken
            it[AccessTokenKeys.refreshToken] = tokenPreference.refreshToken
            it[AccessTokenKeys.otpVerification] = tokenPreference.otpVerification
            it[AccessTokenKeys.details] = tokenPreference.details
        }
    }

    suspend fun updateAccessTokenDetailsKey(details: Boolean) {
        context.accessTokenPreferenceDataStore.edit {
            it[AccessTokenKeys.details] = details
        }
    }

    val accessTokenPrefFlow = context.accessTokenPreferenceDataStore.data.catch { exception ->
        if(exception is IOException){
            emit(emptyPreferences())
        }else{
            throw exception
        }
    }.map {
        val authToken = it[AccessTokenKeys.accessToken]?: ""
        val refreshToken = it[AccessTokenKeys.refreshToken]?: ""
        val otpVerified = it[AccessTokenKeys.otpVerification]?: false
        val details = it[AccessTokenKeys.details]?: false
        TokenPreference(authToken, refreshToken, otpVerified, details)
    }

}