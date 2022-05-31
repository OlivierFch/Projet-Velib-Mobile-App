package fr.perso.projetvelib.model

data class StationDetails(
    val station_id: Long,
    val numDocksAvailable: Int,
    val num_bikes_available_types: BikeTypes
)

data class BikeTypes(val mechanical: Int, val ebikes: Int)