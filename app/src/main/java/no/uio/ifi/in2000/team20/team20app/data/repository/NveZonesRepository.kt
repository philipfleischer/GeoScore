package no.uio.ifi.in2000.team20.team20app.data.repository

import no.uio.ifi.in2000.team20.team20app.data.datasource.NveZonesRemoteDataSourceService
import javax.inject.Inject

interface NveZonesRepositoryService {
    suspend fun isInLandslideZone(lat: Double, lon: Double): Boolean
    suspend fun isInFloodZone(lat: Double, lon: Double): Boolean
}

class NveZonesRepository @Inject constructor(
    private val nveZonesDataSource: NveZonesRemoteDataSourceService
) : NveZonesRepositoryService {
    override suspend fun isInLandslideZone(lat: Double, lon: Double): Boolean {
        val response = nveZonesDataSource.getLandslideZoneData(lon, lat)
        
        //Returns true if in zone and false if not in zone
        return response.features.isNotEmpty()
    }

    override suspend fun isInFloodZone(lat: Double, lon: Double): Boolean {
        val response = nveZonesDataSource.getFloodZoneData(lon, lat)

        //Returns true if in zone and false if not in zone
        return response.features.isNotEmpty()
    }

}