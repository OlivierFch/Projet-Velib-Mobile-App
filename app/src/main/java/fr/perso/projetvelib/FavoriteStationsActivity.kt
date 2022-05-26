package fr.perso.projetvelib

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment

class FavoriteStationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorite_stations_layout)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#07BEB8")));
        supportActionBar?.title = "Favoris"

    }

}