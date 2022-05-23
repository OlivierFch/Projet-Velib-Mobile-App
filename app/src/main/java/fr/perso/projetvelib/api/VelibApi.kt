package fr.perso.projetvelib.api

import fr.perso.projetvelib.model.Station
import fr.perso.projetvelib.model.StationDetails
import retrofit2.http.GET

interface VelibApi {

    @GET("station_information.json")
    suspend fun getStations() : GetStationsResults

    @GET("station_status.json")
    suspend fun getStationDetails() : GetStationsDetailsResults

}

data class GetStationsResults(val data: StationResult)
data class StationResult(val stations: List<Station>)


data class GetStationsDetailsResults(val data: StationDetailsResults)
data class StationDetailsResults(val stations: List<StationDetails>)