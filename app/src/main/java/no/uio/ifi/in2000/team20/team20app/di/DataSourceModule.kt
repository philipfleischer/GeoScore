package no.uio.ifi.in2000.team20.team20app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.team20.team20app.data.datasource.AddressApiService
import no.uio.ifi.in2000.team20.team20app.data.datasource.AddressRemoteDataSource
import no.uio.ifi.in2000.team20.team20app.data.datasource.FrostDataSource
import no.uio.ifi.in2000.team20.team20app.data.datasource.FrostDataSourceService
import no.uio.ifi.in2000.team20.team20app.data.datasource.NveZonesRemoteDataSource
import no.uio.ifi.in2000.team20.team20app.data.datasource.NveZonesRemoteDataSourceService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindFrostDataSource(impl: FrostDataSource): FrostDataSourceService

    @Binds
    @Singleton
    abstract fun bindNveZonesDataSource(impl: NveZonesRemoteDataSource): NveZonesRemoteDataSourceService

    @Binds
    @Singleton
    abstract fun bindAddressDataSource(impl: AddressRemoteDataSource): AddressApiService
}
