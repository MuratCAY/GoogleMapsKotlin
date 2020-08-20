package com.example.googlemapskotlin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    // Konumun ismini yazmıyor. Bir hata var

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(dinleyici    )


        //Latitude -> Enlem
        //Longitude ->Boylam

        // 41.0922305,28.8793609

        /*
        val evim = LatLng(41.0922305, 28.8793609)
        mMap.addMarker(MarkerOptions().position(evim).title("SULTANGAZİ"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(evim,20f))

         */

        // casting -> as

        locationManager = getSystemService(Context.LOCATION_SERVICE) as  LocationManager
        locationListener = object : LocationListener{
            override fun onLocationChanged(p0: Location) {
               // Lokasyon, konum değişince yapilacak işlemler
                //println(p0.longitude)
                //println(p0.latitude)
                mMap.clear()
                val guncelKonum = LatLng(p0.latitude, p0.longitude)
                mMap.addMarker(MarkerOptions().position(guncelKonum).title("Güncel Konumunuz"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncelKonum,15f))

                val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
                try {
                    val adresListesi = geocoder.getFromLocation(p0.latitude, p0.longitude,1)
                    if (adresListesi.size > 0){
                        println(adresListesi.get(0).toString())
                    }

                }catch (e: Exception){
                    e.printStackTrace()
                }

            }
        }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //İzin verilmiş
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
                }else{
                    //izin verilmemiş
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
                    val sonBilinenKonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (sonBilinenKonum != null){
                        val sonBilinenLatLng = LatLng(sonBilinenKonum.longitude,sonBilinenKonum.latitude)
                        mMap.addMarker(MarkerOptions().position(sonBilinenLatLng).title("Son Bilinen Konumunuz"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonBilinenLatLng,15f))
                    }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1 ){
            if (grantResults.size > 0){
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
                }
            }
        }



        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val dinleyici= object : GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng?) {
            mMap.clear()

            val geocoder= Geocoder(this@MapsActivity,Locale.getDefault())

            var adres = ""
            if (p0 != null){
                try {
                    val adresListesi = geocoder.getFromLocation(p0.latitude,p0.longitude,1)
                    if (adresListesi.size > 0){
                        if (adresListesi.get(0).thoroughfare != null){
                            adres += adresListesi.get(0).thoroughfare
                        }
                        if (adresListesi.get(0).subThoroughfare != null){
                            adres += adresListesi.get(0).subThoroughfare
                        }
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
                mMap.addMarker(MarkerOptions().position(p0).title(adres))
            }
        }
        }



}