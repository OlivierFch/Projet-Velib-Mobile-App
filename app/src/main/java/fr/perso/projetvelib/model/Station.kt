package fr.perso.projetvelib.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "station")
data class Station(
    @ColumnInfo(name = "bikes_available") val bikes_available: Int,
    @ColumnInfo(name = "capacity") val capacity: Int,
    @ColumnInfo(name = "ebikes_available") val ebikes_available: Int,
    @ColumnInfo(name = "last_reported") val last_reported: Int,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lon") val lon: Double,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "num_docks_available") val num_docks_available: Int,
    @ColumnInfo(name = "stationCode") val stationCode: String,
    @PrimaryKey val station_id: Long,
    @ColumnInfo(name = "liked") var liked : Boolean = false,
) : ClusterItem {

    override fun getPosition(): LatLng {
        return LatLng(lat, lon)
    }

    override fun getTitle(): String {
        return name
    }

    override fun getSnippet(): String? {
        return null
    }

}