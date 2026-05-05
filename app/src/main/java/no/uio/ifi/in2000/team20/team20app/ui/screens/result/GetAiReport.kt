package no.uio.ifi.in2000.team20.team20app.ui.screens.result

import no.uio.ifi.in2000.team20.team20app.data.repository.ChatGPTRepository
import no.uio.ifi.in2000.team20.team20app.domain.model.GeoScore
import no.uio.ifi.in2000.team20.team20app.domain.model.Report

class GetAiReport(private val chatGPTRepository: ChatGPTRepository) {
    suspend fun generateReport(geoScore: GeoScore): Report {
        return chatGPTRepository.getAiGeneratedReport(geoScore)
    }
}
