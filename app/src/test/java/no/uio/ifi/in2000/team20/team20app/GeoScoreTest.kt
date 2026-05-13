package no.uio.ifi.in2000.team20.team20app

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team20.team20app.data.datasource.FrostDataSource
import no.uio.ifi.in2000.team20.team20app.data.datasource.NveZonesRemoteDataSource
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.PrecipitationCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.SnowCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.SunshineCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.TemperatureCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.WindCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.PrecipitationCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.SnowCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.SunshineCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.TemperatureCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.WindCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.NveZonesRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.ScoreCacheRepository
import no.uio.ifi.in2000.team20.team20app.domain.model.ExposureScoreResult
import no.uio.ifi.in2000.team20.team20app.domain.model.GeoScore
import no.uio.ifi.in2000.team20.team20app.domain.model.HazardScoreResult
import no.uio.ifi.in2000.team20.team20app.domain.model.VulnerabilityScoreResult
import no.uio.ifi.in2000.team20.team20app.domain.usecase.GetExposureScore
import no.uio.ifi.in2000.team20.team20app.domain.usecase.GetGeoScore
import no.uio.ifi.in2000.team20.team20app.domain.usecase.GetHazardScore
import no.uio.ifi.in2000.team20.team20app.domain.usecase.GetVulnerabilityScore
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Integration tests for GetGeoScore.
 * Makes real API calls to Frost and NVE – requires internet connection.
 */
class GeoScoreTest {

