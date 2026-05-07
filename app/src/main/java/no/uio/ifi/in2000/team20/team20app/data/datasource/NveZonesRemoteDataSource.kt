package no.uio.ifi.in2000.team20.team20app.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import no.uio.ifi.in2000.team20.team20app.data.api.NveRoutes
import no.uio.ifi.in2000.team20.team20app.data.dto.ArcGisResponseDto
import no.uio.ifi.in2000.team20.team20app.di.NveZonesClient
import javax.inject.Inject

interface NveZonesRemoteDataSourceService {
    suspend fun getLandslideZoneData(lon:Double, lat:Double): ArcGisResponseDto
    suspend fun getFloodZoneData(lon:Double, lat:Double): ArcGisResponseDto
}

class NveZonesRemoteDataSource @Inject constructor(
    @NveZonesClient private val client: HttpClient
) : NveZonesRemoteDataSourceService {
    override suspend fun getLandslideZoneData(lon:Double, lat:Double): ArcGisResponseDto {
        val response: ArcGisResponseDto = client.get(
            NveRoutes.SKRED
        ) {
            parameter("geometry", """{"x":$lon,"y":$lat,"spatialReference":{"wkid":4326}}""")
            parameter("geometryType", "esriGeometryPoint")
            parameter("inSR", 4326)
            parameter("spatialRel", "esriSpatialRelIntersects")
            parameter("outFields", "*")
            parameter("returnGeometry", false)
            parameter("f", "json")
        }.body()

        return response
    }

    override suspend fun getFloodZoneData(lon:Double, lat:Double): ArcGisResponseDto {
        val response: ArcGisResponseDto = client.get(
            NveRoutes.FLOM
        ) {
            parameter("geometry", """{"x":$lon,"y":$lat,"spatialReference":{"wkid":4326}}""")
            parameter("geometryType", "esriGeometryPoint")
            parameter("inSR", 4326)
            parameter("spatialRel", "esriSpatialRelIntersects")
            parameter("outFields", "*")
            parameter("returnGeometry", false)
            parameter("f", "json")
        }.body()

        return response
    }
}