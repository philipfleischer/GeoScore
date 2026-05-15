package no.uio.ifi.in2000.team20.team20app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import no.uio.ifi.in2000.team20.team20app.data.local.dao.SavedLocationDao
import no.uio.ifi.in2000.team20.team20app.data.local.entity.SavedLocationEntity
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.LocationWithGeoscore
import javax.inject.Inject

interface SavedRepository {
    fun getAllSaved(): Flow<List<LocationWithGeoscore>>
    suspend fun addSaved(locationWrapper: LocationWithGeoscore)
    suspend fun removeSaved(location: Location)
    suspend fun isSaved(location: Location): Boolean
}

class SavedRepositoryImpl @Inject constructor(
    private val dao: SavedLocationDao
) : SavedRepository {

    override fun getAllSaved(): Flow<List<LocationWithGeoscore>> {
        return dao.getAllSaved().map { entities ->
            entities.map { entity ->
                LocationWithGeoscore(
                    location =
                Location(
                    address = entity.address,
                    name = entity.name,
                    municipality = entity.municipality,
                    county = entity.county,
                    lat = entity.lat,
                    lon = entity.lon,
                    savedAt = entity.savedAt
                ),
                    geoscore = entity.geoScore
                )
            }
        }
    }

    override suspend fun addSaved(locationWrapper: LocationWithGeoscore) {
        dao.insertSaved(
            SavedLocationEntity(
                address = locationWrapper.location.address,
                name = locationWrapper.location.name,
                municipality = locationWrapper.location.municipality,
                county = locationWrapper.location.county,
                lat = locationWrapper.location.lat,
                lon = locationWrapper.location.lon,
                savedAt = System.currentTimeMillis(),
                geoScore = locationWrapper.geoscore
            )
        )
    }

    override suspend fun removeSaved(location: Location) {
        dao.deleteSavedByAddress(location.address)
    }

    override suspend fun isSaved(location: Location): Boolean {
        return dao.isSaved(location.address)
    }
}
