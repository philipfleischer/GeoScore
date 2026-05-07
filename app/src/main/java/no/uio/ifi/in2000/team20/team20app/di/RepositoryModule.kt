package no.uio.ifi.in2000.team20.team20app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepositoryService
import no.uio.ifi.in2000.team20.team20app.data.repository.GeoSearchRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.GeoSearchRepositoryService
import no.uio.ifi.in2000.team20.team20app.data.repository.NveZonesRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.NveZonesRepositoryService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFrostRepository(impl: FrostRepository): FrostRepositoryService

    @Binds
    @Singleton
    abstract fun bindGeoSearchRepository(impl: GeoSearchRepository): GeoSearchRepositoryService

    @Binds
    @Singleton
    abstract fun bindNveZonesRepository(impl: NveZonesRepository): NveZonesRepositoryService
}
