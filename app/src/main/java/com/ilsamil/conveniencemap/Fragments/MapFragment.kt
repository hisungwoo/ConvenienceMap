package com.ilsamil.conveniencemap.Fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ilsamil.conveniencemap.MainViewModel
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.adapters.EvalinfoAdapter
import com.ilsamil.conveniencemap.databinding.ActivityMainBinding
import com.ilsamil.conveniencemap.databinding.FragmentMapBinding
import com.ilsamil.conveniencemap.model.EvalInfoList
import com.ilsamil.conveniencemap.model.FacInfoList
import com.ilsamil.conveniencemap.repository.RetrofitService
import com.ilsamil.conveniencemap.utils.ChangeType
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.*

class MapFragment : Fragment() {
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentMapBinding
    private lateinit var searchFragment : SearchFragment
    private lateinit var mapView: MapView
    lateinit var fadeInAnim : Animation

    companion object {
        fun newInstance() : MapFragment {
            return MapFragment()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = MapView(activity)
        binding.clKakaoMapView.addView(mapView)

        if(mainViewModel.bottomNavLiveData.value != true)
            mainViewModel.bottomNavLiveData.value = true

        setFragmentResultListener("movePin") { requestKey, bundle ->
            mainViewModel.bottomNavLiveData.value = false
            val faclLng = bundle.getDouble("faclLng")
            val faclLat = bundle.getDouble("faclLat")
            val faclNm = bundle.getString("faclNm")
            val faclTyCd = bundle.getString("faclTyCd")
            val lcMnad = bundle.getString("lcMnad")
            val wfcltId = bundle.getString("wfcltId")!!

            fadeInAnim = AnimationUtils.loadAnimation(container?.context, R.anim.bottom_up)

            Log.d("ttest" , "faclLng " + faclLng)
            Log.d("ttest" , "faclLat " + faclLat)
            Log.d("ttest" , "faclNm " + faclNm)

            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(faclLng, faclLat), 0, true);

            val customMarker = MapPOIItem()
            customMarker.itemName = "테스트 마커"
            customMarker.tag = 1
            customMarker.mapPoint = MapPoint.mapPointWithGeoCoord(faclLng, faclLat)
            customMarker.markerType = MapPOIItem.MarkerType.CustomImage
            customMarker.customImageResourceId = R.drawable.ic_location_pin_50_2
            customMarker.isCustomImageAutoscale = false
            customMarker.setCustomImageAnchor(0.5f, 1.0f)

            mapView.addPOIItem(customMarker)

            binding.resultRecyclerView.layoutManager = LinearLayoutManager(container?.context, RecyclerView.HORIZONTAL, false)
            mainViewModel.getEvalInfo(wfcltId)

            binding.resultNmTv.text = faclNm
            binding.resultTypeTv.text = faclTyCd?.let { ChangeType().changeType(it) }
            binding.resultLocationTv.text = lcMnad

            binding.resultLayout.startAnimation(fadeInAnim)
            binding.resultLayout.visibility = View.VISIBLE
        }


        val adapter = EvalinfoAdapter()
        binding.resultRecyclerView.adapter = adapter
        mainViewModel.evalInfoLiveData.observe(this, androidx.lifecycle.Observer {
            adapter.updateItems(it)
        })


        binding.searchBtn.setOnClickListener{
            Log.d("ttest" , "클릭")
            searchFragment = SearchFragment.newInstance()

            mainViewModel.movemove.value = 2
//            activity?.supportFragmentManager?.beginTransaction()?.add(R.id.fragment_view, searchFragment)?.addToBackStack(null)?.commit()
        }


        return binding.root
    }
}