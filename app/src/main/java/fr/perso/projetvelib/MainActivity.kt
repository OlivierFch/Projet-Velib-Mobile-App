package fr.perso.projetvelib

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.android.clustering.ClusterManager
import fr.perso.projetvelib.controller.DataController
import fr.perso.projetvelib.databinding.ActivityMainBinding
import fr.perso.projetvelib.model.Station
import fr.perso.projetvelib.model.StationsAdapter

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapFragment: SupportMapFragment
    private var isLocationPermissionOk = false
    private lateinit var currentLocation: Location
    private var currentMarker: Marker? = null

    private lateinit var stations: List<Station>

    private lateinit var binding: ActivityMainBinding
    lateinit var recyclerViewStations: RecyclerView
    lateinit var stationsAdapter: StationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Appel des données dans la DB
        val dc = DataController(this.applicationContext)
        dc.syncDB()
        stations = dc.getAllStations()

        // Ajouter données en dur
        stations[2].station_id
        stations[150].station_id
        stations[952].station_id
        stations[42].station_id
        DataController(this.applicationContext).likeStation(stations[2])
        Log.d(TAG, "station 2")

        DataController(this.applicationContext).likeStation(stations[150])
        DataController(this.applicationContext).likeStation(stations[952])
        DataController(this.applicationContext).likeStation(stations[42])
        favoriteList = DataController(this).getAllFavoriteStations()
        Log.d(TAG, "FAVORITELIST: ${favoriteList}")

        recyclerViewStations = findViewById(R.id.stationList)
        stationsAdapter = StationsAdapter(stations)
        recyclerViewStations.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = stationsAdapter
        }

        binding.stationList.isVisible = false


        val searchIcon =
            binding.searchBar.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(Color.WHITE)

        val textView =
            binding.searchBar.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        textView.setTextColor(Color.WHITE)
        textView.hint = "Chercher une station..."

        val cancelIcon =
            binding.searchBar.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        cancelIcon.setOnClickListener {
            textView.editableText.clear()
            binding.stationList.isVisible = false
        }
        cancelIcon.setColorFilter(Color.WHITE)


        // Filtre de recherche
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.stationList.isVisible = textView.text.isNotEmpty()
                stationsAdapter.filter.filter(newText)

                return false
            }
        })


        mapFragment = supportFragmentManager.findFragmentById(R.id.map_carte) as SupportMapFragment
        mapFragment.getMapAsync { mMap ->

            // Action suite à la sélection d'une station dans le recyclerView
            stationsAdapter.setOnItemClickListener(object : StationsAdapter.onItemClickListener {
                override fun onItemClick(station: Station) {

                    // Mouvement de caméra vers la position de la station
                    val cameraStationUpdate = CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            station.lat,
                            station.lon
                        ), 17f
                    )
                    mMap.animateCamera(cameraStationUpdate)

                    // Cacher la liste des stations
                    binding.stationList.isVisible = false

                    // Affichage de la bottomSheet avec les détails de la station
                    val bottomFragment = BottomAddFragment(station)
                    bottomFragment.show(supportFragmentManager, TAG)
                }

            })
        }


        mapFragment.getMapAsync(this)

    }


    override fun onMapReady(it: GoogleMap) {

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
            LatLng(
                48.8618454, 2.3521999
            ), 10f
        )

        it.moveCamera(cameraUpdate)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        it.isMyLocationEnabled = true
        it.uiSettings.isTiltGesturesEnabled = true
        it.uiSettings.isMyLocationButtonEnabled = false


        // Afficher les bornes en individuel
        /*synchroApi()

        stations.forEach{
            station -> val (_, name, latitude, longitude) = station
            val velibCoordinate = LatLng(latitude, longitude)

            it.addMarker(
                MarkerOptions()
                    .position(velibCoordinate)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title(name)
            )
        }*/


        // Afficher les stations sous forme de clusters
        setUpClusterManager(it)

        // Bouton qui permet de changer d'activité et afficher la liste des stations favorites
        val favoriteButton = findViewById<FloatingActionButton>(R.id.favorite_stations_button)
        favoriteButton.imageTintList = ColorStateList.valueOf(Color.rgb(255, 255, 255))
        favoriteButton.setOnClickListener {

            if (favoriteList.isEmpty()) {
                Toast.makeText(applicationContext, "La liste est vide ! Vous devez ajouter une station pour y accéder.", Toast.LENGTH_LONG).show()
                FavoriteStationsActivity().finish()
            }else {
                startActivity(Intent(this, FavoriteStationsActivity::class.java))
            }

        }
        // Bouton qui permet de changer le type de carte en fonction des besoins
        val mapTypeButton = findViewById<FloatingActionButton>(R.id.map_type_button)
        mapTypeButton.imageTintList = ColorStateList.valueOf(Color.rgb(255, 255, 255))
        mapTypeButton.setOnClickListener { selectMapMenu() }

        // Bouton de refresh pour avoir les dernières infos des stations
        val synchroApiButton = findViewById<FloatingActionButton>(R.id.map_synchro_api)
        synchroApiButton.imageTintList = ColorStateList.valueOf(Color.rgb(255, 255, 255))
        synchroApiButton.setOnClickListener { DataController(this.applicationContext).syncDB() }

        // Bouton qui permet de se géolocaliser
        val geolocationButton = findViewById<FloatingActionButton>(R.id.geolocation_button)
        geolocationButton.imageTintList = ColorStateList.valueOf(Color.rgb(255, 255, 255))
        geolocationButton.setOnClickListener { getCurrentLocation() }

    }


    private fun selectMapMenu() {
        val popupMenu = PopupMenu(applicationContext, findViewById(R.id.map_type_button))
        popupMenu.apply {
            menuInflater.inflate(R.menu.map_type_menu, popupMenu.menu)
            setOnMenuItemClickListener { item ->

                mapFragment =
                    supportFragmentManager.findFragmentById(R.id.map_carte) as SupportMapFragment

                mapFragment.getMapAsync {
                    when (item.itemId) {
                        R.id.btnNormal -> it.mapType = GoogleMap.MAP_TYPE_NORMAL
                        R.id.btnSatellite -> it.mapType = GoogleMap.MAP_TYPE_SATELLITE
                        R.id.btnHybrid -> it.mapType = GoogleMap.MAP_TYPE_HYBRID
                    }
                }
                true
            }
            show()
        }
    }


    private fun getCurrentLocation() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionOk = false
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {

            currentLocation = it
            moveCameraToLocation(currentLocation)
        }
    }


    private fun moveCameraToLocation(currentLocation: Location) {

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
            LatLng(
                currentLocation.latitude,
                currentLocation.longitude
            ), 17f
        )

        val markerOption = MarkerOptions()
            .position(LatLng(currentLocation.latitude, currentLocation.longitude))
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            .visible(false)


        mapFragment = supportFragmentManager.findFragmentById(R.id.map_carte) as SupportMapFragment

        mapFragment.getMapAsync {
            currentMarker?.remove()
            currentMarker = it.addMarker(markerOption)
            currentMarker?.tag = 703
            it.animateCamera(cameraUpdate)
        }

    }


    private fun setUpClusterManager(mMap: GoogleMap) {

        val clusterManager = ClusterManager<Station>(this, mMap)
        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)

        clusterManager.addItems(stations)
        clusterManager.cluster()

        // Sélection d'une station active un bottomsheet avec le détails des stations
        clusterManager.setOnClusterItemClickListener {

            // Mouvement de caméra vers la position de la station
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    it.lat,
                    it.lon
                ), 17f
            )
            mMap.animateCamera(cameraUpdate)


            val stationClicked = stations.find { station ->
                it.title == station.name
            }

            if (stationClicked !== null) {
                val bottomFragment = BottomAddFragment(stationClicked)
                bottomFragment.show(supportFragmentManager, TAG)
            } else {
                Log.d(TAG, "Error")
            }

            true
        }

    }

}