package fr.perso.projetvelib

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.SupportMapFragment
import fr.perso.projetvelib.model.Station
import fr.perso.projetvelib.model.StationsAdapter

class FavoriteStationsActivity : AppCompatActivity() {

    private val stations: MutableList<Station> = mutableListOf()
    lateinit var recyclerViewStations: RecyclerView
    lateinit var stationsAdapter: StationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorite_stations_layout)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#07BEB8")));
        supportActionBar?.title = "Favoris"

        // Instancier la liste des stations en favoris
        recyclerViewStations = findViewById(R.id.favoriteStationsList)
        stationsAdapter = StationsAdapter(stations)
        recyclerViewStations.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = stationsAdapter
        }

        stationsAdapter.setOnItemClickListener(object : StationsAdapter.onItemClickListener {
            override fun onItemClick(station: Station) {

                // Affichage de la bottomSheet avec les détails de la station
                val bottomFragment = BottomFragment(station)
                bottomFragment.show(supportFragmentManager, TAG)

            }

        })
        

    }

}