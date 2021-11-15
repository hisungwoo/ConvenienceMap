package com.ilsamil.conveniencemap.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.databinding.FragmentMapBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private lateinit var searchFragment: SearchFragment
    private lateinit var mapView: MapView

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


        binding.aatest.setOnClickListener {
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.48942886437432, 126.85835706507918), 2, true);

            val customMarker = MapPOIItem()
            customMarker.itemName = "테스트 마커"
            customMarker.tag = 1
            customMarker.mapPoint = MapPoint.mapPointWithGeoCoord(37.48942886437432, 126.85835706507918)
            customMarker.markerType = MapPOIItem.MarkerType.CustomImage  // 기본으로 제공하는 BluePin 마커 모양.
            customMarker.customImageResourceId = R.drawable.ic_location_pin
            customMarker.isCustomImageAutoscale = false
            customMarker.setCustomImageAnchor(0.5f, 1.0f)


            mapView.addPOIItem(customMarker);


        }



        binding.clKakaoMapView.addView(mapView)

        binding.searchBtn.setOnClickListener{
            searchFragment = SearchFragment.newInstance()
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_view, searchFragment)?.addToBackStack(null)?.commit()
        }


        return binding.root
    }
}