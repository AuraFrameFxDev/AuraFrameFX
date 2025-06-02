package dev.aurakai.auraframefx.data

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiService(private val context: Context) {
    private val securePrefs = SecurePreferences(context)

    init {
        try {
            SecurePreferences.initialize(context)
            securePrefs.saveApiToken("4c633ac0de5680741c7f72a392249285063f6e6c")
            recreateClient()
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Failed to initialize API service", e)
        }
    }

    fun setOAuthToken(token: String) {
        securePrefs.saveOAuthToken(token)
        recreateClient()
    }

    fun setApiToken(token: String) {
        securePrefs.saveApiToken(token)
        recreateClient()
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                val token = securePrefs.getApiToken()
                if (token != null) {
                    request.addHeader("Authorization", "Bearer $token")
                }
                val url = chain.request().url.toString()
                Log.d("Network", "Request URL: $url")
                val response = chain.proceed(request.build())
                Log.d("Network", "Response code: ${response.code}")
                response
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun createRetrofit(): Retrofit {
        val context = context.applicationContext
        val authBaseUrl = context.getString(R.string.cloud_run_auth_service)
        context.getString(R.string.cloud_run_storage_service)
        context.getString(R.string.cloud_run_ml_service)

        return Retrofit.Builder()
            .baseUrl(authBaseUrl)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private var retrofit: Retrofit? = null

    private fun recreateClient() {
        retrofit = createRetrofit()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit?.create(serviceClass)
            ?: throw IllegalStateException("Retrofit client not initialized")
    }
}
