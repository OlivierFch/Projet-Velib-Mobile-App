package fr.perso.projetvelib.dao

import androidx.room.*
import fr.perso.projetvelib.model.Station

@Dao
interface StationDao {
    @Query("SELECT * FROM station")
    fun getAll(): List<Station>

    @Query("SELECT * FROM station WHERE liked=1 ")
    fun getAllLiked(): List<Station>

    @Insert
    fun insertAll(stations: List<Station>)

    @Delete
    fun deleteAll(stations: List<Station>)

    @Query("UPDATE station SET liked = :liked WHERE station_id=:id")
    fun updateLiked(liked: Boolean,id:Long)

    //update function when we sync the database with the API but we keep existing data
    @Query("UPDATE station SET bikes_available = :bikes_available, ebikes_available = :ebikes_available, last_reported = :last_reported, num_docks_available = :num_docks_available  WHERE station_id=:id")
    fun resyncUpdate(bikes_available: Int,ebikes_available: Int,last_reported: Int,num_docks_available: Int, id:Long)

    /*@Update
    fun hardResyncUpdate(station: Station)

    //implementées mais non utiles :
    @Delete
    fun delete(station: Station)*/
}