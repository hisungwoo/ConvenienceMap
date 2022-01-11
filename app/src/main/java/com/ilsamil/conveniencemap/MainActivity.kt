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
import com.ilsamil.conveniencemap.utils.ChangeType
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.io.IOException

class MainActivity : AppCompatActivity(), MapView.MapViewEventListener {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel : MainViewModel by viewModels()
    private lateinit var mapView: MapView

    private val mapFragment: MapFragment by lazy { MapFragment.newInstance() }
    private val listFragment: ListFragment by lazy { ListFragment.newInstance() }
    private val infoFragment: InfoFragment by lazy { InfoFragment.newInstance() }
    private val searchFragment: SearchFragment by lazy { SearchFragment.newInstance() }

    private lateinit var fadeInAnim : Animation
    private lateinit var fadeOutAnim : Animation

    private var shopList = arrayListOf<MapPOIItem>()
    private var livingList = arrayListOf<MapPOIItem>()
    private var educationList = arrayListOf<MapPOIItem>()
    private var hospitalList = arrayListOf<MapPOIItem>()
    private var publicList = arrayListOf<MapPOIItem>()



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

        mapView.setMapViewEventListener(this)
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading


        //카테고리 클릭
        binding.shopCategoryBtn.setOnClickListener {
            categoryClick(1)
        }

        binding.livingCategoryBtn.setOnClickListener{
            categoryClick(2)
        }

        binding.educationCategoryBtn.setOnClickListener {
            categoryClick(3)
        }

        binding.hospitalCategoryBtn.setOnClickListener {
            categoryClick(4)
        }

        binding.publicCategoryBtn.setOnClickListener {
            categoryClick(5)
        }


        mainViewModel.mainStatus.observe(this, Observer {
            when(it) {
                1 -> {
                    // 기본 메인 상태
                    binding.bottomNav.visibility = View.VISIBLE
                    binding.searchBtn.visibility = View.VISIBLE
                    binding.refreshBtn.visibility = View.VISIBLE
                    binding.mylocationBtn.visibility = View.VISIBLE
                    binding.groupCategoryBtn.visibility = View.VISIBLE
                }
                2 -> {
                    // 주소검색 버튼 클릭
                    // 바템네비, 검색, 재검색 제거
                    binding.bottomNav.visibility = View.GONE
                    binding.searchBtn.visibility = View.GONE
                    binding.resultLayout.visibility = View.GONE
                    binding.refreshBtn.visibility = View.GONE
                    binding.mylocationBtn.visibility = View.GONE
                    mapView.removeAllPOIItems()
                    binding.groupCategoryBtn.visibility = View.GONE
                }
                3 -> {
                    // 검색 결과 화면
                    binding.bottomNav.visibility = View.GONE
                }
                4 -> {
                    // BotNav 이동
                    // 검색, 재검색, 내위치 제거
                    binding.searchBtn.visibility = View.GONE
                    binding.refreshBtn.visibility = View.GONE
                    binding.mylocationBtn.visibility = View.GONE
                    binding.groupCategoryBtn.visibility = View.GONE
                }

            }
        })

        replaceFragment(binding)
        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        getLocationFacInfo()
//        supportFragmentManager.beginTransaction().add(R.id.fragment_view, mapFragment, "map").commit()


        binding.searchBtn.setOnClickListener{
            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
            supportFragmentManager.beginTransaction().add(R.id.fragment_view, searchFragment, "search").addToBackStack(null).commit()
        }


        val adapter = EvalinfoAdapter()
        binding.resultRecyclerView.adapter = adapter
        mainViewModel.evalInfoLiveData.observe(this, androidx.lifecycle.Observer {
            adapter.updateItems(it)
        })

        mainViewModel.categoryLiveData.observe(this, Observer {
            when(it) {
                0 -> {
                    clearCategoryBtn()
                    for(data in shopList) {
                        mapView.addPOIItem(data)
                    }
                    for(data in livingList) {
                        mapView.addPOIItem(data)
                    }
                    for(data in educationList) {
                        mapView.addPOIItem(data)
                    }
                    for(data in hospitalList) {
                        mapView.addPOIItem(data)
                    }
                    for(data in publicList) {
                        mapView.addPOIItem(data)
                    }

                }
                1 -> {
                    binding.shopCategoryBtn.setBackgroundResource(R.drawable.button_click)
                    binding.shopCategoryBtn.setTextColor(Color.WHITE)
                    mapView.removeAllPOIItems()
                    for(data in shopList) {
                        mapView.addPOIItem(data)
                    }

                }
                2 -> {
                    binding.livingCategoryBtn.setBackgroundResource(R.drawable.button_click)
                    binding.livingCategoryBtn.setTextColor(Color.WHITE)
                    mapView.removeAllPOIItems()
                    for(data in livingList) {
                        mapView.addPOIItem(data)
                    }
                }
                3 -> {
                    binding.educationCategoryBtn.setBackgroundResource(R.drawable.button_click)
                    binding.educationCategoryBtn.setTextColor(Color.WHITE)
                    mapView.removeAllPOIItems()
                    for(data in educationList) {
                        mapView.addPOIItem(data)
                    }
                }
                4 -> {
                    binding.hospitalCategoryBtn.setBackgroundResource(R.drawable.button_click)
                    binding.hospitalCategoryBtn.setTextColor(Color.WHITE)
                    mapView.removeAllPOIItems()
                    for(data in hospitalList) {
                        mapView.addPOIItem(data)
                    }
                }
                5 -> {
                    binding.publicCategoryBtn.setBackgroundResource(R.drawable.button_click)
                    binding.publicCategoryBtn.setTextColor(Color.WHITE)
                    mapView.removeAllPOIItems()
                    for(data in publicList) {
                        mapView.addPOIItem(data)
                    }
                }
            }
        })

