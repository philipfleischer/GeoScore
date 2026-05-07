package no.uio.ifi.in2000.team20.team20app.domain.usecase

import no.uio.ifi.in2000.team20.team20app.data.repository.ChatGPTRepository
import no.uio.ifi.in2000.team20.team20app.domain.model.GeoScore
import no.uio.ifi.in2000.team20.team20app.domain.model.Report
import javax.inject.Inject

class GetAiReport @Inject constructor(
    private val chatGPTRepository: ChatGPTRepository
) {
    suspend fun generateReport(geoScore: GeoScore): Report =
        chatGPTRepository.generateReport(geoScore)
}
