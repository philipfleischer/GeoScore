package no.uio.ifi.in2000.team20.team20app.data.repository

import android.util.Log
import no.uio.ifi.in2000.team20.team20app.data.datasource.AddressRemoteDataSource
import no.uio.ifi.in2000.team20.team20app.data.datasource.LocationRemoteDatasource
import no.uio.ifi.in2000.team20.team20app.data.model.Address
import no.uio.ifi.in2000.team20.team20app.data.model.FylkerItemsDto
import no.uio.ifi.in2000.team20.team20app.data.model.LocationResponse
import no.uio.ifi.in2000.team20.team20app.data.model.KommuneItemsDto
import no.uio.ifi.in2000.team20.team20app.data.model.NavnItemsDto
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.domain.model.SearchResult

interface GeoSearchRepositoryService {
    suspend fun getSearchResults(query: String, lat: Double? = null, lon: Double? = null): SearchResult
}

class GeoSearchRepository(
    private val locationDatasource: LocationRemoteDatasource,
    private val addressDatasource: AddressRemoteDataSource
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

    private fun LocationResponse.toDomain(): SearchResult {
        val listOfResults = navn.mapNotNull {
            val navn = it.toDomain()
            val fylke = it.fylker.firstOrNull()?.toDomain()
            val kommune = it.kommuner.firstOrNull()?.toDomain()
            val nord = it.representasjonspunkt.nord
            val øst = it.representasjonspunkt.øst

            Location(
                name = navn,
                kommune = kommune,
                fylke = fylke,
                lat = nord,
                lon = øst
            )
        }

        return SearchResult(listOfResults)
    }
    private fun Address.toDomain(): Location {
        return Location(
            name = adressetekst,
            kommune = kommunenavn,
            fylke = null,
            lat = representasjonspunkt?.lat,
            lon = representasjonspunkt?.lon
        )
    }

    override suspend fun getSearchResults(
        query: String,
        lat: Double?,
        lon: Double?
    ): SearchResult {
        val locations = locationDatasource.searchLocationWithQuery(query, lat, lon)
            .toDomain().locations

        val addresses = addressDatasource.searchAddress(query)
            .adresser.map { it.toDomain() }


        //to not show same results from API
        val combined = (addresses + locations).distinctBy { Pair(it.lat, it.lon) }


        return SearchResult(combined)


    }
}