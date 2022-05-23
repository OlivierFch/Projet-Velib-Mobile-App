package fr.perso.projetvelib.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class Station(
    val station_id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int,
    val stationCode: String
) : ClusterItem {

    override fun getPosition(): LatLng {
        return LatLng(lat, lon)
    }

    override fun getTitle(): String {
        return name
    }

    override fun getSnippet(): String {
        return ""
    }

}