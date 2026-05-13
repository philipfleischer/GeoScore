package no.uio.ifi.in2000.team20.team20app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import no.uio.ifi.in2000.team20.team20app.data.dto.Address
import no.uio.ifi.in2000.team20.team20app.data.dto.AddressMetadata
import no.uio.ifi.in2000.team20.team20app.util.Constants.HTTP_OK
import java.math.BigDecimal
import java.math.RoundingMode

data class SearchResult(
    val locations: List<Location> = emptyList(),
    // For setting the potential error message in SearchScreen
    val status: Int = HTTP_OK
)

data class AddressResponseWrapper(
    val metadata: AddressMetadata?,
    val addresses: List<Address>,
    val status: Int
)

@Serializable
@Parcelize
data class Location(
    val address: String,
    val name: String = address,
    val municipality: String? = null,
    val county: String? = null,
    val lat: Double,
    val lon: Double,
    val savedAt: Long = 0L
):Parcelable{
    companion object{
        fun roundToStandard(coordinate: Double): Double{
            return BigDecimal(coordinate).setScale(5, RoundingMode.HALF_UP).toDouble()
        }
    }
}