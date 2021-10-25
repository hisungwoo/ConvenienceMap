package com.ilsamil.conveniencemap

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.ilsamil.conveniencemap.databinding.ActivityMainBinding
import net.daum.mf.map.api.MapView
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val mapView = MapView(this)
        binding.clKakaoMapView.addView(mapView)



        getAppKeyHash()
    }



    private fun getAppKeyHash() {
        try {
            val info =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                var md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something = String(Base64.encode(md.digest(), 0))
                Log.e("Hash key", "#################################################" + something + "#################################################")
            }
        } catch (e: Exception) {

            Log.e("name not found", e.toString())
        }
    }



}