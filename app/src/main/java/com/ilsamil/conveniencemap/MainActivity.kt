package com.ilsamil.conveniencemap

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ilsamil.conveniencemap.Fragments.*
import com.ilsamil.conveniencemap.adapters.EvalinfoAdapter
import com.ilsamil.conveniencemap.databinding.ActivityMainBinding
import com.ilsamil.conveniencemap.model.ServList
import com.ilsamil.conveniencemap.utils.Util
import net.daum.mf.map.api.MapPOIItem
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel : MainViewModel by viewModels()
//    private lateinit var mapView: MapView

    private val mapFragment: MapFragment by lazy { MapFragment.newInstance() }
    private val listFragment: ListFragment by lazy { ListFragment.newInstance() }
    private val infoFragment: InfoFragment by lazy { InfoFragment.newInstance() }

    private var shopServList = arrayListOf<ServList>()
    private var livingServList = arrayListOf<ServList>()
    private var educationServList = arrayListOf<ServList>()
    private var hospitalServList = arrayListOf<ServList>()
    private var publicServList = arrayListOf<ServList>()

    private var mapServList = arrayListOf<ServList>()
    private var mapCggNm = "지역"


    private var fLatitude = 1.00
    private var fLongitude = 1.00
    private var markedLat : Double = 1.00
    private var markedLng : Double = 1.00


    private lateinit var selectedMarker : ServList

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            getLocationFacInfo()
//            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(binding)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, mapFragment, "").commit()

//        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)



//        mainViewModel.mainStatus.observe(this, Observer {
//            when(it) {
//                1 -> {
//                    // 기본 메인 상태
//                    Log.d("ttest", "status = 1   기본 메인 상태")
//                    binding.bottomNav.visibility = View.VISIBLE
//                    binding.searchBtn.visibility = View.VISIBLE
//                    binding.refreshBtn.visibility = View.VISIBLE
//                    binding.mylocationBtn.visibility = View.VISIBLE
//                    binding.categoryLayout.visibility = View.VISIBLE
//                    binding.appTitleTv.visibility = View.VISIBLE
//                    binding.topLayout.visibility = View.VISIBLE
//                    binding.resultLayout.visibility = View.GONE
//
//                    // 검색결과 마커 제거
//                    if (mapView.findPOIItemByName("searchItem") != null) {
//                        val searchMarker = mapView.findPOIItemByName("searchItem")[0]
//                        mapView.removePOIItem(searchMarker)
//                    }
//
//                    binding.bottomNav.menu.findItem(R.id.menu_map).isChecked = true
//
////                    binding.groupCategoryBtn.visibility = View.VISIBLE
//                }
//                2 -> {
//                    // 주소검색 버튼 클릭
//                    // 바템네비, 검색, 재검색 제거
//                    Log.d("ttest", "status = 2   주소검색 버튼 클릭")
////                    binding.bottomNav.visibility = View.GONE
////                    binding.categoryLayout.visibility = View.GONE
////                    binding.searchBtn.visibility = View.GONE
////                    binding.resultLayout.visibility = View.GONE
////                    binding.refreshBtn.visibility = View.GONE
////                    binding.mylocationBtn.visibility = View.GONE
////                    binding.appTitleTv.visibility = View.GONE
////                    binding.topLayout.visibility = View.GONE
////                    binding.groupCategoryBtn.visibility = View.GONE
//                }
//                3 -> {
//                    // 검색 결과 화면
//                    Log.d("ttest", "status = 3   검색 결과 화면")
//                    binding.bottomNav.visibility = View.GONE
//                }
//                4 -> {
//                    // BotNav 이동
//                    // 검색, 재검색, 내위치 제거
//                    Log.d("ttest", "status = 4  BotNav 이동")
//                    binding.searchBtn.visibility = View.GONE
//                    binding.refreshBtn.visibility = View.GONE
//                    binding.mylocationBtn.visibility = View.GONE
////                    binding.categoryLayout.visibility = View.GONE
//
////                    binding.topLayout.visibility = View.GONE
////                    binding.appTitleTv.visibility = View.GONE
//
////                    binding.groupCategoryBtn.visibility = View.GONE
//                }
//                5 -> {
//                    // 로케이션 마커 클릭 진행중
//                    Log.d("ttest", "status = 5   로케이션 마커 클릭 진행중")
//                }
//                6 -> {
//                    // 디테일 프레그먼트 표시
//                    Log.d("ttest", "status = 6   디테일 프레그먼트 표시")
////                    binding.searchBtn.visibility = View.GONE
////                    binding.refreshBtn.visibility = View.GONE
////                    binding.mylocationBtn.visibility = View.GONE
////                    binding.groupCategoryBtn.visibility = View.GONE
////                    binding.resultLayout.visibility = View.GONE
//                }
//                7-> {
//                    // 로케이션 마커 클릭
//                    Log.d("ttest", "status = 7   로케이션 마커 클릭")
//
//                }
//            }
//        })

