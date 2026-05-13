package no.uio.ifi.in2000.team20.team20app.data.datasource

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.takeFrom
import io.ktor.utils.io.CancellationException
import no.uio.ifi.in2000.team20.team20app.data.dto.AddressResponse
import no.uio.ifi.in2000.team20.team20app.di.GeoSearchClient
import no.uio.ifi.in2000.team20.team20app.domain.model.AddressResponseWrapper
import no.uio.ifi.in2000.team20.team20app.util.Constants.ADDRESS_URL_FORMATTER
import no.uio.ifi.in2000.team20.team20app.util.Constants.CANCELLED_SEARCH
import no.uio.ifi.in2000.team20.team20app.util.Constants.HTTP_CLIENT_ERROR
import no.uio.ifi.in2000.team20.team20app.util.Constants.HTTP_OK
import no.uio.ifi.in2000.team20.team20app.util.Constants.HTTP_SERVER_ERROR
import no.uio.ifi.in2000.team20.team20app.util.Constants.NO_INTERNET
import java.net.URLEncoder
import javax.inject.Inject


interface AddressApiService {
    suspend fun searchAddress(query: String): AddressResponseWrapper
}

class AddressRemoteDataSource @Inject constructor(
    @GeoSearchClient private val client: HttpClient
) : AddressApiService {
    override suspend fun searchAddress(query: String): AddressResponseWrapper {

        val encodedQuery = URLEncoder.encode(query, "UTF-8")

        Log.d("GeoSearch", "Encoded query: $encodedQuery")

        val formattedUrl = ADDRESS_URL_FORMATTER(encodedQuery)

        try {
            val response = client.get{ url.takeFrom(formattedUrl)} // Prevent double encoding
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
                Log.d("GeoSearch", "Adresse API-kall feilet. Statuskode: ${response.status.value}")

                throw Exception("Adresse API-kall feilet. Statuskode: ${response.status.value}")
            }
        } catch (e: CancellationException){
            Log.d("GeoSearch", "Adresse API-kall ble avbrutt. ${e.message}")
            return AddressResponseWrapper(
                metadata = null,
                addresses = emptyList(),
                status = CANCELLED_SEARCH
            )
        } catch (e: Exception) {
            Log.d("GeoSearch", "Adresse API-kall feilet. ${e.message}")
            return AddressResponseWrapper(
                metadata = null,
                addresses = emptyList(),
                status = NO_INTERNET
            )
        }
    }
}