package fr.perso.projetvelib

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.perso.projetvelib.model.Station
import fr.perso.projetvelib.model.StationsAdapter

class FavoriteStationsActivity : AppCompatActivity() {

    lateinit var recyclerViewStations: RecyclerView
    lateinit var stationsAdapter: StationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorite_stations_layout)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#07BEB8")));
        supportActionBar?.title = "Favoris"

        recyclerViewStations = findViewById(R.id.favoriteStationsList)
        stationsAdapter = StationsAdapter(favoriteList)

        recyclerViewStations.apply {
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            adapter = stationsAdapter
        }

        stationsAdapter.setOnItemClickListener(object : StationsAdapter.onItemClickListener {
            override fun onItemClick(station: Station) {

                // Affichage de la bottomSheet avec les détails de la station
                val bottomFragment = BottomDeleteFragment(station)
                bottomFragment.show(supportFragmentManager, TAG)
            }
        })

    }

}