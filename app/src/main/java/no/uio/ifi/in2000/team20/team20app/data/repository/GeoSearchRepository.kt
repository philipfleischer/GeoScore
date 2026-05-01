package no.uio.ifi.in2000.team20.team20app.data.repository

import android.util.Log
import no.uio.ifi.in2000.team20.team20app.data.datasource.AddressRemoteDataSource
import no.uio.ifi.in2000.team20.team20app.data.model.Address
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.domain.model.SearchResult

interface GeoSearchRepositoryService {
    suspend fun getSearchResults(query: String, lat: Double? = null, lon: Double? = null): SearchResult
}

class GeoSearchRepository(
    private val addressDatasource: AddressRemoteDataSource
) : GeoSearchRepositoryService {

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

        //Geonorge-adresse-API returns error eror on short queryes, catch the error here and return an emtpty list
        val addresses = try {
            addressDatasource.searchAddress(query).adresser.mapNotNull { it.toDomain() }
        } catch (e: Exception) {
            Log.d("GeoSearch", "Address API failed for query \"$query\": ${e.message}")
            emptyList()
        }


        Log.d("GeoSearch", "addresses: ${addresses.size}")

        return SearchResult(addresses)


    }
}