package no.uio.ifi.in2000.team20.team20app

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team20.team20app.BuildConfig.FROST_V0_CLIENT_ID
import no.uio.ifi.in2000.team20.team20app.BuildConfig.FROST_V0_CLIENT_SECRET
import no.uio.ifi.in2000.team20.team20app.data.api.FrostClientProvider
import no.uio.ifi.in2000.team20.team20app.data.api.NveZonesClientProvider
import no.uio.ifi.in2000.team20.team20app.data.datasource.FrostDataSource
import no.uio.ifi.in2000.team20.team20app.data.datasource.NveZonesRemoteDataSource
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.ExposureCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.ExposureCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.HazardCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.HazardCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.TotalScoreCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.TotalScoreCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.VulnerabilityCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.VulnerabilityCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.NveZonesRepository
import no.uio.ifi.in2000.team20.team20app.domain.usecase.GetExposureScore
import no.uio.ifi.in2000.team20.team20app.domain.usecase.GetGeoScore
import no.uio.ifi.in2000.team20.team20app.domain.usecase.GetHazardScore
import no.uio.ifi.in2000.team20.team20app.domain.usecase.getVulnerabilityScore
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Integration tests for GetGeoScore.
 * Makes real API calls to Frost and NVE – requires internet connection.
 */
class GeoScoreTest {

    companion object {
        private val credentials = "${FROST_V0_CLIENT_ID}:${FROST_V0_CLIENT_SECRET}"

        private val frostRepo = FrostRepository(
            FrostDataSource(client = FrostClientProvider.client, credentials = credentials)
        )
        private val nveRepo = NveZonesRepository(
            NveZonesRemoteDataSource(NveZonesClientProvider.client)
        )

        private val fakeHazardDao = object : HazardCacheDao {
            private val cache = mutableMapOf<String, HazardCacheEntity>()
            override suspend fun getByKey(locationKey: String) = cache[locationKey]
            override suspend fun insert(entity: HazardCacheEntity) { cache[entity.locationKey] = entity }
        }

        private val fakeExposureDao = object : ExposureCacheDao {
            private val cache = mutableMapOf<String, ExposureCacheEntity>()
            override suspend fun getByKey(locationKey: String) = cache[locationKey]
            override suspend fun insert(entity: ExposureCacheEntity) { cache[entity.locationKey] = entity }
        }

        private val fakeVulnerabilityDao = object : VulnerabilityCacheDao {
            private val cache = mutableMapOf<String, VulnerabilityCacheEntity>()
            override suspend fun getByKey(locationKey: String) = cache[locationKey]
            override suspend fun insert(entity: VulnerabilityCacheEntity) { cache[entity.locationKey] = entity }
        }

        private val fakeTotalScoreDao = object : TotalScoreCacheDao {
            private val cache = mutableMapOf<String, TotalScoreCacheEntity>()
            override suspend fun getByKey(locationKey: String) = cache[locationKey]
            override suspend fun insert(entity: TotalScoreCacheEntity) { cache[entity.locationKey] = entity }
        }

        val geoScore = GetGeoScore(
            getExposureScore = GetExposureScore(frostRepo, fakeExposureDao),
            getHazardScore = GetHazardScore(frostRepo, fakeHazardDao),
            getVulnerabilityScore = getVulnerabilityScore(nveRepo, fakeVulnerabilityDao),
            geoScoreDAO = fakeTotalScoreDao
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
