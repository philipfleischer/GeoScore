package no.uio.ifi.in2000.team20.team20app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.SavedLocationDao
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.SavedLocationEntity
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import javax.inject.Inject

interface SavedRepository {
    fun getAllSaved(): Flow<List<Location>>
    suspend fun addSaved(location: Location)
    suspend fun removeSaved(location: Location)
    suspend fun isSaved(location: Location): Boolean
}

class SavedRepositoryImpl @Inject constructor(
    private val dao: SavedLocationDao
) : SavedRepository {

    override fun getAllSaved(): Flow<List<Location>> {
        return dao.getAllSaved().map { entities ->
            entities.map { entity ->
                Location(
                    address = entity.address,
                    name = entity.name,
                    municipality = entity.municipality,
                    county = entity.county,
                    lat = entity.lat,
                    lon = entity.lon,
                    savedAt = entity.savedAt
                )
            }
        }
    }

    override suspend fun addSaved(location: Location) {
        dao.insertSaved(
            SavedLocationEntity(
                address = location.address,
                name = location.name,
                municipality = location.municipality,
                county = location.county,
                lat = location.lat,
                lon = location.lon,
                savedAt = System.currentTimeMillis()
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
