package no.uio.ifi.in2000.team20.team20app.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.utils.io.CancellationException
import no.uio.ifi.in2000.team20.team20app.data.dto.AddressResponse
import no.uio.ifi.in2000.team20.team20app.di.GeoSearchClient
import no.uio.ifi.in2000.team20.team20app.domain.model.AddressResponseWrapper
import no.uio.ifi.in2000.team20.team20app.util.Constants.CANCELLED_SEARCH
import no.uio.ifi.in2000.team20.team20app.util.Constants.HTTP_CLIENT_ERROR
import no.uio.ifi.in2000.team20.team20app.util.Constants.HTTP_OK
import no.uio.ifi.in2000.team20.team20app.util.Constants.HTTP_SERVER_ERROR
import no.uio.ifi.in2000.team20.team20app.util.Constants.NO_INTERNET
import javax.inject.Inject


interface AddressApiService {
    suspend fun searchAddress(query: String): AddressResponseWrapper
}

const val GEONORGE_BASE_URL = "https://ws.geonorge.no/adresser/v1/sok?"


class AddressRemoteDataSource @Inject constructor(
    @param:GeoSearchClient private val client: HttpClient
) : AddressApiService {
    override suspend fun searchAddress(query: String): AddressResponseWrapper {
        try {
            val response = client.get(GEONORGE_BASE_URL){
                parameter("sok", query)
                parameter("fuzzy", false)
                parameter("utkoordsys", 4258)
                parameter("treffPerSide", 10)
                parameter("side", 0)
                parameter("asciiKompatibel", true)
            }

            if (response.status.value == HTTP_OK) {
                val deserialized: AddressResponse = response.body()

                return AddressResponseWrapper(
                    metadata = deserialized.metadata,
                    addresses = deserialized.adresser,
                    status = HTTP_OK
                )
            } else if (response.status.value in HTTP_CLIENT_ERROR..<HTTP_SERVER_ERROR) {
                return AddressResponseWrapper(
                    metadata = null,
                    addresses = emptyList(),
                    status = HTTP_CLIENT_ERROR
                )
            } else if (response.status.value >= HTTP_SERVER_ERROR) {
                return AddressResponseWrapper(
                    metadata = null,
                    addresses = emptyList(),
                    status = HTTP_SERVER_ERROR
                )
            } else {
                throw Exception("Adresse API-kall feilet. Statuskode: ${response.status.value}")
            }
        } catch (_: CancellationException){
            return AddressResponseWrapper(
                metadata = null,
                addresses = emptyList(),
                status = CANCELLED_SEARCH
            )
        } catch (_: Exception) {
            return AddressResponseWrapper(
                metadata = null,
                addresses = emptyList(),
                status = NO_INTERNET
            )
        }
    }
}