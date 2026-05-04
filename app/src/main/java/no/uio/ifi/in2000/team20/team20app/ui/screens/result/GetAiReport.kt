package no.uio.ifi.in2000.team20.team20app.ui.screens.result

import no.uio.ifi.in2000.team20.team20app.domain.model.Report

//contains the Ai response for the report
class GetAiReport{
    //ChatGPT kall that returns a report struct
    suspend fun generateReport(hazardScore: Double, exposureScore: Double, vulnerabilityScore: Double, grade: String): Report
    {
        //TODO
        return Report(
            extremePrecipitationText = "",
            extremeWindText = "",
            floodText = "",
            landslideText = ""
        )
    }
}