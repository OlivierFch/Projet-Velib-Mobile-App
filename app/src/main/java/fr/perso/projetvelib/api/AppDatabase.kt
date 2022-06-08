package fr.perso.projetvelib.api

import fr.perso.projetvelib.dao.StationDao
import fr.perso.projetvelib.model.Station
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Station::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stationDao(): StationDao
}