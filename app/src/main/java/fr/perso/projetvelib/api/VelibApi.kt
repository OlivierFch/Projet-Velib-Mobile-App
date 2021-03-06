package fr.perso.projetvelib.api

import fr.perso.projetvelib.model.Station
import retrofit2.http.GET

interface VelibApi {

    @GET("get-all-stations")
    suspend fun getStations() : List<Station>

}