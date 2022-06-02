package fr.perso.projetvelib

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import fr.perso.projetvelib.model.Station

class BottomFragment(val station: Station) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnFavorite = view.findViewById<Button>(R.id.idBtnFavorite)
        //btnFavorite.setOnClickListener { favoriteStationsList.add(it) }

        val nameStation = view.findViewById<TextView>(R.id.idStationName)
        nameStation.text = station.name

        val placeAvailable = view.findViewById<TextView>(R.id.idPlacesRestantes)
        placeAvailable.text = "Places parking restantes: ${station.num_docks_available}"

        val mechanicalAvailable = view.findViewById<TextView>(R.id.idNumberBikeAvailable)
        mechanicalAvailable.text = "Vélib mécaniques : ${station.bikes_available}"

        val ebikeAvailable = view.findViewById<TextView>(R.id.idNumberEbikeAvailable)
        ebikeAvailable.text = "Vélib électriques: ${station.ebikes_available}"
    }

}