//        supportFragmentManager.beginTransaction().add(R.id.fragment_view, mapFragment, "map").commit()


//        binding.searchBtn.setOnClickListener{
////            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
//            removeMarker()
//            supportFragmentManager.beginTransaction().replace(R.id.main_constraint_layout, searchFragment, "search").addToBackStack(null).commit()
//        }



//        mainViewModel.evalInfoLiveData.observe(this, androidx.lifecycle.Observer {
//            val adapter = EvalinfoAdapter()
//            binding.resultRecyclerView.adapter = adapter
//            adapter.updateItems(it)
//            binding.resultLayout.startAnimation(fadeInAnim)
//            binding.resultLayout.visibility = View.VISIBLE
//            binding.progressBarCenter.visibility = View.GONE
//            binding.progressView.visibility = View.GONE
//        })

//        mainViewModel.categoryLiveData.observe(this, Observer {
//            binding.resultLayout.visibility = View.GONE
//            mainViewModel.mainStatus.value = 1
//
//            if(mapView.findPOIItemByTag(1) != null) {
//                mapView.deselectPOIItem(mapView.findPOIItemByTag(1))
//            }
//
//
//            when(it) {
//                0 -> {
//                    clearCategoryBtn()
//                    for(data in shopList) {
//                        mapView.addPOIItem(data)
//                    }
//                    for(data in livingList) {
//                        mapView.addPOIItem(data)
//                    }
//                    for(data in educationList) {
//                        mapView.addPOIItem(data)
//                    }
//                    for(data in hospitalList) {
//                        mapView.addPOIItem(data)
//                    }
//                    for(data in publicList) {
//                        mapView.addPOIItem(data)
//                    }
//
//                }
//                1 -> {
//                    binding.shopCategoryBtn.setBackgroundResource(R.drawable.button_category_click)
//                    binding.shopCategoryBtn.setTextColor(Color.WHITE)
//                    mapView.removeAllPOIItems()
//                    for(data in shopList) {
//                        mapView.addPOIItem(data)
//                    }
//
//                }
//                2 -> {
//                    binding.livingCategoryBtn.setBackgroundResource(R.drawable.button_category_click)
//                    binding.livingCategoryBtn.setTextColor(Color.WHITE)
//                    mapView.removeAllPOIItems()
//                    for(data in livingList) {
//                        mapView.addPOIItem(data)
//                    }
//                }
//                3 -> {
//                    binding.educationCategoryBtn.setBackgroundResource(R.drawable.button_category_click)
//                    binding.educationCategoryBtn.setTextColor(Color.WHITE)
//                    mapView.removeAllPOIItems()
//                    for(data in educationList) {
//                        mapView.addPOIItem(data)
//                    }
//                }
////                4 -> {
////                    binding.hospitalCategoryBtn.setBackgroundResource(R.drawable.button_click)
////                    binding.hospitalCategoryBtn.setTextColor(Color.WHITE)
////                    mapView.removeAllPOIItems()
////                    for(data in hospitalList) {
////                        mapView.addPOIItem(data)
////                    }
////                }
//                5 -> {
//                    binding.publicCategoryBtn.setBackgroundResource(R.drawable.button_category_click)
//                    binding.publicCategoryBtn.setTextColor(Color.WHITE)
//                    mapView.removeAllPOIItems()
//                    for(data in publicList) {
//                        mapView.addPOIItem(data)
//                    }
//                }
//            }
//        })





