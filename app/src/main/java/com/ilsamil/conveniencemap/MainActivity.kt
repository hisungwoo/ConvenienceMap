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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
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

    // ActivityResultContracts ????????? ?????? ?????? ??????
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

        // ???????????? API ??????
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

            //LiveData??? ?????? ?????? ?????? View ????????????
            mainStatus.observe(this@MainActivity, Observer {
                when(it) {
                    1 -> {
                        // ?????? ?????? ??????
                        binding.mainBottomNav.visibility = View.VISIBLE
                        binding.mainSearchBtn.visibility = View.VISIBLE
                        binding.mainRefreshBtn.visibility = View.VISIBLE
                        binding.mainMylocationBtn.visibility = View.VISIBLE
                        binding.mainCategoryLl.visibility = View.VISIBLE
                        binding.mainAppTitleTv.visibility = View.VISIBLE
                        binding.mainTopVw.visibility = View.VISIBLE
                        binding.mainResultCl.visibility = View.GONE

                        // ???????????? ?????? ??????
                        if (mapView.findPOIItemByName("searchItem") != null) {
                            val searchMarker = mapView.findPOIItemByName("searchItem")[0]
                            mapView.removePOIItem(searchMarker)
                        }

                    }
                    2 -> {
                        // ???????????? ?????? ??????
                        binding.mainRefreshBtn.visibility = View.GONE
                        binding.mainMylocationBtn.visibility = View.GONE
                    }
                    3 -> {
                        // ?????? ?????? ??????
                        binding.mainBottomNav.visibility = View.GONE
                    }
                    4 -> {
                        // ???????????? ??????
                        binding.mainBottomNav.visibility = View.GONE
                        binding.mainSearchBtn.visibility = View.GONE
                        binding.mainRefreshBtn.visibility = View.GONE
                        binding.mainMylocationBtn.visibility = View.GONE
                        binding.mainResultCl.visibility = View.GONE
                    }
                    8 -> {
                        // ??? ???????????? ??????
                        binding.mainBottomNav.visibility = View.VISIBLE
                    }
                }
            })

            // ?????? ?????? ?????? ????????????
            locationLiveData.observe(this@MainActivity, Observer {
                binding.mainProgressBarCenter.visibility = View.VISIBLE
                if(it != null) {
                    fLatitude = it.latitude
                    fLongitude = it.longitude
                    markedLat = fLatitude
                    markedLng = fLongitude
                    mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(fLatitude, fLongitude), 3, true)
                } else {
                    Toast.makeText(this@MainActivity, "?????? ????????? ?????? ??? ??? ????????????", Toast.LENGTH_SHORT).show()
                    Log.d(LOG_TAG, "?????? ?????? : null")
                }
            })

            // ?????? ?????? ????????????
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

            // ???????????? ?????? ??? ??????, ??????????????? ?????? ???????????? ??????
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

            // ?????? ?????? ????????? ?????? ??? ??????, ?????? ????????? ??????????????? ??????
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
                    "?????? ??? ??????" -> customMarker.customImageResourceId = R.drawable.category_click_shop
                    "????????????" -> customMarker.customImageResourceId = R.drawable.category_click_living
                    "????????????" -> customMarker.customImageResourceId = R.drawable.category_click_education
                    "??????" -> customMarker.customImageResourceId = R.drawable.category_click_public
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


            // ?????? ???????????? ??????????????? ??????
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
                            "?????? ??? ??????" -> {
                                marker.customImageResourceId = R.drawable.category_shop_img
                                marker.customSelectedImageResourceId = R.drawable.category_click_shop
                                shopList.add(marker)
                                mainViewModel.shopServList.add(data)
                                mainViewModel.mapServList.add(data)
                                mapView.addPOIItem(marker)
                            }
                            "????????????" -> {
                                marker.customImageResourceId = R.drawable.category_living_img
                                marker.customSelectedImageResourceId = R.drawable.category_click_living
                                livingList.add(marker)
                                mainViewModel.livingServList.add(data)
                                mainViewModel.mapServList.add(data)
                                mapView.addPOIItem(marker)
                            }
                            "????????????" -> {
                                marker.customImageResourceId = R.drawable.category_education_img
                                marker.customSelectedImageResourceId = R.drawable.category_click_education
                                educationList.add(marker)
                                mainViewModel.educationServList.add(data)
                                mainViewModel.mapServList.add(data)
                                mapView.addPOIItem(marker)
                            }
                            "??????" -> {
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
            //???????????? ??????
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

            // ???????????? ??????
            mainSearchBtn.setOnClickListener{
                removeMarker()
                supportFragmentManager.beginTransaction().replace(R.id.main_const_cl, searchFragment, "search").addToBackStack(null).commit()
            }

            // ??? ?????? ?????? ??????
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

            // ??? ?????? ????????? ??????
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
                    Log.d(LOG_TAG, "?????? ????????? ?????? : $cggNm")
                    mainViewModel.mapCggNm = cggNm

                    mapView.removeAllPOIItems()
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(markedLat, markedLng), true)
                    mainViewModel.getLocationFacl(cggNm)

                } catch (e : IOException) {
                    Log.d(LOG_TAG, "???????????? ?????? : " + e.printStackTrace())
                }
            }

            //???????????? ??????
            mainResultDetailBtn.setOnClickListener{
                mainViewModel.mainStatus.value = 6
                supportFragmentManager.beginTransaction().replace(R.id.main_const_cl, detailFragment, "detail").addToBackStack(null).commit()
                val userObject = selectedMarker
                mainViewModel.detailLiveData.value = userObject
            }
        }
    }

    // ?????? ?????? ?????? ????????????
    private fun getLocationFacInfo() {
        mapView.removeAllPOIItems()
        binding.mainProgressBarCenter.visibility = View.VISIBLE
        val cggNm = mainViewModel.getLocationInfo(this)
        if (cggNm != "null") {
            Log.d(LOG_TAG, "?????? ?????? = $cggNm" )
            mainViewModel.getLocationFacl(cggNm)
        } else {
            Toast.makeText(this, "?????? ????????? ???????????? ???????????????" , Toast.LENGTH_SHORT).show()
        }
    }

    // ???????????? ??????
    private fun categoryClick(btn : Int) {
        if (btn == mainViewModel.categoryLiveData.value) {
            mainViewModel.categoryLiveData.value = 0
        } else {
            clearCategoryBtn()
            mainViewModel.categoryLiveData.value = btn
        }
    }

    // ???????????? ?????? ?????????
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

    // ??????????????? ??????
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

    // ???????????? ??? mainBottomNav ?????? ?????????
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

    // ????????? ????????? ?????? ??????
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
        //MapView??? ???????????? ??? ????????? ???????????? ????????????.
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
        //?????? ?????? ????????? ????????? ?????? ????????????.
        if (mapView.currentLocationTrackingMode != MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving) {
            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
        }
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
        //?????? ??????/?????? ????????? ????????? ?????? ????????????.
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        //???????????? ?????? ?????? ????????? ?????? ????????????.
        removeMarker()
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
        //???????????? ?????? ??? ??? ????????? ?????? ????????? ?????? ????????????.
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
        //???????????? ?????? ??? ??? ????????? ?????? ?????? ??????(long press) ????????????.
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
        //???????????? ?????? ???????????? ????????? ?????? ????????????.
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
        //???????????? ?????? ???????????? ?????? ?????? ????????????.
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
        //????????? ????????? ????????? ?????? ????????????.
    }



    // ?????? ?????? ????????? ?????????, ???????????? ?????? ??????
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
    // ?????? ?????? ????????? ????????? END


    override fun onCurrentLocationUpdate(mapViewP: MapView?, mapPoint: MapPoint?, p2: Float) {
        val mapPointGeo = mapPoint?.mapPointGeoCoord
        val currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo?.latitude!!, mapPointGeo?.longitude)

        //??????????????? ?????? ?????? ??????
        mCurrentLat = mapPointGeo.latitude
        mCurrentLng = mapPointGeo.longitude
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {
    }

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {
        Toast.makeText(this, "???????????? ?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
    }

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {
    }


}