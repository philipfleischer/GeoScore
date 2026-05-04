package no.uio.ifi.in2000.team20.team20app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import no.uio.ifi.in2000.team20.team20app.data.local.SavedLocationDao
import no.uio.ifi.in2000.team20.team20app.data.local.SavedLocationEntity
import no.uio.ifi.in2000.team20.team20app.domain.model.Location

class SavedRepository(
    private val dao: SavedLocationDao
) {
    fun getAllSaved(): Flow<List<Location>> {
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

    suspend fun addSaved(location: Location) {
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

    suspend fun removeSaved(location: Location) {
        dao.deleteSavedByAddress(location.address)
    }

    suspend fun isSaved(location: Location): Boolean {
        return dao.isSaved(location.address)
    }
}