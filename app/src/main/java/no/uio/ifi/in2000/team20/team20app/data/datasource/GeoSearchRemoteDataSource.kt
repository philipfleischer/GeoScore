package no.uio.ifi.in2000.team20.team20app.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team20.team20app.data.model.AddressResponse
import java.net.URLEncoder



interface AddressApiService {
    suspend fun searchAddress(query: String): AddressResponse
}

class AddressRemoteDataSource(
    private val client: HttpClient
): AddressApiService {
    override suspend fun searchAddress(query: String): AddressResponse {

        val encodedQuery = URLEncoder.encode(query, "UTF-8")

        val url = "https://ws.geonorge.no/adresser/v1/sok?sok=$encodedQuery&fuzzy=false&utkoordsys=4258&treffPerSide=10&side=0&asciiKompatibel=true"

        val response = client.get(url)

        if (response.status.value == 200) {
            return response.body()
        } else {
            throw Exception("Adresse API-kall feilet. Statuskode: ${response.status.value}")
        }
    }
}