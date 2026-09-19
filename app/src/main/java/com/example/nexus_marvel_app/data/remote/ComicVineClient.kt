package com.example.nexus_marvel_app.data.remote

import com.example.nexus_marvel_app.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Builds the singleton [ComicVineApi]:
 *  - injects api_key + format=json on every request and a descriptive User-Agent,
 *  - throttles to a maximum of 1 request/second (client-side rate limit),
 *  - logs requests in debug builds.
 */
object ComicVineClient {

    val api: ComicVineApi by lazy { build() }

    private fun build(): ComicVineApi {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(RateLimitInterceptor(minIntervalMs = 1000))
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(ComicVineApi.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ComicVineApi::class.java)
    }

    /** Appends api_key + format and sets the required User-Agent. */
    private class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val url = original.url.newBuilder()
                .addQueryParameter("api_key", BuildConfig.COMIC_VINE_API_KEY)
                .addQueryParameter("format", "json")
                .build()
            val request = original.newBuilder()
                .url(url)
                .header("User-Agent", "NexusMarvelApp/1.0")
                .build()
            return chain.proceed(request)
        }
    }

    /** Blocks so consecutive requests are at least [minIntervalMs] apart. */
    private class RateLimitInterceptor(private val minIntervalMs: Long) : Interceptor {
        private val lock = Any()
        private var lastRequestAt = 0L

        override fun intercept(chain: Interceptor.Chain): Response {
            synchronized(lock) {
                val now = System.currentTimeMillis()
                val wait = lastRequestAt + minIntervalMs - now
                if (wait > 0) {
                    try {
                        Thread.sleep(wait)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                }
                lastRequestAt = System.currentTimeMillis()
            }
            return chain.proceed(chain.request())
        }
    }
}