    companion object {
        private val frostClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
                json(Json { ignoreUnknownKeys = true }, contentType = ContentType.Text.Plain)
            }
            expectSuccess = false
            defaultRequest { header("User-Agent", "IN2000-Team20 jeryosa@uio.no") }
        }

        private val nveClient = HttpClient(CIO) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            defaultRequest { header(HttpHeaders.Accept, "application/json") }
        }

        // Fake in-memory DAOs for FrostRepository — no Room dependency needed in tests
        private val fakeTemperatureDao = object : TemperatureCacheDao {
            private val cache = mutableMapOf<String, TemperatureCacheEntity>()
            override suspend fun getByKey(stationId: String) = cache[stationId]
            override suspend fun insert(entity: TemperatureCacheEntity) { cache[entity.stationId] = entity }
        }
        private val fakeWindDao = object : WindCacheDao {
            private val cache = mutableMapOf<String, WindCacheEntity>()
            override suspend fun getByKey(stationId: String) = cache[stationId]
            override suspend fun insert(entity: WindCacheEntity) { cache[entity.stationId] = entity }
        }
        private val fakeSunshineDao = object : SunshineCacheDao {
            private val cache = mutableMapOf<String, SunshineCacheEntity>()
            override suspend fun getByKey(stationId: String) = cache[stationId]
            override suspend fun insert(entity: SunshineCacheEntity) { cache[entity.stationId] = entity }
        }
        private val fakeSnowDao = object : SnowCacheDao {
            private val cache = mutableMapOf<String, SnowCacheEntity>()
            override suspend fun getByKey(stationId: String) = cache[stationId]
            override suspend fun insert(entity: SnowCacheEntity) { cache[entity.stationId] = entity }
        }
        private val fakePrecipitationDao = object : PrecipitationCacheDao {
            private val cache = mutableMapOf<String, PrecipitationCacheEntity>()
            override suspend fun getByKey(stationId: String) = cache[stationId]
            override suspend fun insert(entity: PrecipitationCacheEntity) { cache[entity.stationId] = entity }
        }

        // Single fake ScoreCacheRepository — replaces the 4 individual score DAO fakes
        private val fakeScoreCacheRepository = object : ScoreCacheRepository {
            private val hazardCache = mutableMapOf<String, HazardScoreResult>()
            private val exposureCache = mutableMapOf<String, ExposureScoreResult>()
            private val vulnerabilityCache = mutableMapOf<String, VulnerabilityScoreResult>()
            private val totalScoreCache = mutableMapOf<String, GeoScore>()

            override suspend fun getHazardCache(locationKey: String) = hazardCache[locationKey]
            override suspend fun saveHazardScore(locationKey: String, result: HazardScoreResult) { hazardCache[locationKey] = result }

            override suspend fun getExposureCache(locationKey: String) = exposureCache[locationKey]
            override suspend fun saveExposureScore(locationKey: String, result: ExposureScoreResult) { exposureCache[locationKey] = result }

            override suspend fun getVulnerabilityCache(locationKey: String) = vulnerabilityCache[locationKey]
            override suspend fun saveVulnerabilityScore(locationKey: String, result: VulnerabilityScoreResult, isInFloodZone: Boolean, isInLandslideZone: Boolean) { vulnerabilityCache[locationKey] = result }

            override suspend fun getGeoScoreCache(locationKey: String) = totalScoreCache[locationKey]
            override suspend fun saveGeoScore(geoScore: GeoScore) { totalScoreCache[geoScore.locationKey] = geoScore }
        }

        private val frostRepo = FrostRepository(
            dataSource = FrostDataSource(frostClient),
            temperatureCacheDao = fakeTemperatureDao,
            windCacheDao = fakeWindDao,
            sunshineCacheDao = fakeSunshineDao,
            snowCacheDao = fakeSnowDao,
            precipitationCacheDao = fakePrecipitationDao
        )
        private val nveRepo = NveZonesRepository(
            NveZonesRemoteDataSource(nveClient)
        )

        val geoScore = GetGeoScore(
            getExposureScore = GetExposureScore(frostRepo, fakeScoreCacheRepository),
            getHazardScore = GetHazardScore(frostRepo, fakeScoreCacheRepository),
            getVulnerabilityScore = GetVulnerabilityScore(nveRepo, fakeScoreCacheRepository),
            scoreCacheRepository = fakeScoreCacheRepository
        )
    }

    // Oslo-koordinater – generell test
    private val Oslolat = 59.91
    private val Oslolon = 10.74
    private val Bergenlat = 60.3913
    private val Bergenlon = 5.3221

    // Koordinater i flomsone (Lillestrøm-området)
    private val flomLat = 59.9554
    private val flomLon = 11.0488

    @Test
    fun calculateGeoScoreReturnNotNull() = runBlocking {
        val result = geoScore.calculateGeoScore(Oslolat, Oslolon)
        println("HazardScore: ${result.hazardScore}")
        println("ExposureScore: ${result.exposureScore}")
        println("VulnerabilityScore: ${result.vulnerabilityScore}")
        println("GeoScore: $result")
        assertNotNull(result)
    }

    @Test
    fun calculateGeoScoreErMellom0Og100() = runBlocking {
        val result = geoScore.calculateGeoScore(Bergenlat, Bergenlon)
        assertTrue("Score skal være >= 0, var: $result", result.geoScore >= 0.0)
        assertTrue("Score skal være <= 100, var: $result", result.geoScore <= 100.0)
    }

    @Test
    fun calculateGeoScoreIFlomsoneErMellom0Og100() = runBlocking {
        val result = geoScore.calculateGeoScore(flomLat, flomLon)
        print(result)
        assertTrue("Score skal være >= 0, var: $result", result.geoScore >= 0.0)
        assertTrue("Score skal være <= 100, var: $result", result.geoScore <= 100.0)
    }

    @Test
    fun calculateGeoScoreIFlomsoneErHoyereEnnUtenSone() = runBlocking {
        val scoreUtenSone = geoScore.calculateGeoScore(Oslolat, Oslolon)
        val scoreIFlomsone = geoScore.calculateGeoScore(flomLat, flomLon)
        assertTrue(
            "Score i flomsone ($scoreIFlomsone) skal være høyere enn utenfor ($scoreUtenSone)",
            scoreIFlomsone.geoScore > scoreUtenSone.geoScore
        )
    }
}
