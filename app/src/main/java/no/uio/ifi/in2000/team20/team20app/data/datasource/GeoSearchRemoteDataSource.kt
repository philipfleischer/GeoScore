package no.uio.ifi.in2000.team20.team20app.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team20.team20app.data.api.GeoSearchApiService
import no.uio.ifi.in2000.team20.team20app.data.model.GeoResponse
import java.net.URLEncoder

class GeoSearchRemoteDataSource(
    private val client: HttpClient
) : GeoSearchApiService {
    override suspend fun searchLocationWithQuery(
        query: String,
        lat: Double?,
        lon: Double?
    ): GeoResponse {
        if (query.isEmpty() && lat != null && lon != null) {
            return searchLocationWithCoordinates(lat, lon)
        } else if (query.isEmpty()) {
            throw Exception("query is empty and no coordinates have been given")
        }

        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "https://api.kartverket.no/stedsnavn/v1/navn?sok=$encodedQuery&fuzzy=true"

        val response = client.get(url)

        if (response.status.value == 200) {
            val body: GeoResponse = response.body()
            return body
        } else {
            throw Exception("The Api call dident work. status code: ${response.status.value}")
        }
    }

    override suspend fun searchLocationWithCoordinates(
        lat: Double,
        lon: Double
    ): GeoResponse {
        // radius på 100 (antokk et hus ikke vil ha en radius på mer enn 100m)
        val url = "https://api.kartverket.no/stedsnavn/v1/punkt?nord=$lat&ost=$lon&koordsys=4258&radius=100"

        val response = client.get(url)

        if (response.status.value == 200) {
            val body: GeoResponse = response.body()
            return body
        } else {
            throw Exception("The Api call dident work. status code: ${response.status.value}")
        }
    }
}