//        binding.refreshBtn.setOnClickListener {
//            binding.progressBarCenter.visibility = View.VISIBLE
//            binding.progressView.visibility = View.VISIBLE
//
//            val refreshLocation = mapView.mapCenterPoint
//            markedLat = refreshLocation.mapPointGeoCoord.latitude
//            markedLng = refreshLocation.mapPointGeoCoord.longitude
//            val geocoder = Geocoder(this)
//
//            try {
//                val gList = geocoder.getFromLocation(markedLat, markedLng, 5)
//                val cggNm = if (gList[0].subLocality != null) {
//                    gList[0].subLocality
//                } else {
//                    gList[0].locality
//                }
//                val roadNm = ""
//                Log.d("ttest", "지역 재검색 위치 : $cggNm")
//
//                getMapFacInfo(cggNm, roadNm)
//                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(markedLat, markedLng), true)
//
//            } catch (e : IOException) {
//                Log.d("ttest", "지오코드 오류 : " + e.printStackTrace())
//            }
//
//
//        }

        //상세보기 클릭
//        binding.resultDetailBtn.setOnClickListener{
//            mainViewModel.mainStatus.value = 6
//            supportFragmentManager.beginTransaction().replace(R.id.main_constraint_layout, detailFragment, "detail").addToBackStack(null).commit()
//            val userObject = selectedMarker
//            mainViewModel.detailLiveData.value = userObject
//        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationFacInfo() {
        binding.progressBarCenter.visibility = View.VISIBLE
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if(location != null) {
            fLatitude = location.latitude
            fLongitude = location.longitude
            markedLat = fLatitude
            markedLng = fLongitude
            Log.d("ttest", "latitude : " + fLatitude)
            Log.d("ttest", "longitude : " + fLongitude)

            val geocoder = Geocoder(this)
            try {
                val gList = geocoder.getFromLocation(fLatitude, fLongitude, 5)
                val cggNm = if (gList[0].subLocality != null) {
                    gList[0].subLocality
                } else {
                    gList[0].locality
                }

                mapCggNm = cggNm
                val roadNm = ""
                Log.d("ttest", "현재 위치 : " + cggNm + " " + roadNm)

//                getMapFacInfo(cggNm, roadNm)

                val bundle = Bundle()
                bundle.putString("cggNm", cggNm)
                bundle.putDouble("fLatitude", fLatitude)
                bundle.putDouble("fLongitude", fLongitude)
                binding.progressBarCenter.visibility = View.GONE
                mapFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, mapFragment, "map").commit()


            } catch (e : IOException) {
                Log.d("ttest", "지오코드 오류 : " + e.printStackTrace())
            }
        } else {
            Log.d("ttest", "현재 위치 : null")
        }
    }





    private fun replaceFragment(binding: ActivityMainBinding) {
        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.menu_category -> {
                    if(mapServList.isNotEmpty()) {
                        val bundle = Bundle()
                        bundle.putString("cggNm", mapCggNm)
                        bundle.putSerializable("shopServList", shopServList)
                        bundle.putSerializable("livingServList", livingServList)
                        bundle.putSerializable("educationServList", educationServList)
                        bundle.putSerializable("hospitalServList", hospitalServList)
                        bundle.putSerializable("publicServList", publicServList)

                        bundle.putSerializable("mapServList", mapServList)

                        listFragment.arguments = bundle
                        supportFragmentManager.popBackStackImmediate("list", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, listFragment, "list").addToBackStack("list").commit()
                    }

                }
                R.id.menu_map -> {
//                    bottomClickMap()
                    supportFragmentManager.popBackStackImmediate("map", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, mapFragment, "map").addToBackStack("map").commit()
                }
                R.id.menu_info -> {
                    mainViewModel.mainStatus.value = 4
                    supportFragmentManager.popBackStackImmediate("info", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, infoFragment, "info").addToBackStack("info").commit()
                }
            }
            true
        }
    }


    // 뒤로가기 시 bottomnav 클릭 활성화
    private fun updateBottomMenu() {
        Log.d("ttest", "updateBottomMenu 실행")
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag("list")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("info")


        binding.bottomNav.apply {
            if(tag1 != null && tag1.isVisible) {
                this.menu.findItem(R.id.menu_category).isChecked = true
            }
            if(tag3 != null && tag3.isVisible) {
                this.menu.findItem(R.id.menu_info).isChecked = true
            }
            if(tag1 == null && tag3 == null) {
                this.menu.findItem(R.id.menu_map).isChecked = true
                mainViewModel.mainStatus.value = 1
            }
        }
    }

    private fun updateMapView() {
        Log.d("ttest", "updateMapView 실행")

        if(mainViewModel.mainStatus.value == 2) {
            mainViewModel.mainStatus.value = 1
        } else if (mainViewModel.mainStatus.value == 3) {
            mainViewModel.mainStatus.value = 2
        }
    }