        mainViewModel.movePin.observe(this, Observer {
            mainViewModel.mainStatus.value = 3

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

        mainViewModel.locationFaclLiveData.observe(this, Observer {
            for(data in it) {
                val marker = MapPOIItem()
                when(data.faclTyCd.toString()) {
                    //음식 및 상점
                    in "UC0B01", "UC0R02", "UC0E01", "UC0B02", "UC0K02" -> {
                        if (data.faclLat != null && data.faclLng != null) {
                            marker.mapPoint = MapPoint.mapPointWithGeoCoord(data.faclLat, data.faclLng)
                            marker.itemName = data.faclNm
                            marker.markerType = MapPOIItem.MarkerType.CustomImage
                            marker.customImageResourceId = R.drawable.ic_location_pin_40
                            marker.isCustomImageAutoscale = false
                            marker.setCustomImageAnchor(0.5f, 1.0f)
                            shopList.add(marker)
                        }
                    }
                    //생활시설
                    in "UC0A05", "UC0J01", "UC0H03", "UC0I01", "UC0A01", "UC0A02", "UC0T01", "UC0A07", "UC0G09", "UC0C01", "UC0C04",
                    "UC0C05", "UC0U01", "UC0U03", "UC0U04", "UC0A13", "UC0R01", "UC0J02", "UC0U02", "UC0L01", "UC0V01", "UC0L02"
                    -> {
                        if (data.faclLat != null && data.faclLng != null) {
                            marker.mapPoint = MapPoint.mapPointWithGeoCoord(data.faclLat, data.faclLng)
                            marker.itemName = data.faclNm
                            marker.markerType = MapPOIItem.MarkerType.CustomImage
                            marker.customImageResourceId = R.drawable.ic_location_pin_40
                            marker.isCustomImageAutoscale = false
                            marker.setCustomImageAnchor(0.5f, 1.0f)
                            livingList.add(marker)
                        }
                    }
                    //교육시설
                    in "UC0H01", "UC0G02", "UC0A15", "UC0G03", "UC0G08", "UC0G01", "UC0N02", "UC0G04", "UC0G05", "UC0G06", "UC0G07" -> {
                        if (data.faclLat != null && data.faclLng != null) {
                            marker.mapPoint = MapPoint.mapPointWithGeoCoord(data.faclLat, data.faclLng)
                            marker.itemName = data.faclNm
                            marker.markerType = MapPOIItem.MarkerType.CustomImage
                            marker.customImageResourceId = R.drawable.ic_location_pin_40
                            marker.isCustomImageAutoscale = false
                            marker.setCustomImageAnchor(0.5f, 1.0f)
                            educationList.add(marker)
                        }
                    }
                    //병원
                    in "UC0F01", "UC0F03", "UC0F02", "UC0A14" -> {
                        if (data.faclLat != null && data.faclLng != null) {
                            marker.mapPoint = MapPoint.mapPointWithGeoCoord(data.faclLat, data.faclLng)
                            marker.itemName = data.faclNm
                            marker.markerType = MapPOIItem.MarkerType.CustomImage
                            marker.customImageResourceId = R.drawable.ic_location_pin_40
                            marker.isCustomImageAutoscale = false
                            marker.setCustomImageAnchor(0.5f, 1.0f)
                            hospitalList.add(marker)
                        }
                    }
                    // 공공기관 및 기타
                    in "UC0A10", "UC0K03", "UC0Q01", "UC0T02", "UC0H05", "UC0A03", "UC0A04", "UC0A08", "UC0A09", "UC0A11", "UC0A06",
                    "UC0K01", "UC0K05", "UC0H02", "UC0H04", "UC0K04", "UC0K06", "UC0N01", "UC0O02", "UC0B03", "UC0O01", "UC0C03",
                    "UC0P01", "UC0A12", "UC0M01", "UC0C02", "UC0S01", "UC0D01", "UC0Q02", "UC0I02" -> {
                        if (data.faclLat != null && data.faclLng != null) {
                            marker.mapPoint = MapPoint.mapPointWithGeoCoord(data.faclLat, data.faclLng)
                            marker.itemName = data.faclNm
                            marker.markerType = MapPOIItem.MarkerType.CustomImage
                            marker.customImageResourceId = R.drawable.ic_location_pin_40
                            marker.isCustomImageAutoscale = false
                            marker.setCustomImageAnchor(0.5f, 1.0f)
                            publicList.add(marker)
                        }
                    }
                }

                if (data.faclLat != null && data.faclLng != null) {
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


        binding.mylocationBtn.setOnClickListener {
            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        }

        binding.refreshBtn.setOnClickListener {
            getMapFacInfo("구로구","구로동로47길")
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLocationFacInfo() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if(location != null) {
            val latitude = location.latitude
            val longitude = location.longitude

            val geocoder = Geocoder(this)
            try {
                var gList = geocoder.getFromLocation(latitude, longitude, 5)
                val cggNm : String = gList[1].subLocality
                val roadNm : String = gList[1].featureName
                Log.d("ttest", "현재 위치 : " + cggNm + " " + roadNm)

//                mainViewModel.getLocationFacl(cggNm, roadNm, "")
                getMapFacInfo(cggNm, roadNm)
                mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 1, true)

            } catch (e : IOException) {
                Log.d("ttest", "지오코드 오류 : " + e.printStackTrace())
            }
        } else {
            Log.d("ttest", "현재 위치 : null")
        }
    }

    private fun categoryClick(btn : Int) {
        if (btn == mainViewModel.categoryLiveData.value) {
            mainViewModel.categoryLiveData.value = 0
        } else {
            clearCategoryBtn()
            mainViewModel.categoryLiveData.value = btn
        }
    }

    private fun clearCategoryBtn() {
        binding.shopCategoryBtn.setBackgroundResource(R.drawable.button_refresh)
        binding.livingCategoryBtn.setBackgroundResource(R.drawable.button_refresh)
        binding.educationCategoryBtn.setBackgroundResource(R.drawable.button_refresh)
        binding.hospitalCategoryBtn.setBackgroundResource(R.drawable.button_refresh)
        binding.publicCategoryBtn.setBackgroundResource(R.drawable.button_refresh)
        binding.shopCategoryBtn.setTextColor(ContextCompat.getColor(this, R.color.category_text))
        binding.livingCategoryBtn.setTextColor(ContextCompat.getColor(this, R.color.category_text))
        binding.educationCategoryBtn.setTextColor(ContextCompat.getColor(this, R.color.category_text))
        binding.hospitalCategoryBtn.setTextColor(ContextCompat.getColor(this, R.color.category_text))
        binding.publicCategoryBtn.setTextColor(ContextCompat.getColor(this, R.color.category_text))
    }


    private fun getMapFacInfo(cggNm : String , roadNm : String) {
        mapView.removeAllPOIItems()
        mainViewModel.getLocationFacl(cggNm, roadNm)
    }

    private fun replaceFragment(binding: ActivityMainBinding) {
        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.menu_category -> {
                    mainViewModel.mainStatus.value = 4
                    supportFragmentManager.popBackStackImmediate("category", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_view, listFragment, "category").addToBackStack("category").commit()
                }
                R.id.menu_map -> {
                    bottomClickMap()
                }
                R.id.menu_info -> {
                    mainViewModel.mainStatus.value = 4
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
        Log.d("ttest", "updateMapView")

        if(mainViewModel.mainStatus.value == 2) {
            mainViewModel.mainStatus.value = 1
        } else if (mainViewModel.mainStatus.value == 3) {
            mainViewModel.mainStatus.value = 2
        }
    }

    private fun bottomClickMap() {
        mainViewModel.mainStatus.value = 1
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag("category")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("info")

        if(tag1 != null) {
            supportFragmentManager.beginTransaction().remove(listFragment).addToBackStack(null)?.commit()
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

    override fun onMapViewInitialized(p0: MapView?) {
        //MapView가 사용가능 한 상태가 되었음을 알려준다.
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
        //지도 중심 좌표가 이동한 경우 호출된다.
        if (mapView.currentLocationTrackingMode != MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving) {
            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
        }
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
        //지도 확대/축소 레벨이 변경된 경우 호출된다.
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        //사용자가 지도 위를 터치한 경우 호출된다.
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
        //사용자가 지도 위 한 지점을 더블 터치한 경우 호출된다.
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
        //사용자가 지도 위 한 지점을 길게 누른 경우(long press) 호출된다.
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
        //사용자가 지도 드래그를 시작한 경우 호출된다.
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
        //사용자가 지도 드래그를 끝낸 경우 호출된다.
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
        //지도의 이동이 완료된 경우 호출된다.
    }

}