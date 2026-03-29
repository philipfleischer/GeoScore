package no.uio.ifi.in2000.team20.team20app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import no.uio.ifi.in2000.team20.team20app.data.local.FavoriteLocationDao
import no.uio.ifi.in2000.team20.team20app.data.local.FavoriteLocationEntity
import no.uio.ifi.in2000.team20.team20app.domain.model.Location

class FavoritesRepository(
    private val dao: FavoriteLocationDao
) {
    fun getAllFavorites(): Flow<List<Location>> {
        return dao.getAllFavorites().map { entities ->
            entities.map { entity ->
                Location(
                    address = entity.address,
                    name = entity.name,
                    municipality = entity.municipality,
                    county = entity.county,
                    lat = entity.lat,
                    lon = entity.lon
                )
            }
        }
    }

    suspend fun addFavorite(location: Location) {
        dao.insertFavorite(
            FavoriteLocationEntity(
                address = location.address,
                name = location.name,
                municipality = location.municipality,
                county = location.county,
                lat = location.lat,
                lon = location.lon
            )
        )
    }

    suspend fun removeFavorite(location: Location) {
        dao.deleteFavoriteByAddress(location.address)
    }

    suspend fun isFavorite(location: Location): Boolean {
        return dao.isFavorite(location.address)
    }
}