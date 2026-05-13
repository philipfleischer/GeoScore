package no.uio.ifi.in2000.team20.team20app.data.repository

import no.uio.ifi.in2000.team20.team20app.data.datasource.ChatGPTRemoteDataSource
import no.uio.ifi.in2000.team20.team20app.data.local.dao.ReportCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.entity.ReportCacheEntity
import no.uio.ifi.in2000.team20.team20app.domain.model.GeoScore
import no.uio.ifi.in2000.team20.team20app.domain.model.Report
import javax.inject.Inject

interface ChatGPTRepository {
    suspend fun getAiGeneratedReport(geoScore: GeoScore): Report
    suspend fun generateReport(geoScore: GeoScore): Report

}

class ChatGPTRepositoryImpl @Inject constructor(
    private val dataSource: ChatGPTRemoteDataSource,
    private val reportCacheDao: ReportCacheDao
) : ChatGPTRepository {

    override suspend fun getAiGeneratedReport(geoScore: GeoScore): Report {
        val result = dataSource.getReport(geoScore)
        val parts = result?.split("---")
        return Report(
            locationKey = geoScore.locationKey,
            extremePrecipitationText = parts?.getOrNull(0)?.trim() ?: "",
            extremeWindText = parts?.getOrNull(1)?.trim() ?: "",
            floodText = parts?.getOrNull(2)?.trim() ?: "",
            landslideText = parts?.getOrNull(3)?.trim() ?: ""
        )
    }

    /**
     * Generates an AI report for the given GeoScore, using the Room cache if available
     * Caching is handled here in the repository. callers don't need to know about it
     */
    override suspend fun generateReport(geoScore: GeoScore): Report {
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

        val report = getAiGeneratedReport(geoScore)
        reportCacheDao.insert(
            ReportCacheEntity(
                locationKey = report.locationKey,
                extremePrecipitationText = report.extremePrecipitationText,
                extremeWindText = report.extremeWindText,
                floodText = report.floodText,
                landslideText = report.landslideText
            )
        )
        return report
    }


}
