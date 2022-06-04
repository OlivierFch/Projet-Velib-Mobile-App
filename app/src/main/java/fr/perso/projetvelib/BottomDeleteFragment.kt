package fr.perso.projetvelib

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import fr.perso.projetvelib.model.Station

class BottomDeleteFragment(val station: Station) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_delete_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnDeleteFavorite = view.findViewById<Button>(R.id.idBtnDeleteFavorite)
        btnDeleteFavorite.setOnClickListener {

            if (favoriteList.contains(station)) {
                favoriteList.remove(station)
                Toast.makeText(requireContext(), "${station.name} est supprimé des favoris !", Toast.LENGTH_SHORT).show()
            }else {
                btnDeleteFavorite.isEnabled = false
                Toast.makeText(requireContext(), "${station.name} est déjà supprimé des favoris !", Toast.LENGTH_SHORT).show()
            }

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