package fr.perso.projetvelib.controller

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.room.Room
import fr.perso.projetvelib.TAG
import fr.perso.projetvelib.api.AppDatabase
import fr.perso.projetvelib.api.VelibApi
import fr.perso.projetvelib.dao.StationDao
import fr.perso.projetvelib.model.Station
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class DataController(appliContext: Context) {

    private var stationDao: StationDao
    private var db: AppDatabase
    private var context: Context = appliContext

    init {
        this.db = Room.databaseBuilder(
            this.context,
            AppDatabase::class.java, "velib_db"
        ).allowMainThreadQueries().build()
        this.stationDao = this.db.stationDao()
    }

    // method appelée en début de chargement uniquement
    fun syncDB(resetDB: Boolean = false) {
        if (this.checkInternetConnection()) {
            Log.d("EPF", "Internet Connection Ok !")
            val stations = this.getDataFromAPI()
            if (resetDB) {
                this.stationDao.deleteAll(stations)
                this.stationDao.insertAll(stations)
            } else {
                stations.forEach {
                    this.stationDao.resyncUpdate(
                        it.bikes_available,
                        it.ebikes_available,
                        it.last_reported,
                        it.num_docks_available,
                        it.station_id
                    )
                }
            }
        } else {
            Log.d("EPF", "No internet Connection... cannot sync")
        }
    }

    fun getAllStations(): List<Station> {
        return this.stationDao.getAll()
    }

    fun getAllFavoriteStations(): List<Station> {
        return this.stationDao.getAllLiked()
    }

    fun dislikeStation(station: Station) {
        if (station.liked) {
            this.stationDao.updateLiked(false, station.station_id)
            station.liked = false
        } else {
            Log.d("EPF", "Déjà dislikée")
        }
    }

    fun likeStation(station: Station) {
        if (!station.liked) {
            this.stationDao.updateLiked(true, station.station_id)
            station.liked = true
        } else {
            Log.d("EPF", "Déjà likée")
        }
    }

    private fun getDataFromAPI(): List<Station> {
        var stations: List<Station>
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()

                // API créée par un script Python qui récupère les données utiles pour le projet depuis les APIs Vélib
            .baseUrl("http://94.247.183.221:8078/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()

        val service = retrofit.create(VelibApi::class.java)

        runBlocking {
            val resultStation = service.getStations()
            stations = resultStation

        }
        return stations
    }


    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            this.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
}