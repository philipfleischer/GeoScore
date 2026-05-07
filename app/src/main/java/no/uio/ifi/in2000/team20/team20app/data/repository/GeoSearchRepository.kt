package no.uio.ifi.in2000.team20.team20app.data.repository

import no.uio.ifi.in2000.team20.team20app.data.datasource.AddressApiService
import no.uio.ifi.in2000.team20.team20app.data.dto.Address
import no.uio.ifi.in2000.team20.team20app.domain.model.AddressResponseWrapper
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.domain.model.SearchResult
import javax.inject.Inject

interface GeoSearchRepositoryService {
    suspend fun getSearchResults(query: String, lat: Double? = null, lon: Double? = null): SearchResult
}

class GeoSearchRepository @Inject constructor(
    private val addressDatasource: AddressApiService
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
        // SearchResult now contains the (potentially empty) list of locations and a status code
        val response: AddressResponseWrapper = addressDatasource.searchAddress(query)

       return SearchResult(
           locations = response.addresses.mapNotNull { it.toDomain() },
           status = response.status
       )
    }
}