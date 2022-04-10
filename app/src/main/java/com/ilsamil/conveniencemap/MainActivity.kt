package com.ilsamil.conveniencemap

import android.Manifest
import android.graphics.Color
import android.location.Geocoder
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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ilsamil.conveniencemap.fragments.DetailFragment
import com.ilsamil.conveniencemap.fragments.InfoFragment
import com.ilsamil.conveniencemap.fragments.ListFragment
import com.ilsamil.conveniencemap.fragments.SearchFragment
import com.ilsamil.conveniencemap.fragments.*
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
    private var markedLat : Double = 1.00
    private var markedLng : Double = 1.00

    private val util = Util()
    private lateinit var selectedMarker : ServList

    private val LOG_TAG = "cm log " + MainActivity::class.simpleName

    // ActivityResultContracts 이용한 위치 권한 체크
    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            getLocationFacInfo()
            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setContentView(binding.root)

        if (!(MapView.isMapTilePersistentCacheEnabled())) {
            MapView.setMapTilePersistentCacheEnabled(true)
        }



        // 카카오맵 API 적용
        mapView = MapView(this)
        mapView.apply {
            isHDMapTileEnabled
            setCustomCurrentLocationMarkerTrackingImage(R.drawable.current_location_marker, MapPOIItem.ImageOffset(30,30))
            setPOIItemEventListener(this@MainActivity)
            setMapViewEventListener(this@MainActivity)
            setCurrentLocationEventListener(this@MainActivity)
        }
        binding.mainMapCl.addView(mapView)

        fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_up)
        fadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_down)

        replaceFragment(binding)
        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        mainViewModel.apply {

            //LiveData를 통해 메인 화면 View 업데이트
            mainStatus.observe(this@MainActivity, Observer {
                when(it) {
                    1 -> {
                        // 기본 메인 상태
                        binding.mainBottomNav.visibility = View.VISIBLE
                        binding.mainSearchBtn.visibility = View.VISIBLE
                        binding.mainRefreshBtn.visibility = View.VISIBLE
                        binding.mainMylocationBtn.visibility = View.VISIBLE
                        binding.mainCategoryLl.visibility = View.VISIBLE
                        binding.mainAppTitleTv.visibility = View.VISIBLE
                        binding.mainTopVw.visibility = View.VISIBLE
                        binding.mainResultCl.visibility = View.GONE
                        binding.mainBottomNav.menu.findItem(R.id.menu_map).isChecked = true

                        // 검색결과 마커 제거
                        if (mapView.findPOIItemByName("searchItem") != null) {
                            val searchMarker = mapView.findPOIItemByName("searchItem")[0]
                            mapView.removePOIItem(searchMarker)
                        }

                    }
                    2 -> {
                        // 주소검색 버튼 클릭
                    binding.mainRefreshBtn.visibility = View.GONE
                    binding.mainMylocationBtn.visibility = View.GONE
                    }
                    3 -> {
                        // 검색 결과 화면
                        binding.mainBottomNav.visibility = View.GONE
                    }
                    4 -> {
                        // 목록보기 이동
                        binding.mainBottomNav.visibility = View.GONE
                        binding.mainSearchBtn.visibility = View.GONE
                        binding.mainRefreshBtn.visibility = View.GONE
                        binding.mainMylocationBtn.visibility = View.GONE
                        binding.mainResultCl.visibility = View.GONE
                    }
                    5 -> {
                        // 로케이션 마커 클릭 진행중
                    }
                    6 -> {
                        // 디테일 프레그먼트 표시
                    }
                    7 -> {
                        // 로케이션 마커 클릭
                    }
                    8 -> {
                        // 앱 정보보기 이동
                        binding.mainBottomNav.visibility = View.VISIBLE
                    }
                }
            })

            // 현재 위치 정보 업데이트
            locationLiveData.observe(this@MainActivity, Observer {
                binding.mainProgressBarCenter.visibility = View.VISIBLE
                if(it != null) {
                    fLatitude = it.latitude
                    fLongitude = it.longitude
                    markedLat = fLatitude
                    markedLng = fLongitude
                    mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(fLatitude, fLongitude), 3, true)
                } else {
                    Toast.makeText(this@MainActivity, "현재 위치를 가져 올 수 없습니다", Toast.LENGTH_SHORT).show()
                    Log.d(LOG_TAG, "현재 위치 : null")
                }
            })

            // 시설 정보 업데이트
            evalInfoLiveData.observe(this@MainActivity, Observer {
                val adapter = EvalinfoAdapter()
                binding.apply {
                    mainResultRecyclerVw.adapter = adapter
                    adapter.updateItems(it)
                    mainResultCl.startAnimation(fadeInAnim)
                    mainResultCl.visibility = View.VISIBLE
                    mainProgressBarCenter.visibility = View.GONE
                    mainProgressVw.visibility = View.GONE
                }
            })

            // 카테고리 클릭 시 호출, 카카오맵에 해당 정보들을 표시
            categoryLiveData.observe(this@MainActivity, Observer {
                binding.mainResultCl.visibility = View.GONE
                mainViewModel.mainStatus.value = 1

                if(mapView.findPOIItemByTag(1) != null) {
                    mapView.deselectPOIItem(mapView.findPOIItemByTag(1))
                }
                when(it) {
                    0 -> {
                        clearCategoryBtn()
                        for(data in shopList) mapView.addPOIItem(data)
                        for(data in livingList) mapView.addPOIItem(data)
                        for(data in educationList) mapView.addPOIItem(data)
                        for(data in hospitalList) mapView.addPOIItem(data)
                        for(data in publicList) mapView.addPOIItem(data)
                    }
                    1 -> {
                        binding.mainCategoryBtnShop.setBackgroundResource(R.drawable.button_category_click)
                        binding.mainCategoryBtnShop.setTextColor(Color.WHITE)
                        mapView.removeAllPOIItems()
                        for(data in shopList) mapView.addPOIItem(data)
                    }
                    2 -> {
                        binding.mainCategoryBtnLiving.setBackgroundResource(R.drawable.button_category_click)
                        binding.mainCategoryBtnLiving.setTextColor(Color.WHITE)
                        mapView.removeAllPOIItems()
                        for(data in livingList) mapView.addPOIItem(data)
                    }
                    3 -> {
                        binding.mainCategoryBtnEducation.setBackgroundResource(R.drawable.button_category_click)
                        binding.mainCategoryBtnEducation.setTextColor(Color.WHITE)
                        mapView.removeAllPOIItems()
                        for(data in educationList) mapView.addPOIItem(data)
                    }
                    4 -> {
                        binding.mainCategoryBtnPublic.setBackgroundResource(R.drawable.button_category_click)
                        binding.mainCategoryBtnPublic.setTextColor(Color.WHITE)
                        mapView.removeAllPOIItems()
                        for(data in publicList) mapView.addPOIItem(data)
                    }
                }
            })

            // 검색 결과 리스트 클릭 시 호출, 해당 정보를 카카오맵에 표시
            movePin.observe(this@MainActivity, Observer {
                if (mapView.currentLocationTrackingMode != MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving) {
                    mapView.currentLocationTrackingMode =
                        MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
                }
                mapView.removeAllPOIItems()
                binding.apply {
                    mainProgressBarCenter.visibility = View.VISIBLE
                    mainProgressVw.visibility = View.VISIBLE
                    mainCategoryLl.visibility = View.GONE
                    mainBottomNav.visibility = View.GONE
                    mainResultCl.visibility = View.GONE
                }
                mainViewModel.mainStatus.value = 9

                val faclLng = it.faclLng!!
                val faclLat = it.faclLat!!
                val faclNm = it.faclNm
                val faclTyCd = it.faclTyCd
                val lcMnad = it.lcMnad
                val wfcltId = it.wfcltId!!

                mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(faclLat, faclLng), 0, true)

                val customMarker = MapPOIItem()
                customMarker.itemName = "searchItem"
                customMarker.tag = 1
                customMarker.mapPoint = MapPoint.mapPointWithGeoCoord(faclLat, faclLng)
                customMarker.markerType = MapPOIItem.MarkerType.CustomImage
                customMarker.isShowCalloutBalloonOnTouch = false
                customMarker.userObject = it

                when(util.changeFaclType(faclTyCd.toString())) {
                    "음식 및 상점" -> customMarker.customImageResourceId = R.drawable.category_click_shop
                    "생활시설" -> customMarker.customImageResourceId = R.drawable.category_click_living
                    "교육시설" -> customMarker.customImageResourceId = R.drawable.category_click_education
                    "기타" -> customMarker.customImageResourceId = R.drawable.category_click_public
                }

                customMarker.isCustomImageAutoscale = true
                customMarker.setCustomImageAnchor(0.5f, 1.0f)

                mapView.addPOIItem(customMarker)

                binding.apply {
                    mainResultRecyclerVw.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.HORIZONTAL, false)
                    mainResultNmTv.text = faclNm
                    mainResultTypeTv.text = faclTyCd?.let { it1 -> util.changeFaclType(it1) }
                    mainResultLocationTv.text = lcMnad
                }

                selectedMarker = it
                mainViewModel.getEvalInfo(wfcltId, "1")
            })


            // 시설 정보들을 카카오맵에 표시
            locationFaclLiveData.observe(this@MainActivity, Observer {
                removeCategoryData()
                for(data in it) {
                    if (data.faclLat != null && data.faclLng != null) {
                        val marker = MapPOIItem()
                        marker.apply {
                            mapPoint = MapPoint.mapPointWithGeoCoord(data.faclLat, data.faclLng)
                            itemName = data.faclNm
                            userObject = data
                            markerType = MapPOIItem.MarkerType.CustomImage
                            selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                            showAnimationType = MapPOIItem.ShowAnimationType.SpringFromGround
                            isShowCalloutBalloonOnTouch = false
                            isCustomImageAutoscale = true
                            setCustomImageAnchor(0.5f, 1.0f)
                        }

                        when(util.changeFaclCategory(data.faclTyCd.toString())) {
                            "음식 및 상점" -> {
                                marker.customImageResourceId = R.drawable.category_shop_img
                                marker.customSelectedImageResourceId = R.drawable.category_click_shop
                                shopList.add(marker)
                                mainViewModel.shopServList.add(data)
                                mainViewModel.mapServList.add(data)
                                mapView.addPOIItem(marker)
                            }
                            "생활시설" -> {
                                marker.customImageResourceId = R.drawable.category_living_img
                                marker.customSelectedImageResourceId = R.drawable.category_click_living
                                livingList.add(marker)
                                mainViewModel.livingServList.add(data)
                                mainViewModel.mapServList.add(data)
                                mapView.addPOIItem(marker)
                            }
                            "교육시설" -> {
                                marker.customImageResourceId = R.drawable.category_education_img
                                marker.customSelectedImageResourceId = R.drawable.category_click_education
                                educationList.add(marker)
                                mainViewModel.educationServList.add(data)
                                mainViewModel.mapServList.add(data)
                                mapView.addPOIItem(marker)
                            }
                            "기타" -> {
                                marker.customImageResourceId = R.drawable.category_public_img
                                marker.customSelectedImageResourceId = R.drawable.category_click_public
                                publicList.add(marker)
                                mainViewModel.publicServList.add(data)
                                mainViewModel.mapServList.add(data)
                                mapView.addPOIItem(marker)
                            }
                        }
                        binding.mainProgressBarCenter.visibility = View.GONE
                        binding.mainProgressVw.visibility = View.GONE

                        binding.mainBottomNav.startAnimation(fadeInAnim)
                        binding.mainBottomNav.visibility = View.VISIBLE
                        binding.mainRefreshBtn.visibility = View.VISIBLE

                    }
                }
            })
        }

        binding.apply {
            //카테고리 클릭
            mainCategoryBtnShop.setOnClickListener {
                categoryClick(1)
            }
            mainCategoryBtnLiving.setOnClickListener{
                categoryClick(2)
            }
            mainCategoryBtnEducation.setOnClickListener {
                categoryClick(3)
            }
            mainCategoryBtnPublic.setOnClickListener {
                categoryClick(4)
            }

            // 검색버튼 클릭
            mainSearchBtn.setOnClickListener{
//            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
                removeMarker()
                supportFragmentManager.beginTransaction().replace(R.id.main_const_cl, searchFragment, "search").addToBackStack(null).commit()
            }

            // 내 위치 버튼 클릭
            mainMylocationBtn.setOnClickListener {
                if(mCurrentLat != 1.00) {
                    val currentMapPoint = MapPoint.mapPointWithGeoCoord(mCurrentLat, mCurrentLng)
                    mapView.setMapCenterPoint(currentMapPoint, true)
                } else {
                    val currentMapPoint = MapPoint.mapPointWithGeoCoord(fLatitude, fLongitude)
                    mapView.setMapCenterPoint(currentMapPoint, true)
                }
                mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
            }

            // 이 지역 재검색 클릭
            binding.mainRefreshBtn.setOnClickListener {
                binding.mainProgressBarCenter.visibility = View.VISIBLE
                binding.mainProgressVw.visibility = View.VISIBLE

                val refreshLocation = mapView.mapCenterPoint
                markedLat = refreshLocation.mapPointGeoCoord.latitude
                markedLng = refreshLocation.mapPointGeoCoord.longitude
                val geocoder = Geocoder(applicationContext)

                try {
                    val gList = geocoder.getFromLocation(markedLat, markedLng, 5)
                    val cggNm = if (gList[0].subLocality != null) {
                        gList[0].subLocality
                    } else {
                        gList[0].locality
                    }
                    Log.d(LOG_TAG, "지역 재검색 위치 : $cggNm")
                    mainViewModel.mapCggNm = cggNm

                    mapView.removeAllPOIItems()
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(markedLat, markedLng), true)
                    mainViewModel.getLocationFacl(cggNm)

                } catch (e : IOException) {
                    Log.d(LOG_TAG, "지오코드 오류 : " + e.printStackTrace())
                }
            }

            //상세보기 클릭
            mainResultDetailBtn.setOnClickListener{
                mainViewModel.mainStatus.value = 6
                supportFragmentManager.beginTransaction().replace(R.id.main_const_cl, detailFragment, "detail").addToBackStack(null).commit()
                val userObject = selectedMarker
                mainViewModel.detailLiveData.value = userObject
            }
        }
    }

    // 현재 위치 정보 가져오기
    private fun getLocationFacInfo() {
        mapView.removeAllPOIItems()
        binding.mainProgressBarCenter.visibility = View.VISIBLE
        val cggNm = mainViewModel.getLocationInfo(this)
        if (cggNm != "null") {
            Log.d(LOG_TAG, "현재 위치 = $cggNm" )
            mainViewModel.getLocationFacl(cggNm)
        } else {
            Toast.makeText(this, "현재 위치를 가져오지 못했습니다" , Toast.LENGTH_SHORT).show()
        }
    }

    // 카테고리 클릭
    private fun categoryClick(btn : Int) {
        if (btn == mainViewModel.categoryLiveData.value) {
            mainViewModel.categoryLiveData.value = 0
        } else {
            clearCategoryBtn()
            mainViewModel.categoryLiveData.value = btn
        }
    }

    // 카테고리 클릭 초기화
    private fun clearCategoryBtn() {
        binding.mainProgressVw.visibility = View.GONE
        binding.mainCategoryBtnShop.setBackgroundResource(R.color.button_transparency)
        binding.mainCategoryBtnLiving.setBackgroundResource(R.color.button_transparency)
        binding.mainCategoryBtnEducation.setBackgroundResource(R.color.button_transparency)
        binding.mainCategoryBtnPublic.setBackgroundResource(R.color.button_transparency)
        binding.mainCategoryBtnShop.setTextColor(ContextCompat.getColor(this, R.color.category_text))
        binding.mainCategoryBtnLiving.setTextColor(ContextCompat.getColor(this, R.color.category_text))
        binding.mainCategoryBtnEducation.setTextColor(ContextCompat.getColor(this, R.color.category_text))
        binding.mainCategoryBtnPublic.setTextColor(ContextCompat.getColor(this, R.color.category_text))
    }

    // 프래그먼트 변경
    private fun replaceFragment(binding: ActivityMainBinding) {
        binding.mainBottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.menu_list -> {
                    supportFragmentManager.popBackStackImmediate("list", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, listFragment, "list").addToBackStack("list").commit()
                }
                R.id.menu_map -> {
                    bottomClickMap()
                }
                R.id.menu_info -> {
                    supportFragmentManager.popBackStackImmediate("info", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, infoFragment, "info").addToBackStack("info").commit()
                }
            }
            true
        }
    }

    // 뒤로가기 시 mainBottomNav 클릭 활성화
    private fun updateBottomMenu() {
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag("list")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("info")

        binding.mainBottomNav.apply {
            if(tag1 != null && tag1.isVisible) {
                this.menu.findItem(R.id.menu_list).isChecked = true
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
        if(mainViewModel.mainStatus.value == 2) {
            mainViewModel.mainStatus.value = 1
        } else if (mainViewModel.mainStatus.value == 3) {
            mainViewModel.mainStatus.value = 2
        }
    }

    // 지도에 표시된 마커 제거
    private fun removeMarker() {
        if(mainViewModel.mainStatus.value == 7) {
            if(mapView.findPOIItemByTag(1) != null) {
                val searchMarker = mapView.findPOIItemByTag(1)
                mapView.deselectPOIItem(searchMarker)
                searchMarker.tag = 0
            }

            binding.mainResultCl.visibility = View.GONE
            binding.mainProgressVw.visibility = View.GONE
            mainViewModel.mainStatus.value = 1
        }
    }

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

    private fun removeCategoryData() {
        shopList.clear()
        livingList.clear()
        educationList.clear()
        publicList.clear()
        mainViewModel.shopServList.clear()
        mainViewModel.livingServList.clear()
        mainViewModel.educationServList.clear()
        mainViewModel.publicServList.clear()
        mainViewModel.mapServList.clear()
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
            in "4", "8" -> {
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
                removeMarker()
            }
            else -> {
                super.onBackPressed()
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



    // 마커 클릭 이벤트 리스터, 카카오맵 마커 클릭
    override fun onPOIItemSelected(map: MapView?, item : MapPOIItem?) {
        if (map != null && item != null && item.userObject != null
            && mainViewModel.mainStatus.value != 5 && mainViewModel.mainStatus.value != 9) {
            mainViewModel.mainStatus.value = 5
            binding.mainResultCl.visibility = View.GONE
            binding.mainProgressBarCenter.visibility = View.VISIBLE
            binding.mainProgressVw.visibility = View.VISIBLE

            if (map.currentLocationTrackingMode != MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving) {
                map.currentLocationTrackingMode =
                    MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
            }

            map.setMapCenterPoint(
                MapPoint.mapPointWithGeoCoord(
                    item.mapPoint.mapPointGeoCoord.latitude,
                    item.mapPoint.mapPointGeoCoord.longitude
                ),
                true
            )
            val itemData : ServList = item.userObject as ServList
            selectedMarker = itemData

            binding.apply {
                mainResultRecyclerVw.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.HORIZONTAL, false)
                mainResultNmTv.text = item.itemName
                mainResultTypeTv.text = itemData.faclTyCd?.let { it1 -> util.changeFaclType(it1) }
                mainResultLocationTv.text = itemData.lcMnad

                mainRefreshBtn.visibility = View.GONE
                mainBottomNav.visibility = View.GONE
                mainMylocationBtn.visibility = View.GONE
            }

            mainViewModel.mainStatus.value = 7
            mainViewModel.isSelectMarker = true

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
    }

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {
    }


}