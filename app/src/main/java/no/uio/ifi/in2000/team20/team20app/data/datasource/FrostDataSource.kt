package no.uio.ifi.in2000.team20.team20app.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.util.encodeBase64
import no.uio.ifi.in2000.team20.team20app.data.api.FrostRoutes
import no.uio.ifi.in2000.team20.team20app.data.model.FrostObservationResponseDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostSourceDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostSourceResponseDto
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Interface for Frost API data fetching.
 *
 * Responsibility:
 * - Define the contract for getting stations and observations
 *
 * Why:
 * Makes FrostDataSource testable by allowing fake implementations in tests.
 */
interface FrostDataSourceService {
    suspend fun getStation(lat: Double, lon: Double): FrostSourceDto
    suspend fun getObservations(stationId: String): FrostObservationResponseDto
}

/**
 * Handles remote climate data retrieval from the Frost API.
 *
 * Responsibility:
 * - Call Frost API endpoints
 * - Return raw DTO data
 *
 * Why:
 * Abstracts network layer away from the repository layer.
 */
class FrostDataSource(
    private val client: HttpClient,
    private val credentials: String // format: "clientId:clientSecret"
) : FrostDataSourceService {

    private val authHeader: String
        get() = "Basic " + credentials.encodeBase64()

    // Get nearest weather station based on coordinates
    override suspend fun getStation(lat: Double, lon: Double): FrostSourceDto {
        val response: FrostSourceResponseDto = client.get(FrostRoutes.SOURCES) {
            parameter("geometry", "nearest(POINT($lon $lat))")
            parameter("types", "SensorSystem")
            header("Authorization", authHeader)
        }.body()

        return response.data.firstOrNull()
            ?: throw NoSuchElementException("No station found near ($lat, $lon)")
    }

    // Get monthly climate observations for a given station
    override suspend fun getObservations(stationId: String): FrostObservationResponseDto {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val endTime = formatter.format(Calendar.getInstance().time)
        val startTime = formatter.format(
            Calendar.getInstance().apply { add(Calendar.YEAR, -1) }.time
        )

        val response: FrostObservationResponseDto = client.get(FrostRoutes.OBSERVATIONS) {
            parameter("sources", stationId)
            parameter(
                "elements",
                listOf(
                    "mean(air_temperature P1M)",
                    "best_estimate_sum(precipitation_amount P1M)"
                ).joinToString(",")
            )
            parameter("referencetime", "$startTime/$endTime")
            parameter("levels", "default")
            parameter("timeoffsets", "default")
            header("Authorization", authHeader)
        }.body()

        return response
    }
}
