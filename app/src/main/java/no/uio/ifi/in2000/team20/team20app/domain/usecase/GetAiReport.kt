package no.uio.ifi.in2000.team20.team20app.domain.usecase

import no.uio.ifi.in2000.team20.team20app.data.local.Dao.ReportCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.ReportCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.repository.ChatGPTRepository
import no.uio.ifi.in2000.team20.team20app.domain.model.GeoScore
import no.uio.ifi.in2000.team20.team20app.domain.model.Report

class GetAiReport(
    private val chatGPTRepository: ChatGPTRepository,
    private val reportCacheDao: ReportCacheDao
)
{
    suspend fun generateReport(geoScore: GeoScore): Report {
        val locationKey = geoScore.locationKey
        val cachedReport = reportCacheDao.getByKey(locationKey)

        if (cachedReport != null) {
            return Report(
                locationKey = cachedReport.locationKey,
                extremePrecipitationText = cachedReport.extremePrecipitationText,
                extremeWindText = cachedReport.extremeWindText,
                floodText = cachedReport.floodText,
                landslideText = cachedReport.landslideText
            )
        }

        val chatGPTResponse = chatGPTRepository.getAiGeneratedReport(geoScore)
        reportCacheDao.insert(
            ReportCacheEntity(
                locationKey = chatGPTResponse.locationKey,
                extremePrecipitationText = chatGPTResponse.extremePrecipitationText,
                extremeWindText = chatGPTResponse.extremeWindText,
                floodText = chatGPTResponse.floodText,
                landslideText = chatGPTResponse.landslideText
            )
        )

        return chatGPTResponse
    }
}