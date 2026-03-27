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
    private fun Address.toDomain(): Location? {
        val lat = representasjonspunkt?.lat ?: return null
        val lon = representasjonspunkt?.lon ?: return null
        val name = adressetekst ?: adressenavn ?: return null
        return Location(
            address = name,
            municipality = kommunenavn,
            lat = lat,
            lon = lon
        )
    }

    override suspend fun getSearchResults(
        query: String,
        lat: Double?,
        lon: Double?
    ): SearchResult {
        val locations = locationDatasource.searchLocationWithQuery(query, lat, lon)
            .toDomain().locations

        //Geonorge-adresse-API returns error eror on short queryes, catch the error here and return an emtpty list
        val addresses = try {
            addressDatasource.searchAddress(query).adresser.mapNotNull { it.toDomain() }
        } catch (e: Exception) {
            Log.d("GeoSearch", "Address API failed for query \"$query\": ${e.message}")
            emptyList()
        }


        Log.d("GeoSearch", "addresses: ${addresses.size}, locations: ${locations.size}")

        //to not show same results from API
        val combined = (addresses + locations).distinctBy { Pair(it.lat, it.lon) }


        return SearchResult(combined)


    }
}