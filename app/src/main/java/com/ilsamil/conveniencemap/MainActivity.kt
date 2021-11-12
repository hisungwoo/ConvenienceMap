package com.ilsamil.conveniencemap

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ilsamil.conveniencemap.Fragments.BookmarkFragment
import com.ilsamil.conveniencemap.Fragments.CategoryFragment
import com.ilsamil.conveniencemap.Fragments.InfoFragment
import com.ilsamil.conveniencemap.Fragments.MapFragment
import com.ilsamil.conveniencemap.databinding.ActivityMainBinding
import com.ilsamil.conveniencemap.model.FacInfoList
import com.ilsamil.conveniencemap.repository.RetrofitService
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class MainActivity : AppCompatActivity() {
    private var backButtonTime = 0L
    private var fragmentCount = 0

    private lateinit var binding: ActivityMainBinding
    private val mapFragment: MapFragment by lazy { MapFragment.newInstance() }
    private val categoryFragment: CategoryFragment by lazy { CategoryFragment.newInstance() }
    private val bookmarkFragment: BookmarkFragment by lazy { BookmarkFragment.newInstance() }
    private val infoFragment: InfoFragment by lazy { InfoFragment.newInstance() }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
//            Toast.makeText(this@MainActivity, "위치권한 승인", Toast.LENGTH_SHORT ).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().add(R.id.fragment_view, mapFragment, "map").commit()
        replaceFragment(binding)

        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)


    }

    private fun replaceFragment(binding: ActivityMainBinding) {
        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.menu_category -> {
                    supportFragmentManager.popBackStackImmediate("category", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_view, categoryFragment, "category").addToBackStack("category").commit()
                }
                R.id.menu_bookmark -> {
                    supportFragmentManager.popBackStackImmediate("bookmark", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_view, bookmarkFragment, "bookmark").addToBackStack("bookmark").commit()
                }
                R.id.menu_map -> {
                    supportFragmentManager.popBackStackImmediate("map", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_view, mapFragment, "map").addToBackStack("map").commit()
                }
                R.id.menu_info -> {
                    supportFragmentManager.popBackStackImmediate("info", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_view, infoFragment, "info").addToBackStack("info").commit()
                }
            }
            true
        }
    }


    private fun updateBottomMenu() {
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag("category")
        val tag2: Fragment? = supportFragmentManager.findFragmentByTag("bookmark")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("map")
        val tag4: Fragment? = supportFragmentManager.findFragmentByTag("info")

        binding.bottomNav.apply {
            if(tag1 != null && tag1.isVisible) {
                this.menu.findItem(R.id.menu_category).isChecked = true
            }
            if(tag2 != null && tag2.isVisible) {
                this.menu.findItem(R.id.menu_bookmark).isChecked = true
            }
            if(tag3 != null && tag3.isVisible) {
                this.menu.findItem(R.id.menu_map).isChecked = true
            }
            if(tag4 != null && tag4.isVisible) {
                this.menu.findItem(R.id.menu_info).isChecked = true
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        updateBottomMenu()

    }





//    private fun getAppKeyHash() {
//        try {
//            val info =
//                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
//            for (signature in info.signatures) {
//                var md: MessageDigest
//                md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                val something = String(Base64.encode(md.digest(), 0))
//                Log.e("Hash key", "#################################################" + something + "#################################################")
//            }
//        } catch (e: Exception) {
//
//            Log.e("name not found", e.toString())
//        }
//    }



}