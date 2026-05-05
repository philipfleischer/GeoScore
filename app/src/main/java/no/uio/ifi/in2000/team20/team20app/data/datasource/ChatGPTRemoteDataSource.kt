package no.uio.ifi.in2000.team20.team20app.data.datasource

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import no.uio.ifi.in2000.team20.team20app.domain.model.GeoScore
import no.uio.ifi.in2000.team20.team20app.domain.model.Report
import no.uio.ifi.in2000.team20.team20app.util.Constants

class ChatGPTRemoteDataSource {
    val openAI = OpenAI(Constants.CHATGPT_API_KEY)

    suspend fun getReport(geoScore: GeoScore): String? {

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    //Seperator is ---
                    content = "Du er en klimarisiko-ekspert som skriver korte, informative analyser for en mobilapp som vurderer klimarisiko for norske lokasjoner.\n" +
                            "\n" +
                            "Du får fire risikoscorer fra 0-100 for en lokasjon, der:\n" +
                            "- 0-20 = Svært lav risiko (karakter A)\n" +
                            "- 21-40 = Lav risiko (karakter B)\n" +
                            "- 41-60 = Moderat risiko (karakter C)\n" +
                            "- 61-80 = Høy risiko (karakter D)\n" +
                            "- 81-100 = Svært høy risiko (karakter E/F)\n" +
                            "\n" +
                            "Scorer for denne lokasjonen:\n" +
                            "- Nedbør: ${geoScore.precipitationScore}\n" +
                            "- Vind/storm: ${geoScore.windScore}\n" +
                            "- Flom: ${geoScore.floodScore}\n" +
                            "- Skred: ${geoScore.landslideScore}\n" +
                            "\n" +
                            "Skriv FIRE separate avsnitt, ett for hver risikotype, i denne rekkefølgen: nedbør, vind, flom, skred.\n" +
                            "\n" +
                            "Hvert avsnitt skal:\n" +
                            "- Være 2-3 setninger langt\n" +
                            "- Beskrive risikonivået basert på scoren\n" +
                            "- Forklare hva risikoen innebærer for området\n" +
                            "- Være skrevet på norsk i en saklig, informativ tone\n" +
                            "- IKKE inneholde overskrifter, punktlister, tall eller emojis\n" +
                            "- IKKE nevne selve scoren eller karakteren\n" +
                            "\n" +
                            "VIKTIG FORMATERING:\n" +
                            "Separer de fire avsnittene med nøyaktig denne separatoren på en egen linje:\n" +
                            "---\n" +
                            "\n" +
                            "Ikke skriv noe annet enn de fire avsnittene og separatorene mellom dem. Ikke inkluder separator før første avsnitt eller etter siste avsnitt.\n" +
                            "\n" +
                            "Eksempel på struktur:\n" +
                            "[Avsnitt om nedbør]\n" +
                            "---\n" +
                            "[Avsnitt om vind]\n" +
                            "---\n" +
                            "[Avsnitt om flom]\n" +
                            "---\n" +
                            "[Avsnitt om skred]")))

        val completion = openAI.chatCompletion(chatCompletionRequest)
        val response = completion.choices.first().message.content
        return response
    }

}
