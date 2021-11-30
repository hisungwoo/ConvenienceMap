package com.ilsamil.conveniencemap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ilsamil.conveniencemap.Fragments.*
import com.ilsamil.conveniencemap.adapters.EvalinfoAdapter
import com.ilsamil.conveniencemap.databinding.ActivityMainBinding
import com.ilsamil.conveniencemap.model.EvalInfoList
import com.ilsamil.conveniencemap.model.FacInfoList
import com.ilsamil.conveniencemap.model.ServList
import com.ilsamil.conveniencemap.repository.RetrofitService
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val mainViewModel : MainViewModel by viewModels()

    private val mapFragment: MapFragment by lazy { MapFragment.newInstance() }
    private val categoryFragment: CategoryFragment by lazy { CategoryFragment.newInstance() }
    private val bookmarkFragment: BookmarkFragment by lazy { BookmarkFragment.newInstance() }
    private val infoFragment: InfoFragment by lazy { InfoFragment.newInstance() }
    private val searchFragment: SearchFragment by lazy { SearchFragment.newInstance() }

//    private lateinit var searchFragment: SearchFragment
    lateinit var fadeInAnim : Animation
    lateinit var fadeOutAnim : Animation


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

        fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_up)
        fadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_down)

        mainViewModel.bottomNavLiveData.observe(this, Observer {
            if(it) {
                binding.bottomNav.startAnimation(fadeInAnim)
                binding.bottomNav.visibility = View.VISIBLE
            } else {
//                binding.bottomNav.startAnimation(fadeOutAnim)
                binding.bottomNav.visibility = View.GONE
            }
        })

        supportFragmentManager.beginTransaction().add(R.id.fragment_view, mapFragment, "map").commit()

//        supportFragmentManager.beginTransaction().add(R.id.fragment_view, categoryFragment, "category").commit()
//        supportFragmentManager.beginTransaction().add(R.id.fragment_view, bookmarkFragment, "bookmark").commit()
//        supportFragmentManager.beginTransaction().add(R.id.fragment_view, infoFragment, "info").commit()
//        supportFragmentManager.beginTransaction().add(R.id.fragment_view, mapFragment, "map").commit()
//        supportFragmentManager.beginTransaction().add(R.id.fragment_view, searchFragment, "map").commit()
//
//        supportFragmentManager.beginTransaction().hide(categoryFragment).commit()
//        supportFragmentManager.beginTransaction().hide(bookmarkFragment).commit()
//        supportFragmentManager.beginTransaction().hide(infoFragment).commit()
//        supportFragmentManager.beginTransaction().hide(searchFragment).commit()
//        supportFragmentManager.beginTransaction().show(mapFragment).commit()


        replaceFragment(binding)
        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        mainViewModel.movemove.observe(this, Observer {
            //태그 이용해봐
            // https://lucky516.tistory.com/63
            if(it == 1) {
                supportFragmentManager.beginTransaction().remove(searchFragment).addToBackStack(null).commit()
            } else {
                supportFragmentManager.beginTransaction().add(R.id.fragment_view, searchFragment).addToBackStack(null).commit()
            }
        })
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


    // 뒤로가기 시 bottomnav 클릭 활성화
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


//            if (this.visibility == View.VISIBLE) {
//                Log.d("ttest" , "나타남")
//            } else if (this.visibility == View.GONE) {
//                Log.d("ttest", "gone 상태")
//            }


        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        updateBottomMenu()
    }


}