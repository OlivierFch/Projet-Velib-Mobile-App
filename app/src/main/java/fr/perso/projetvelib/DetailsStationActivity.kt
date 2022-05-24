package fr.perso.projetvelib

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.perso.projetvelib.databinding.ActivityDetailsBinding
import fr.perso.projetvelib.model.Station

class DetailsStationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //binding.detailStationName.text = intent.extras!!.getString("")

    }

}