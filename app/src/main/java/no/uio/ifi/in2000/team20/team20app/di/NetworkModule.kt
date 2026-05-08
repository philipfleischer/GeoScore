package no.uio.ifi.in2000.team20.team20app.di

import com.aallam.openai.client.OpenAI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team20.team20app.util.Constants
import javax.inject.Qualifier
import javax.inject.Singleton

// ─── Qualifiers ───────────────────────────────────────────────────────────────

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FrostClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeoSearchClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NveZonesClient

// ─── Module ───────────────────────────────────────────────────────────────────

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @FrostClient
    @Provides
    @Singleton
    fun provideFrostHttpClient(): HttpClient = HttpClient(CIO) {
        // Frost returns JSON error bodies with content-type: text/plain — register both
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
            json(Json { ignoreUnknownKeys = true }, contentType = ContentType.Text.Plain)
        }
        // Do not throw on non-2xx — datasource checks status and throws explicitly
        expectSuccess = false
        defaultRequest {
            header("User-Agent", "IN2000-Team20 jeryosa@uio.no")
        }
    }

    @GeoSearchClient
    @Provides
    @Singleton
    fun provideGeoSearchHttpClient(): HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        defaultRequest {
            header(HttpHeaders.Accept, "application/json")
        }
    }

    @NveZonesClient
    @Provides
    @Singleton
    fun provideNveZonesHttpClient(): HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        defaultRequest {
            header(HttpHeaders.Accept, "application/json")
        }
    }

    @Provides
    @Singleton
    fun provideOpenAIClient(): OpenAI = OpenAI(Constants.CHATGPT_API_KEY)
}
