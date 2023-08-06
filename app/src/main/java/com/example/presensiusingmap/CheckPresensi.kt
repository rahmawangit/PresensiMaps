package com.example.presensiusingmap

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.Html
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.presensiusingmap.data.Presensi
import com.example.presensiusingmap.data.PresensiViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale

class CheckPresensi : AppCompatActivity(), OnMapReadyCallback {
//    private lateinit var binding: ActivityCheckPresensiBinding

    lateinit var mMap  : GoogleMap //VAR MAP
    lateinit var mLocationRequest: LocationRequest //VAR MINTA LOKASI
    private lateinit var mPresensiViewModel : PresensiViewModel
    private lateinit var myPosition : LatLng

    internal var mFusedLocationClient: FusedLocationProviderClient? = null  // IDK

    private var status =""
    var mLastLocation: Location? = null // VAR LOKASI TERAKHIR
    var mCurrentLocationMarker: Marker? = null  // MARKER LOKASI SAAT INI
    var cevestLocationMarker : Marker? = null   // MARKER LOKASI KANTOR
    var garisJarak : Polyline? = null           // GARIS
    var checkIn : String = ""



    internal var mLocationCallback: LocationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult){
//            binding = ActivityCheckPresensiBinding.inflate(layoutInflater)
            val locationList = locationResult?.locations
            Log.d("CEKUP","INI INTERNAL")
            if(locationList!!.isNotEmpty()){
                val location = locationList.last()
                Log.i("cekcek",location.toString())
                mLastLocation = location
                if(mCurrentLocationMarker != null){
                    mCurrentLocationMarker?.remove()
                }

                if(cevestLocationMarker !=null){
                    cevestLocationMarker?.remove()
                }

                if(garisJarak != null){
                    garisJarak?.remove()
                }
                // ============================= OUR LOCATION START
                myPosition = LatLng(location.latitude,location.longitude)
                val markerOptions = MarkerOptions()
                markerOptions.position(myPosition)

                markerOptions.title("Posisi sekarang "+location.latitude+","+location.longitude.toString())
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                mCurrentLocationMarker = mMap.addMarker(markerOptions)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition,15.0F))
                // ============================= OUR LOCATION END
                // ============================= KANTOR LOCATION START
                val cevestLocation = LatLng(-6.2347677,106.987864)

                cevestLocationMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(cevestLocation)
                        .title("BBPVP BEKASI")
                        .icon(
                            BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        )
                )

                val circleOptions = CircleOptions()
                circleOptions.center(cevestLocation)

                circleOptions.radius(1000.0)
                circleOptions.strokeColor(Color.RED)
                mMap.addCircle(circleOptions)
                // =========================== KANTOR LOCATION END
                // =========================== GARIS JARAK START
                val distance = FloatArray(2)
                Location.distanceBetween(
                    cevestLocation.latitude,cevestLocation.longitude,location.latitude,location.longitude,distance
                )

                if (distance[0] > circleOptions.radius){
                    status = "WFH"
                }else{
                    status = "WFO"
                }

                garisJarak = mMap.addPolyline(
                    PolylineOptions().add(cevestLocation,
                        LatLng(location.latitude, location.longitude)
                    )
                        .width(5.0F)
                        .color(Color.YELLOW)
                )
                // ========================== GARIS JARAK END
                val geocoder: Geocoder
                val addresses: List<Address>?
                val latitude = location.latitude
                val longitude = location.longitude
                geocoder = Geocoder(this@CheckPresensi, Locale.getDefault())

                addresses = geocoder.getFromLocation(latitude,longitude,1)

                val address =
                    addresses!![0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                val citya = addresses[0].
//                val city = addresses[0].locality
//                val state = addresses[0].adminArea
//                val country = addresses[0].countryName
//                val postalCode = addresses[0].postalCode
//                val knownName = addresses[0].featureName // Only if available else return NULL


                checkIn = "<b>Posisi anda Saat ini</b> : $address " +
                        "</br> <b>Status</b> : $status"
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_presensi)
        Log.d("CEKUP","INI ONCREATE")
        var buttonCheck = findViewById<Button>(R.id.butt_check)
        var tvLokasi = findViewById<TextView>(R.id.tv_lokasi)
        var tvStatus = findViewById<TextView>(R.id.tv_status)
        mPresensiViewModel = ViewModelProvider(this).get(PresensiViewModel::class.java)

//        binding = ActivityCheckPresensiBinding.inflate(layoutInflater)
//        setContentView(binding.root)

//        if(status == "WFH"){
////            binding.buttCheck.text = R.string.status_wfh.toString()
////            binding.buttCheck.text = "WFH"
//            buttonCheck.text = "Absen (WFH)"
//
//        }else{
////            binding.buttCheck.text = "WFO"
////            buttonCheck.text = R.string.status_wfo.toString()
//            buttonCheck.text = "Absen (WFO)"
//
//        }
        buttonCheck.setOnClickListener(){
            insertDataToDatabase(status)
            val pesan = "Status Bekerja anda saat ini : $status"
            tvStatus.text = pesan
            tvLokasi.text = Html.fromHtml(checkIn)
            buttonCheck.text = "Sudah Absen"
            buttonCheck.setEnabled(false)

        }

//        binding.buttCheck.setOnClickListener(){
//            insertDataToDatabase(status)
//            val pesan = "Status Bekerja anda saat ini : $status"
//            binding.tvStatus.text = pesan
//            binding.tvLokasi.text = Html.fromHtml(checkIn)
//            binding.buttCheck.text = R.string.sudah_absen.toString()
//            binding.buttCheck.setEnabled(false)
//        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap : GoogleMap){
        mMap = googleMap
        mMap.mapType= GoogleMap.MAP_TYPE_HYBRID
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 50000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return Toast.makeText(this,"Izinkan Akses Location", Toast.LENGTH_LONG).show()
        }
        mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
        googleMap.isMyLocationEnabled = true
    }

    private fun insertDataToDatabase(status : String){
//        val username = listOf<String>("Areef","Bagas","Jae","Ray","Muti","Jaka","Fatma","Samsul","Aji","Panji","Cikal","Kei",
//            "Azis","Dipo","Debo","Rizal")
        val username = arrayOf("Areef","Bagas","Jae","Ray","Muti","Jaka","Fatma","Samsul","Aji","Panji","Cikal","Kei",
            "Azis","Dipo","Debo","Rizal")
        val calendar = Calendar.getInstance()
        val current = LocalDateTime.of(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND))
        val tgl = "${current.dayOfMonth} ${current.month} ${current.year}"
        val jam = "${current.hour}:${current.minute}"
        val noUser = (0..15).random()
        val presensi = Presensi(0,username[noUser],tgl,jam,status)

        Log.d("CEKUP","GACHA $noUser ${username[noUser]} , $tgl, $jam , $status")
        mPresensiViewModel.addPresensi(presensi)
    }
}