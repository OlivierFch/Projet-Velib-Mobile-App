package fr.perso.projetvelib

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import fr.perso.projetvelib.controller.DataController
import fr.perso.projetvelib.model.Station

var favoriteList: List<Station> = listOf()
//var favoriteList: List<Station> = DataController(appliContext = MainActivity().applicationContext).getAllFavoriteStations()

class BottomAddFragment(val station: Station) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_add_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnFavorite = view.findViewById<Button>(R.id.idBtnFavorite)
        btnFavorite.setOnClickListener {

            if (favoriteList.contains(station)) {
                btnFavorite.isEnabled = false
                Toast.makeText(requireContext(), "${station.name} est déjà dans les favoris !", Toast.LENGTH_SHORT).show()
            }else {

                DataController(this.requireContext()).likeStation(station)
                //favoriteList.add(station)
                //Toast.makeText(requireContext(), "${station.name} est ajoutée aux favoris !", Toast.LENGTH_SHORT).show()
            }

            // Refresh favoriteList
            favoriteList = DataController(this.requireContext()).getAllFavoriteStations()

        }

        val nameStation = view.findViewById<TextView>(R.id.idStationName)
        nameStation.text = station.name

        val placeAvailable = view.findViewById<TextView>(R.id.idPlacesRestantes)
        placeAvailable.text = "Places parking restantes: ${station.num_docks_available}"

        val mechanicalAvailable = view.findViewById<TextView>(R.id.idNumberBikeAvailable)
        mechanicalAvailable.text = "Vélib mécaniques dispo : ${station.bikes_available}"

        val ebikeAvailable = view.findViewById<TextView>(R.id.idNumberEbikeAvailable)
        ebikeAvailable.text = "Vélib électriques dispo : ${station.ebikes_available}"
    }


}