//    private fun removeMarker() {
//        Log.d("ttest", "removeMarker 실행")
//        if(mainViewModel.mainStatus.value == 7) {
//            if(mapView.findPOIItemByTag(1) != null) {
//                val searchMarker = mapView.findPOIItemByTag(1)
//                mapView.deselectPOIItem(searchMarker)
//                searchMarker.tag = 0
//            }
//
//            binding.resultLayout.visibility = View.GONE
//            binding.progressView.visibility = View.GONE
//            mainViewModel.mainStatus.value = 1
//        }
//    }

    private fun bottomClickMap() {
        mainViewModel.mainStatus.value = 1
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag("list")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("info")

        if(tag1 != null) {
            supportFragmentManager.beginTransaction().remove(listFragment).addToBackStack(null)?.commit()
        }
        if(tag3 != null) {
            supportFragmentManager.beginTransaction().remove(infoFragment).addToBackStack(null)?.commit()
        }
    }


    private fun changeFaclType(faclType : String) : String {
        when(faclType) {
            //음식 및 상점
            in "UC0B01", "UC0R02", "UC0E01", "UC0B02" -> {
                return "음식 및 상점"
            }
            //생활시설
            in "UC0A05", "UC0J01", "UC0H03", "UC0I01", "UC0A01", "UC0A02", "UC0T01", "UC0A07", "UC0G09", "UC0C01", "UC0C04",
            "UC0C05", "UC0A13", "UC0R01", "UC0J02", "UC0U02", "UC0V01", "UC0L02", "UC0K02"
            -> {
                return "생활시설"
            }
            //교육시설
            in "UC0H01", "UC0G02", "UC0A15", "UC0G03", "UC0G08", "UC0G01", "UC0N02", "UC0G04", "UC0G05", "UC0G06", "UC0G07" -> {
                return "교육시설"
            }
            //병원
            in "UC0F01", "UC0F03", "UC0F02", "UC0A14" -> {
                return "기타"
            }
            // 공공기관 및 기타
            in "UC0A10", "UC0K03", "UC0Q01", "UC0T02", "UC0H05", "UC0A03", "UC0A04", "UC0A08", "UC0A09", "UC0A11", "UC0A06",
            "UC0K01", "UC0K05", "UC0H02", "UC0H04", "UC0K04", "UC0K06", "UC0N01", "UC0O02", "UC0B03", "UC0O01", "UC0C03",
            "UC0P01", "UC0A12", "UC0M01", "UC0C02", "UC0S01", "UC0D01", "UC0Q02", "UC0I02", "UC0U01", "UC0U03", "UC0L01" -> {
                return "기타"
            }
        }
        // 제외
        // UC0U04:다세대주택,


        return ""
    }

//    private fun removeCategoryData() {
//        shopList.clear()
//        livingList.clear()
//        educationList.clear()
//        publicList.clear()
//        shopServList.clear()
//        livingServList.clear()
//        educationServList.clear()
//        publicServList.clear()
//
//        mapServList.clear()
//    }


    override fun onBackPressed() {
        when(mainViewModel.mainStatus.value.toString()) {
            in "1","2" -> {
                super.onBackPressed()
                updateMapView()
            }
            in "3" -> {
                super.onBackPressed()
            }
            in "4" -> {
                super.onBackPressed()
                updateBottomMenu()
            }
            in "5" -> {
                super.onBackPressed()
            }
            in "6" -> {
                super.onBackPressed()
            }
            in "7" -> {
//                removeMarker()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }


}