package fr.perso.projetvelib

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.android.clustering.ClusterManager
import fr.perso.projetvelib.api.VelibApi
import fr.perso.projetvelib.databinding.ActivityMainBinding
import fr.perso.projetvelib.model.Station
import fr.perso.projetvelib.model.StationsAdapter
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapFragment: SupportMapFragment
    private var isLocationPermissionOk = false
    private lateinit var currentLocation: Location
    private var currentMarker: Marker? = null

    private val stations: MutableList<Station> = mutableListOf()

    private lateinit var binding: ActivityMainBinding
    lateinit var recyclerViewStations: RecyclerView
    lateinit var stationsAdapter: StationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        recyclerViewStations = findViewById(R.id.stationList)

        stationsAdapter = StationsAdapter(stations)
        recyclerViewStations.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = stationsAdapter
        }

        binding.stationList.isVisible = false


        val searchIcon = binding.searchBar.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(Color.WHITE)

        val textView = binding.searchBar.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        textView.setTextColor(Color.WHITE)
        textView.hint = "Chercher une station..."

        val cancelIcon = binding.searchBar.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        cancelIcon.setOnClickListener {
            textView.editableText.clear()
            binding.stationList.isVisible = false
        }
        cancelIcon.setColorFilter(Color.WHITE)


        binding.searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.stationList.isVisible = textView.text.isNotEmpty()
                stationsAdapter.filter.filter(newText)

                return false
            }
        })


        // Initialisation du bottomsheet qui contient les détails des stations
        val bottomFragment = BottomFragment()
        bottomFragment.show(supportFragmentManager, TAG)


        mapFragment = supportFragmentManager.findFragmentById(R.id.map_carte) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }


    override fun onMapReady(it: GoogleMap) {

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
            LatLng(
                48.8618454,2.3521999
            ), 10f
        )

        it.moveCamera(cameraUpdate)

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
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
            startActivity(Intent(this, FavoriteStationsActivity::class.java))
        }
        // Bouton qui permet de changer le type de carte en fonction des besoins
        val mapTypeButton = findViewById<FloatingActionButton>(R.id.map_type_button)
        mapTypeButton.imageTintList = ColorStateList.valueOf(Color.rgb(255, 255, 255))
        mapTypeButton.setOnClickListener { selectMapMenu() }
        // Bouton de refresh pour avoir les dernières infos des stations
        val synchroApiButton = findViewById<FloatingActionButton>(R.id.map_synchro_api)
        synchroApiButton.imageTintList = ColorStateList.valueOf(Color.rgb(255, 255, 255))
        synchroApiButton.setOnClickListener { synchroApi() }
        // Bouton qui permet de se géolocaliser
        val geolocationButton = findViewById<FloatingActionButton>(R.id.geolocation_button)
        geolocationButton.imageTintList = ColorStateList.valueOf(Color.rgb(255, 255, 255))
        geolocationButton.setOnClickListener { getCurrentLocation() }


        // Sélection d'une station active un bottomsheet avec le détails des stations
        /*it.setOnMarkerClickListener {

            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)

            val btnFavorite = view.findViewById<Button>(R.id.idBtnFavorite)
            val btnClose = view.findViewById<Button>(R.id.idBtnDismiss)

            //btnFavorite.setOnClickListener { favoriteStationsList.add(it) }
            btnClose.setOnClickListener { dialog.dismiss() }
            dialog.setContentView(view)
            dialog.isShowing

        }*/

    }


    private fun selectMapMenu() {
        val popupMenu = PopupMenu(applicationContext, findViewById(R.id.map_type_button))
        popupMenu.apply {
            menuInflater.inflate(R.menu.map_type_menu, popupMenu.menu)
            setOnMenuItemClickListener { item ->

                mapFragment = supportFragmentManager.findFragmentById(R.id.map_carte) as SupportMapFragment

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


    private fun synchroApi() {

        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://94.247.183.221:8078/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()

        val service = retrofit.create(VelibApi::class.java)

        runBlocking {
            val resultStation = service.getStations()
            Log.d(TAG, "synchroApi: ${resultStation}")

            resultStation.map {
                stations.remove(it)
                stations.add(it)
            }

        }
    }


    private fun setUpClusterManager(it: GoogleMap) {

        val clusterManager = ClusterManager<Station>(this, it)
        it.setOnCameraIdleListener(clusterManager)
        it.setOnMarkerClickListener(clusterManager)

        // Fetch stations' data from API
        synchroApi()

        clusterManager.addItems(stations)
        clusterManager.cluster()



    }

}