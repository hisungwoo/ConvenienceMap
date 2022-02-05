package com.ilsamil.conveniencemap.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ilsamil.conveniencemap.MainViewModel
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.adapters.EvalinfoAdapter
import com.ilsamil.conveniencemap.databinding.FragmentMapBinding
import com.ilsamil.conveniencemap.utils.Util
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapFragment : Fragment() {
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentMapBinding
    private lateinit var searchFragment : SearchFragment
    private lateinit var mapView: MapView
    lateinit var fadeInAnim : Animation
    private lateinit var callback: OnBackPressedCallback

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

        val adapter = EvalinfoAdapter()
        binding.resultRecyclerView.adapter = adapter
        mainViewModel.evalInfoLiveData.observe(this, androidx.lifecycle.Observer {
            adapter.updateItems(it)
        })

        setFragmentResultListener("movePin") { requestKey, bundle ->
            mainViewModel.mainStatus.value = 3
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

            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(faclLat, faclLng), 0, true);

            val customMarker = MapPOIItem()
            customMarker.itemName = "테스트 마커"
            customMarker.tag = 1
            customMarker.mapPoint = MapPoint.mapPointWithGeoCoord(faclLat, faclLng)
            customMarker.markerType = MapPOIItem.MarkerType.CustomImage
            customMarker.customImageResourceId = R.drawable.ic_location_pin_click_center
            customMarker.isCustomImageAutoscale = false
            customMarker.setCustomImageAnchor(0.5f, 1.0f)

            mapView.addPOIItem(customMarker)

            binding.resultRecyclerView.layoutManager = LinearLayoutManager(container?.context, RecyclerView.HORIZONTAL, false)
            mainViewModel.getEvalInfo(wfcltId, "1")

            binding.resultNmTv.text = faclNm
            binding.resultTypeTv.text = faclTyCd?.let { Util().changeType(it) }
            binding.resultLocationTv.text = lcMnad

            binding.resultLayout.startAnimation(fadeInAnim)
            binding.resultLayout.visibility = View.VISIBLE

        }



        binding.searchBtn.setOnClickListener{
            searchFragment = SearchFragment.newInstance()
            activity?.supportFragmentManager?.beginTransaction()?.add(R.id.fragment_view, searchFragment, "search")?.addToBackStack("search")?.commit()
        }


        return binding.root
    }
}