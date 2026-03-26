package no.uio.ifi.in2000.team20.team20app.data.repository

import no.uio.ifi.in2000.team20.team20app.data.datasource.GeoSearchRemoteDataSource
import no.uio.ifi.in2000.team20.team20app.data.model.FylkerItemsDto
import no.uio.ifi.in2000.team20.team20app.data.model.GeoResponse
import no.uio.ifi.in2000.team20.team20app.data.model.KommuneItemsDto
import no.uio.ifi.in2000.team20.team20app.data.model.NavnItemsDto
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.domain.model.SearchResult

interface GeoSearchRepositoryService {
    suspend fun getSearchResults(query: String, lat: Double? = null, lon: Double? = null): SearchResult
}

class GeoSearchRepository(
    private val datasource: GeoSearchRemoteDataSource
) : GeoSearchRepositoryService {
    private fun NavnItemsDto.toDomain(): String {
        return skrivemåte
    }

    private fun FylkerItemsDto.toDomain(): String {
        return fylkesnavn
    }

    private fun KommuneItemsDto.toDomain(): String {
        return kommunenavn
    }

    private fun GeoResponse.toDomain(): SearchResult {
        val listOfResults = navn.mapNotNull {
            val navn = it.toDomain()
            val fylke = it.fylker.firstOrNull()?.toDomain()
            val kommune = it.kommuner.firstOrNull()?.toDomain()
            val nord = it.representasjonspunkt.nord
            val øst = it.representasjonspunkt.øst
            //TODO: look at this again
            Location(
                address = navn,
                municipality = kommune,
                county = fylke,
                lat = nord,
                lon = øst
            )
        }

        return SearchResult(listOfResults)
    }

    override suspend fun getSearchResults(
        query: String,
        lat: Double?,
        lon: Double?
    ): SearchResult {
        val geoResponse = datasource.searchLocationWithQuery(query, lat, lon)
        return geoResponse.toDomain()
    }
}