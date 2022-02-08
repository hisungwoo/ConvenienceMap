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
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.io.IOException

class MainActivity : AppCompatActivity(), MapView.MapViewEventListener, MapView.POIItemEventListener,
    MapView.CurrentLocationEventListener {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel : MainViewModel by viewModels()
    private lateinit var mapView: MapView

    private val mapFragment: MapFragment by lazy { MapFragment.newInstance() }
    private val listFragment: ListFragment by lazy { ListFragment.newInstance() }
    private val infoFragment: InfoFragment by lazy { InfoFragment.newInstance() }
    private val searchFragment: SearchFragment by lazy { SearchFragment.newInstance() }
    private val detailFragment: DetailFragment by lazy { DetailFragment.newInstance() }

    private lateinit var fadeInAnim : Animation
    private lateinit var fadeOutAnim : Animation

    private var shopList = arrayListOf<MapPOIItem>()
    private var livingList = arrayListOf<MapPOIItem>()
    private var educationList = arrayListOf<MapPOIItem>()
    private var hospitalList = arrayListOf<MapPOIItem>()
    private var publicList = arrayListOf<MapPOIItem>()

    private var fLatitude = 1.00
    private var fLongitude = 1.00
    private var mCurrentLat : Double = 1.00
    private var mCurrentLng : Double = 1.00

    private lateinit var selectedMarker : MapPOIItem

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            getLocationFacInfo()
            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
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
        mapView.setPOIItemEventListener(this)
        mapView.setMapViewEventListener(this)
        mapView.setCurrentLocationEventListener(this)


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

//        binding.hospitalCategoryBtn.setOnClickListener {
//            categoryClick(4)
//        }

        binding.publicCategoryBtn.setOnClickListener {
            categoryClick(5)
        }


        mainViewModel.mainStatus.observe(this, Observer {
            when(it) {
                1 -> {
                    // 기본 메인 상태
                    Log.d("ttest", "status = 1   기본 메인 상태")
                    binding.bottomNav.visibility = View.VISIBLE
                    binding.searchBtn.visibility = View.VISIBLE
                    binding.refreshBtn.visibility = View.VISIBLE
                    binding.mylocationBtn.visibility = View.VISIBLE
                    binding.categoryLayout.visibility = View.VISIBLE
                    binding.appTitleTv.visibility = View.VISIBLE
                    binding.topLayout.visibility = View.VISIBLE

                    // 검색결과 마커 제거
                    if (mapView.findPOIItemByName("searchItem") != null) {
                        val searchMarker = mapView.findPOIItemByName("searchItem")[0]
                        mapView.removePOIItem(searchMarker)
                    }

//                    binding.groupCategoryBtn.visibility = View.VISIBLE
                }
                2 -> {
                    // 주소검색 버튼 클릭
                    // 바템네비, 검색, 재검색 제거
                    Log.d("ttest", "status = 2   주소검색 버튼 클릭")
                    binding.bottomNav.visibility = View.GONE
                    binding.categoryLayout.visibility = View.GONE
                    binding.searchBtn.visibility = View.GONE
                    binding.resultLayout.visibility = View.GONE
                    binding.refreshBtn.visibility = View.GONE
                    binding.mylocationBtn.visibility = View.GONE
                    binding.appTitleTv.visibility = View.GONE
                    binding.topLayout.visibility = View.GONE
//                    binding.groupCategoryBtn.visibility = View.GONE
                }
                3 -> {
                    // 검색 결과 화면
                    Log.d("ttest", "status = 3   검색 결과 화면")
                    binding.bottomNav.visibility = View.GONE
                }
                4 -> {
                    // BotNav 이동
                    // 검색, 재검색, 내위치 제거
                    Log.d("ttest", "status = 4  BotNav 이동")
                    binding.searchBtn.visibility = View.GONE
                    binding.refreshBtn.visibility = View.GONE
                    binding.mylocationBtn.visibility = View.GONE
//                    binding.groupCategoryBtn.visibility = View.GONE
                }
                5 -> {
                    // 로케이션 마커 클릭
                    Log.d("ttest", "status = 5   로케이션 마커 클릭")
                    binding.refreshBtn.visibility = View.GONE
                    binding.bottomNav.visibility = View.GONE
                    binding.mylocationBtn.visibility = View.GONE
                }
                6 -> {
                    // 디테일 프레그먼트 표시
                    Log.d("ttest", "status = 6   디테일 프레그먼트 표시")
//                    binding.searchBtn.visibility = View.GONE
//                    binding.refreshBtn.visibility = View.GONE
//                    binding.mylocationBtn.visibility = View.GONE
//                    binding.groupCategoryBtn.visibility = View.GONE
//                    binding.resultLayout.visibility = View.GONE
                }

            }
        })

        replaceFragment(binding)
        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)

