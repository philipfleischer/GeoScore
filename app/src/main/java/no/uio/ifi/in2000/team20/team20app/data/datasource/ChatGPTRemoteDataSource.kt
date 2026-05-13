package no.uio.ifi.in2000.team20.team20app.data.datasource

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import no.uio.ifi.in2000.team20.team20app.domain.model.GeoScore
import javax.inject.Inject

class ChatGPTRemoteDataSource @Inject constructor(
    private val openAI: OpenAI
) {

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
                            "- 0-16 = Svært lav risiko (karakter A)\n" +
                            "- 17-32 = Lav risiko (karakter B)\n" +
                            "- 33-49 = Moderat risiko (karakter C)\n" +
                            "- 50-66 = Høy risiko (karakter D)\n" +
                            "- 67-82 = Svært høy risiko (karakter E)\n" +
                            "- 83-100 = Kritisk risiko (karakter F)\n" +
                            "\n" +
                            "Scorer for denne lokasjonen:\n" +
                            "- Nedbør: ${geoScore.precipitationScore ?: "INGEN_DATA"}\n" +
                            "- Vind/storm: ${geoScore.windScore ?: "INGEN_DATA"}\n" +
                            "- Flom: ${geoScore.floodScore}\n" +
                            "- Skred: ${geoScore.landslideScore}\n" +
                            "\n" +
                            "Skriv FIRE separate avsnitt, ett for hver risikotype, i denne rekkefølgen: nedbør, vind, flom, skred.\n" +
                            "\n" +
                            "Hvert avsnitt skal:\n" +
                            "- Være 2-3 setninger langt\n" +
                            "- Beskrive risikonivået basert på scoren\n" +
                            "- Forklare hva risikoen innebærer for området og boliger\n" +
                            "- Være skrevet på norsk i en saklig, informativ tone\n" +
                            "- Kan ikke inneholde overskrifter.\n" +
                            "- Skal inneholde en punktliste av tiltak for boliger dersom karakteren for avsnittet er lavere enn en B\n" +
                            "- Kan nevne karakteren men ikke scoren\n" +
                            "- Hvis scoren for et avsnitt er 'INGEN_DATA', skriv KUN denne setningen for akkurat det avsnittet: 'Vi har ikke tilstrekkelig data til å beregne denne risikoen for dette stedet.' De andre avsnittene skal behandles normalt basert på sine scorer.\n" +
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
