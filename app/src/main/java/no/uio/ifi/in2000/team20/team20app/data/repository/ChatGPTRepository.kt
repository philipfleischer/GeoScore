package no.uio.ifi.in2000.team20.team20app.data.repository

import no.uio.ifi.in2000.team20.team20app.data.datasource.ChatGPTRemoteDataSource
import no.uio.ifi.in2000.team20.team20app.domain.model.GeoScore
import no.uio.ifi.in2000.team20.team20app.domain.model.Report

class ChatGPTRepository (
    private val DataSource: ChatGPTRemoteDataSource
)
{
    suspend fun getAiGeneratedReport(geoScore: GeoScore): Report {
        val result = DataSource.getReport(geoScore)
        val parts = result?.split("---")
        return Report(
            locationKey = geoScore.locationKey,
            extremePrecipitationText = parts?.getOrNull(0)?.trim() ?: "",
            extremeWindText = parts?.getOrNull(1)?.trim() ?: "",
            floodText = parts?.getOrNull(2)?.trim() ?: "",
            landslideText = parts?.getOrNull(3)?.trim() ?: ""
        )
    }
}