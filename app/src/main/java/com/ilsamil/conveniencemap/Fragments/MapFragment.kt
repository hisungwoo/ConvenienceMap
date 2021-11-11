package com.ilsamil.conveniencemap.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.FragmentManager
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.databinding.ActivityMainBinding
import com.ilsamil.conveniencemap.databinding.FragmentMapBinding
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
        binding.clKakaoMapView.addView(mapView)


        binding.searchBtn.setOnClickListener{
            searchFragment = SearchFragment.newInstance()
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_view, searchFragment)?.addToBackStack(null)?.commit()
        }


        return binding.root
    }
}