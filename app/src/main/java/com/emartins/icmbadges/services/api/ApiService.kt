package com.emartins.icmbadges.services.api

import com.emartins.icmbadges.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLDecoder
import java.util.concurrent.TimeUnit

object SharedCookieJar : CookieJar {
    private val cookies = mutableListOf<Cookie>()

    override fun saveFromResponse(url: HttpUrl, cookiesList: List<Cookie>) {
        synchronized(this) {
            for (cookie in cookiesList) {
                cookies.removeAll { it.name == cookie.name }
                cookies.add(cookie)
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> = cookies

    fun getXsrfToken(): String? {
        return cookies.find { it.name == "XSRF-TOKEN" }?.value
    }
}

class ApiService {
    val logging = HttpLoggingInterceptor().apply {
        // Nível de log: BODY mostra tudo (headers + corpo)
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient  = OkHttpClient.Builder()
        .cookieJar(SharedCookieJar)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val token = SharedCookieJar.getXsrfToken()
            val requestBuilder = chain.request().newBuilder()
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                //.addHeader("Origin", "https://portal.presbiterio.org.br")
                //.addHeader("Referer", "https://portal.presbiterio.org.br/mgestao")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36")

            token?.let {
                val decodedToken = URLDecoder.decode(it, "UTF-8")
                println("TOKEN: $decodedToken")
                requestBuilder.addHeader("X-XSRF-TOKEN", decodedToken)
            }

            val authorizedRequest = requestBuilder.build()

            chain.proceed(authorizedRequest)
        }
        .build()

    val gson: Gson = GsonBuilder()
        .serializeNulls()
        .create()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val api: ApiClient by lazy {
        retrofit.create(ApiClient::class.java)
    }

    suspend fun fetchFinalLink(initialUrl: String): Pair<String, List<String>> =
        withContext(Dispatchers.IO) { // roda em background
            val request = Request.Builder()
                .url(initialUrl)
                .get()
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                val finalUrl = response.request.url.toString()
                val cookies = response.headers("Set-Cookie")
                Pair(finalUrl, cookies)
            }
        }
}