//        supportFragmentManager.beginTransaction().add(R.id.fragment_view, mapFragment, "map").commit()


        binding.searchBtn.setOnClickListener{
//            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
            removeMarker()
            supportFragmentManager.beginTransaction().add(R.id.fragment_view, searchFragment, "search").addToBackStack(null).commit()
        }


        val adapter = EvalinfoAdapter()
        binding.resultRecyclerView.adapter = adapter
        mainViewModel.evalInfoLiveData.observe(this, androidx.lifecycle.Observer {
            adapter.updateItems(it)
        })

        mainViewModel.categoryLiveData.observe(this, Observer {
            binding.resultLayout.visibility = View.GONE
            mainViewModel.mainStatus.value = 1

            if(mapView.findPOIItemByTag(1) != null) {
                mapView.deselectPOIItem(mapView.findPOIItemByTag(1))
            }


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
                    binding.shopCategoryBtn.setBackgroundResource(R.drawable.button_category_click)
                    binding.shopCategoryBtn.setTextColor(Color.WHITE)
                    mapView.removeAllPOIItems()
                    for(data in shopList) {
                        mapView.addPOIItem(data)
                    }

                }
                2 -> {
                    binding.livingCategoryBtn.setBackgroundResource(R.drawable.button_category_click)
                    binding.livingCategoryBtn.setTextColor(Color.WHITE)
                    mapView.removeAllPOIItems()
                    for(data in livingList) {
                        mapView.addPOIItem(data)
                    }
                }
                3 -> {
                    binding.educationCategoryBtn.setBackgroundResource(R.drawable.button_category_click)
                    binding.educationCategoryBtn.setTextColor(Color.WHITE)
                    mapView.removeAllPOIItems()
                    for(data in educationList) {
                        mapView.addPOIItem(data)
                    }
                }
//                4 -> {
//                    binding.hospitalCategoryBtn.setBackgroundResource(R.drawable.button_click)
//                    binding.hospitalCategoryBtn.setTextColor(Color.WHITE)
//                    mapView.removeAllPOIItems()
//                    for(data in hospitalList) {
//                        mapView.addPOIItem(data)
//                    }
//                }
                5 -> {
                    binding.publicCategoryBtn.setBackgroundResource(R.drawable.button_category_click)
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
            customMarker.itemName = "searchItem"
            customMarker.tag = 1
            customMarker.mapPoint = MapPoint.mapPointWithGeoCoord(faclLat, faclLng)
            customMarker.markerType = MapPOIItem.MarkerType.CustomImage
            customMarker.isShowCalloutBalloonOnTouch = false
            customMarker.userObject = it


            when(changeFaclType(faclTyCd.toString())) {
                "음식 및 상점" -> {
                    customMarker.customImageResourceId = R.drawable.category_click_shop
                }
                "생활시설" -> {
                    customMarker.customImageResourceId = R.drawable.category_click_living
                }
                "교육시설" -> {
                    customMarker.customImageResourceId = R.drawable.category_click_education
                }
                "기타" -> {
                    customMarker.customImageResourceId = R.drawable.category_click_public
                }
            }

            customMarker.isCustomImageAutoscale = true
            customMarker.setCustomImageAnchor(0.5f, 1.0f)

            mapView.addPOIItem(customMarker)

            binding.resultRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.HORIZONTAL, false)
            binding.resultNmTv.text = faclNm
            binding.resultTypeTv.text = faclTyCd?.let { it1 -> Util().changeType(it1) }
            binding.resultLocationTv.text = lcMnad

            binding.resultLayout.startAnimation(fadeInAnim)
            binding.resultLayout.visibility = View.VISIBLE

            mainViewModel.getEvalInfo(wfcltId, "1")
        })

        mainViewModel.locationFaclLiveData.observe(this, Observer {
            for(data in it) {
                if (data.faclLat != null && data.faclLng != null) {
                    val marker = MapPOIItem()
                    marker.mapPoint = MapPoint.mapPointWithGeoCoord(data.faclLat, data.faclLng)
                    marker.itemName = data.faclNm
                    marker.userObject = data
                    marker.markerType = MapPOIItem.MarkerType.CustomImage
                    marker.selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                    marker.showAnimationType = MapPOIItem.ShowAnimationType.SpringFromGround
                    marker.isShowCalloutBalloonOnTouch = false
                    marker.isCustomImageAutoscale = true
                    marker.setCustomImageAnchor(0.5f, 1.0f)

                    when(changeFaclType(data.faclTyCd.toString())) {
                        "음식 및 상점" -> {
                            marker.customImageResourceId = R.drawable.category_shop_img
                            marker.customSelectedImageResourceId = R.drawable.category_click_shop
                            shopList.add(marker)
                            mapView.addPOIItem(marker)
                        }
                        "생활시설" -> {
                            marker.customImageResourceId = R.drawable.category_living_img
                            marker.customSelectedImageResourceId = R.drawable.category_click_living
                            livingList.add(marker)
                            mapView.addPOIItem(marker)
                        }
                        "교육시설" -> {
                            marker.customImageResourceId = R.drawable.category_education_img
                            marker.customSelectedImageResourceId = R.drawable.category_click_education
                            educationList.add(marker)
                            mapView.addPOIItem(marker)
                        }
                        "기타" -> {
                            marker.customImageResourceId = R.drawable.category_public_img
                            marker.customSelectedImageResourceId = R.drawable.category_click_public
                            publicList.add(marker)
                            mapView.addPOIItem(marker)
                        }
                    }
                }
            }
        })


        binding.mylocationBtn.setOnClickListener {
            if(mCurrentLat != 1.00) {
                val currentMapPoint = MapPoint.mapPointWithGeoCoord(mCurrentLat, mCurrentLng)
                mapView.setMapCenterPoint(currentMapPoint, true)
            } else {
                val currentMapPoint = MapPoint.mapPointWithGeoCoord(fLatitude, fLongitude)
                mapView.setMapCenterPoint(currentMapPoint, true)
            }
            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        }

        binding.refreshBtn.setOnClickListener {
            val refreshLocation = mapView.mapCenterPoint
            val refreshLat = refreshLocation.mapPointGeoCoord.latitude
            val refreshLong = refreshLocation.mapPointGeoCoord.longitude
            val geocoder = Geocoder(this)

            try {
                val gList = geocoder.getFromLocation(refreshLat, refreshLong, 5)
                val cggNm = if (gList[0].subLocality != null) {
                    gList[0].subLocality
                } else {
                    gList[0].locality
                }
                val roadNm = ""
                Log.d("ttest", "지역 재검색 위치 : $cggNm")

                getMapFacInfo(cggNm, roadNm)
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(refreshLat, refreshLong), true)

            } catch (e : IOException) {
                Log.d("ttest", "지오코드 오류 : " + e.printStackTrace())
            }


        }

        //상세보기 클릭
        binding.resultDetailBtn.setOnClickListener{
            mainViewModel.mainStatus.value = 6
            supportFragmentManager.beginTransaction().replace(R.id.main_constraint_layout, detailFragment, "detail").addToBackStack(null).commit()
            val userObject = selectedMarker.userObject as ServList
            mainViewModel.detailLiveData.value = userObject
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationFacInfo() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if(location != null) {
            fLatitude = location.latitude
            fLongitude = location.longitude
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
                val roadNm = ""
                Log.d("ttest", "현재 위치 : " + cggNm + " " + roadNm)

//                mainViewModel.getLocationFacl(cggNm, roadNm, "")
                getMapFacInfo(cggNm, roadNm)
                mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(fLatitude, fLongitude), 3, true)


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
        binding.shopCategoryBtn.setBackgroundResource(R.color.button_transparency)
        binding.livingCategoryBtn.setBackgroundResource(R.color.button_transparency)
        binding.educationCategoryBtn.setBackgroundResource(R.color.button_transparency)
        binding.publicCategoryBtn.setBackgroundResource(R.color.button_transparency)
        binding.shopCategoryBtn.setTextColor(ContextCompat.getColor(this, R.color.category_text))
        binding.livingCategoryBtn.setTextColor(ContextCompat.getColor(this, R.color.category_text))
        binding.educationCategoryBtn.setTextColor(ContextCompat.getColor(this, R.color.category_text))
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
        Log.d("ttest", "updateBottomMenu 실행")
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
        Log.d("ttest", "updateMapView 실행")

        if(mainViewModel.mainStatus.value == 2) {
            mainViewModel.mainStatus.value = 1
        } else if (mainViewModel.mainStatus.value == 3) {
            mainViewModel.mainStatus.value = 2
        }
    }


    private fun removeMarker() {
        Log.d("ttest", "removeMarker 실행")
        if(mainViewModel.mainStatus.value == 5) {
            if(mapView.findPOIItemByTag(1) != null) {
                val searchMarker = mapView.findPOIItemByTag(1)
                mapView.deselectPOIItem(searchMarker)
                searchMarker.tag = 0
            }

            binding.resultLayout.visibility = View.GONE
            binding.clickMarkerView.visibility = View.GONE
            mainViewModel.mainStatus.value = 1
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


    private fun changeFaclType(faclType : String) : String {
        when(faclType) {
            //음식 및 상점
            in "UC0B01", "UC0R02", "UC0E01", "UC0B02", "UC0K02" -> {
                return "음식 및 상점"
            }
            //생활시설
            in "UC0A05", "UC0J01", "UC0H03", "UC0I01", "UC0A01", "UC0A02", "UC0T01", "UC0A07", "UC0G09", "UC0C01", "UC0C04",
            "UC0C05", "UC0U01", "UC0U03", "UC0U04", "UC0A13", "UC0R01", "UC0J02", "UC0U02", "UC0L01", "UC0V01", "UC0L02"
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
            "UC0P01", "UC0A12", "UC0M01", "UC0C02", "UC0S01", "UC0D01", "UC0Q02", "UC0I02" -> {
                return "기타"
            }
        }
        return ""
    }


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
                removeMarker()
            }
            in "7" -> {
                super.onBackPressed()
            }
            else -> {
                super.onBackPressed()
                mainViewModel.mainStatus.value = 5
            }

        }



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
        removeMarker()
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



    // 마커 클릭 이벤트 리스터
    override fun onPOIItemSelected(map: MapView?, item : MapPOIItem?) {
        // 마커 클릭시 발생
        if (map != null && item != null && item.userObject != null) {
            mainViewModel.mainStatus.value = 5
//            if (map.currentLocationTrackingMode != MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving) {
//                map.currentLocationTrackingMode =
//                    MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
//            }

            map.setMapCenterPoint(
                MapPoint.mapPointWithGeoCoord(
                    item.mapPoint.mapPointGeoCoord.latitude,
                    item.mapPoint.mapPointGeoCoord.longitude
                ),
                true
            )
            selectedMarker = item
//            item.tag = 1
            val itemData : ServList = item.userObject as ServList


            binding.resultRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            binding.resultNmTv.text = item.itemName
            binding.resultTypeTv.text = itemData.faclTyCd?.let { it1 -> Util().changeType(it1) }
            binding.resultLocationTv.text = itemData.lcMnad

            binding.resultLayout.startAnimation(fadeInAnim)
            binding.resultLayout.visibility = View.VISIBLE
            binding.clickMarkerView.visibility = View.VISIBLE


            itemData.wfcltId?.let { mainViewModel.getEvalInfo(it, "1") }

        }
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }
    // 마커 클릭 이벤트 리스터 END


    // CurrentLocationEventListener
    override fun onCurrentLocationUpdate(mapViewP: MapView?, mapPoint: MapPoint?, p2: Float) {
        val mapPointGeo = mapPoint?.mapPointGeoCoord
        val currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo?.latitude!!, mapPointGeo?.longitude)

        //이 좌표로 지도 중심 이동
//        mapViewP?.setMapCenterPoint(currentMapPoint, true)

        //전역변수로 현재 좌표 저장
        mCurrentLat = mapPointGeo.latitude
        mCurrentLng = mapPointGeo.longitude
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {
    }

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {
        Toast.makeText(this, "현재위치 갱신 작업에 실패하였습니다.", Toast.LENGTH_SHORT).show()
        Log.d("ttest", "onCurrentLocationUpdateFailed 발생!")
    }

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {
        Log.d("ttest", "onCurrentLocationUpdateCancelled 발생!")
    }
    // CurrentLocationEventListener End


}