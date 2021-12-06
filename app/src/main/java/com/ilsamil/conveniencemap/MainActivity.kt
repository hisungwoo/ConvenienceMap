package com.ilsamil.conveniencemap

import android.Manifest
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ilsamil.conveniencemap.Fragments.*
import com.ilsamil.conveniencemap.adapters.EvalinfoAdapter
import com.ilsamil.conveniencemap.databinding.ActivityMainBinding
import com.ilsamil.conveniencemap.utils.ChangeType
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel : MainViewModel by viewModels()
    private lateinit var mapView: MapView

    private val mapFragment: MapFragment by lazy { MapFragment.newInstance() }
    private val categoryFragment: CategoryFragment by lazy { CategoryFragment.newInstance() }
    private val bookmarkFragment: BookmarkFragment by lazy { BookmarkFragment.newInstance() }
    private val infoFragment: InfoFragment by lazy { InfoFragment.newInstance() }
    private val searchFragment: SearchFragment by lazy { SearchFragment.newInstance() }

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

        mapView = MapView(this)
        binding.clKakaoMapView.addView(mapView)

        fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_up)
        fadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_down)

        mainViewModel.mainStatus.observe(this, Observer {
            when(it) {
                1 -> {
                    Log.d("ttest", "status 1")
                    // 기본 메인 상태
                    binding.bottomNav.visibility = View.VISIBLE
                    binding.searchBtn.visibility = View.VISIBLE
                }
                2 -> {
                    Log.d("ttest", "status 2")
                    // 바템네비, 검색버튼 제거
                    binding.bottomNav.visibility = View.GONE
                    binding.searchBtn.visibility = View.GONE
                    binding.resultLayout.visibility = View.GONE
                    binding.refreshBtn.visibility = View.GONE
                    mapView.removeAllPOIItems()
                }
            }
        })


        replaceFragment(binding)
        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//        supportFragmentManager.beginTransaction().add(R.id.fragment_view, mapFragment, "map").commit()


        binding.searchBtn.setOnClickListener{
            supportFragmentManager.beginTransaction().add(R.id.fragment_view, searchFragment, "search").addToBackStack(null).commit()
        }


        val adapter = EvalinfoAdapter()
        binding.resultRecyclerView.adapter = adapter
        mainViewModel.evalInfoLiveData.observe(this, androidx.lifecycle.Observer {
            adapter.updateItems(it)
        })

        mainViewModel.movePin.observe(this, Observer {
            mainViewModel.mainStatus.value = 3
            mainViewModel.bottomNavLiveData.value = false

            val faclLng = it.faclLng!!
            val faclLat = it.faclLat!!
            val faclNm = it.faclNm
            val faclTyCd = it.faclTyCd
            val lcMnad = it.lcMnad
            val wfcltId = it.wfcltId!!


            Log.d("ttest" , "faclLng " + faclLng)
            Log.d("ttest" , "faclLat " + faclLat)
            Log.d("ttest" , "faclNm " + faclNm)
            Log.d("ttest" , "wfcltId " + wfcltId)
            Log.d("ttest" , "faclTyCd " + faclTyCd)

            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(faclLat, faclLng), 0, true)

            val customMarker = MapPOIItem()
            customMarker.itemName = "테스트 마커"
            customMarker.tag = 1
            customMarker.mapPoint = MapPoint.mapPointWithGeoCoord(faclLat, faclLng)
            customMarker.markerType = MapPOIItem.MarkerType.CustomImage
            customMarker.customImageResourceId = R.drawable.ic_location_pin_50_1206
            customMarker.isCustomImageAutoscale = false
            customMarker.setCustomImageAnchor(0.5f, 1.0f)

            mapView.addPOIItem(customMarker)

            binding.resultRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.HORIZONTAL, false)
            binding.resultNmTv.text = faclNm
            binding.resultTypeTv.text = faclTyCd?.let { it1 -> ChangeType().changeType(it1) }
            binding.resultLocationTv.text = lcMnad

            binding.resultLayout.startAnimation(fadeInAnim)
            binding.resultLayout.visibility = View.VISIBLE

            mainViewModel.getEvalInfo(wfcltId)
        })


        var latitude = 37.49808164308036
        var longitude = 126.8456638053941

        val geocoder = Geocoder(this)
        try {
            var gList = geocoder.getFromLocation(latitude, longitude, 5)
            val cggNm : String = gList[2].subLocality
            val roadNm : String = gList[2].featureName
            mainViewModel.getLocationFacl(cggNm, roadNm)

            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 1, true)



        } catch (e : IOException) {
            Log.d("ttest", "지오코드 오류 : " + e.printStackTrace())
        }


        mainViewModel.locationFaclLiveData.observe(this, Observer {
            for(data in it) {
                val marker = MapPOIItem()
                if(data.faclLat != null && data.faclLng != null) {
                    marker.mapPoint = MapPoint.mapPointWithGeoCoord(data.faclLat, data.faclLng)
                    marker.itemName = data.faclNm

                    marker.markerType = MapPOIItem.MarkerType.CustomImage
                    marker.customImageResourceId = R.drawable.ic_location_pin_40
                    marker.isCustomImageAutoscale = false
                    marker.setCustomImageAnchor(0.5f, 1.0f)

                    mapView.addPOIItem(marker)
                }
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
                    bottomClickMap()
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
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("info")

        binding.bottomNav.apply {
            if(tag1 != null && tag1.isVisible) {
                this.menu.findItem(R.id.menu_category).isChecked = true
            }
            if(tag2 != null && tag2.isVisible) {
                this.menu.findItem(R.id.menu_bookmark).isChecked = true
            }
            if(tag3 != null && tag3.isVisible) {
                this.menu.findItem(R.id.menu_info).isChecked = true
            }

            if(tag1 == null && tag2 == null && tag3 == null) {
                this.menu.findItem(R.id.menu_map).isChecked = true
            }
        }
    }

    private fun updateMapView() {
        Log.d("ttest", "updateMapView")

        if(mainViewModel.mainStatus.value == 2) {
            mainViewModel.mainStatus.value = 1
        } else if (mainViewModel.mainStatus.value == 3) {
            mainViewModel.mainStatus.value = 2
        }
    }

    private fun bottomClickMap() {
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag("category")
        val tag2: Fragment? = supportFragmentManager.findFragmentByTag("bookmark")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("info")

        if(tag1 != null) {
            supportFragmentManager.beginTransaction().remove(categoryFragment).addToBackStack(null)?.commit()
        }
        if(tag2 != null) {
            supportFragmentManager.beginTransaction().remove(bookmarkFragment).addToBackStack(null)?.commit()
        }
        if(tag3 != null) {
            supportFragmentManager.beginTransaction().remove(infoFragment).addToBackStack(null)?.commit()
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        updateBottomMenu()
        updateMapView()
    }


}